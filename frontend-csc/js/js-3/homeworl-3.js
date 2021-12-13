'use strict';

class MyTime {
    constructor(time) {
        let sTime = time.split("+")
        let time0 = sTime[0].split(":")
        this.plus = parseInt(sTime[1])
        this.hours = parseInt(time0[0])
        this.minutes = parseInt(time0[1])
        this.inMinutes = (this.hours - this.plus) * 60 + this.minutes
    }

    recalc() {
        this.inMinutes = (this.hours - this.plus) * 60 + this.minutes
    }
}

class MyDate {
    constructor(date) {
        let sDate = date.split(" ")
        switch (sDate[0]) {
            case "ПН":
                this.day = 0;
                break;
            case "ВТ":
                this.day = 1;
                break;
            case "СР":
                this.day = 2;
                break;
            case "ЧТ":
                this.day = 3;
                break;
            case "ПТ":
                this.day = 4;
                break;
            case "СБ":
                this.day = 5;
                break;
            case "ВС":
                this.day = 6;
                break;
        }
        this.time = new MyTime(sDate[1])
        this.inMinutes = this.time.inMinutes + this.day * 60 * 24
    }

    addMinutes(minutes) {
        this.time.minutes = (this.time.minutes + minutes) % 60
        if (this.time.minutes + minutes >= 60) {
            this.time.hours = (this.time.hours + 1) % 24
            if (this.time.hours + 1 >= 24) {
                this.day = (this.day + 1) % 7
            }
        }
    }

    addHours(hours) {
        if (this.time.hours + hours >= 24) {
            this.day = (this.day + 1) % 7
        }
        this.time.hours = (this.time.hours + hours) % 24
    }

    subHours(hours) {
        this.time.hours -= hours
        if (this.time.hours < 0) {
            this.time.hours += 24
            this.day -= 1
            if (this.day < 0) {
                this.day += 7
            }
        }
    }

    recalc() {
        this.time.inMinutes = (this.time.hours - this.time.plus) * 60 + this.time.minutes
        this.inMinutes = this.time.inMinutes + this.day * 60 * 24
    }
}

function fromMinutes(minutes) {
    let day = Math.floor(minutes / (24 * 60))
    minutes = minutes % (24 * 60)
    let hours = Math.floor(minutes / 60)
    minutes = minutes % 60
    let res = new MyDate("ПН 00:00+0")
    res.day = day
    res.time.minutes = minutes
    res.time.hours = hours
    res.recalc()
    return res
}

/**
 * @param {Object} schedule Расписание Банды
 * @param {number} duration Время на ограбление в минутах
 * @param {Object} workingHours Время работы банка
 * @param {string} workingHours.from Время открытия, например, "10:00+5"
 * @param {string} workingHours.to Время закрытия, например, "18:00+5"
 * @returns {Object}
 */
function getAppropriateMoment(schedule, duration, workingHours) {
    let newSchedule = []
    let maxPlus = 0
    Object.values(schedule).forEach(coolGuy => {
        coolGuy.forEach(ownSchedule => {
            let temp = {
                from: MyDate,
                to: MyDate
            }
            temp.from = new MyDate(ownSchedule.from)
            temp.to = new MyDate(ownSchedule.to)
            maxPlus = Math.max(maxPlus, temp.from.time.plus)
            newSchedule.push(temp)
        })
    })

    let hours = {
        from: MyDate,
        to: MyDate
    }
    hours.from = new MyTime(workingHours.from)
    hours.to = new MyTime(workingHours.to)
    maxPlus = Math.max(maxPlus, hours.from.plus)

    newSchedule.forEach(ownSchedule => {
        ownSchedule.from.time.plus -= maxPlus
        ownSchedule.to.time.plus -= maxPlus
        ownSchedule.from.recalc()
        ownSchedule.to.recalc()
    })

    hours.from.plus -= maxPlus
    hours.to.plus -= maxPlus
    hours.from.recalc()
    hours.to.recalc()

    let minutes = Array.from({length: 24 * 60 * 3 + 1}, (_, i) => true)

    newSchedule.forEach(ownSchedule => {
        let start = ownSchedule.from.inMinutes
        let end = ownSchedule.to.inMinutes
        for (let i = start; i < end; i++) {
            minutes[i] = false
        }
    })

    for (let i = 0; i < hours.from.inMinutes; i++) {
        for (let j = 0; j < 7; j++) {
            minutes[i + j * 24 * 60] = false
        }
    }

    for (let i = hours.to.inMinutes; i < 24 * 60; i++) {
        for (let j = 0; j < 7; j++) {
            minutes[i + j * 24 * 60] = false
        }
    }

    let found = false
    let startPoint = 0

    while (!found && startPoint < minutes.length - duration && startPoint < 24 * 60 * 3 - hours.from.plus * 60) {
        let count = 0
        while (minutes[startPoint + count] && startPoint + count < 24 * 60 * 3 - hours.from.plus * 60) {
            count++
        }
        if (count >= duration) {
            found = true
            break
        }
        if (count === 0) {
            while (!minutes[startPoint] && startPoint < minutes.length) {
                startPoint++
            }
        }
        startPoint += count
    }


    return {
        /**
         * Найдено ли время
         * @returns {boolean}
         */
        exists() {
            return found;
        },

        /**
         * Возвращает отформатированную строку с часами
         * для ограбления во временной зоне банка
         *
         * @param {string} template
         * @returns {string}
         *
         * @example
         * ```js
         * getAppropriateMoment(...).format('Начинаем в %HH:%MM (%DD)') // => Начинаем в 14:59 (СР)
         * ```
         */
        format(template) {
            if (!found) {
                return ""
            }
            let dif = hours.from.plus
            let res = fromMinutes(startPoint)
            if (dif > 0) {
                res.addHours(dif)
            } else {
                res.subHours(-dif)
            }
            let h = res.time.hours.toString()
            if (res.time.hours < 10) {
                h = '0' + h
            }
            template = template.replace(/%HH/, h)
            let m = res.time.minutes.toString()
            if (res.time.minutes < 10) {
                m = '0' + m
            }
            template = template.replace(/%MM/, m)
            let day
            switch (res.day) {
                case 0:
                    day = "ПН";
                    break;
                case 1:
                    day = "ВТ";
                    break;
                case 2:
                    day = "СР";
                    break;
                case 3:
                    day = "ЧТ";
                    break;
                case 4:
                    day = "ПТ";
                    break;
                case 5:
                    day = "СБ";
                    break;
                case 6:
                    day = "ВС";
                    break;
            }
            template = template.replace(/%DD/, day)
            return template;
        },

        /**
         * Попробовать найти часы для ограбления позже [*]
         * @note Не забудь при реализации выставить флаг `isExtraTaskSolved`
         * @returns {boolean}
         */
        tryLater() {
            if (!found) {
                return false
            }
            let nextTry = startPoint + 30
            let newFound = false
            while (!newFound && nextTry < minutes.length - duration) {
                let count = 0
                while (minutes[nextTry + count]) {
                    count++
                }
                if (count >= duration) {
                    newFound = true
                    break
                }
                nextTry += count + 1
            }
            if (newFound) {
                startPoint = nextTry
            }
            return newFound;
        }
    };
}

module.exports = {
    getAppropriateMoment
};