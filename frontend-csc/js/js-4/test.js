'use strict';

const { getEmitter } = require('./emitter');

// Пример работы дополнительного задания
let students = {
    Sam: {
        focus: 100,
        wisdom: 50
    },
    Bill: {
        focus: 90,
        wisdom: 50
    }
};

let lecturer = getEmitter()
    .several(
        'begin',
        students.Sam,
        function () {
            this.focus += 10;
        },
        1
    )
    .several(
        'begin',
        students.Bill,
        function () {
            this.focus += 10;
            this.wisdom += 5;
        },
        1
    )
    // На Сэма действуют только нечетные слайды
    .through(
        'slide',
        students.Sam,
        function () {
            this.wisdom += Math.round(this.focus * 0.1);
            this.focus -= 10;
        },
        2
    )
    // Концентрации Билла хватит ровно на 4 слайда
    .several(
        'slide',
        students.Bill,
        function () {
            this.wisdom += Math.round(this.focus * 0.05);
            this.focus -= 10;
        },
        4
    )
    .on('slide.funny', students.Sam, function () {
        this.focus += 5;
        this.wisdom -= 10;
    })
    .on('slide.funny', students.Bill, function () {
        this.focus += 5;
        this.wisdom -= 10;
    });

lecturer.emit('begin');
// Sam(110,50); Bill(100,55)
console.log(students)

lecturer
    .emit('slide.text')
    .emit('slide.text')
    .emit('slide.text')
    .emit('slide.funny');
// Sam(95,61); Bill(65,63)
console.log(students)

lecturer
    .emit('slide.text')
    .emit('slide.text')
    .emit('slide.funny');
// Sam(80,70); Bill(70,53)
console.log(students)