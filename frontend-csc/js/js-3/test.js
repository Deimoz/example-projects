'use strict';

const robbery = require('./homeworl-3');

const gangSchedule = {
    Danny: [{ from: 'ПН 12:00+20', to: 'ПН 17:00+20' }, { from: 'ВТ 13:00+20', to: 'ВТ 16:00+20' }],
    Rusty: [{ from: 'ПН 11:30+15', to: 'ПН 16:30+15' }, { from: 'ВТ 13:00+15', to: 'ВТ 16:00+15' }],
    Linus: [
        { from: 'ПН 09:00+12', to: 'ПН 14:00+12' },
        { from: 'ПН 21:00+12', to: 'ВТ 09:30+12' },
        { from: 'СР 09:30+12', to: 'СР 15:00+12' }
    ]
};

const bankWorkingHours = {
    from: '00:00+15',
    to: '00:01+15'
};

// Время не существует
const longMoment = robbery.getAppropriateMoment(gangSchedule, 1, bankWorkingHours);

// Выведется `false` и `""`
console.info(longMoment.exists());
console.info(longMoment.format('Метим на %DD, старт в %HH:%MM!'));

// Время существует
const moment = robbery.getAppropriateMoment(gangSchedule, 90, bankWorkingHours);

// Выведется `true` и `"Метим на ВТ, старт в 11:30!"`
console.info(moment.exists());
console.info(moment.format('Метим на %DD, старт в %HH:%MM!'));

// Дополнительное задание
// Вернет `true`
moment.tryLater();
// `"ВТ 16:00"`
console.info(moment.format('%DD %HH:%MM'));

// Вернет `true`
moment.tryLater();
// `"ВТ 16:30"`
console.info(moment.format('%DD %HH:%MM'));

// Вернет `true`
moment.tryLater();
// `"СР 10:00"`
console.info(moment.format('%DD %HH:%MM'));

// Вернет `false`
moment.tryLater();
// `"СР 10:00"`
console.info(moment.format('%DD %HH:%MM'));