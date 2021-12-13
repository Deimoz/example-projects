'use strict';

function createOrder(friends, limit) {
    const comparator = (first, second) => first.name.localeCompare(second.name);
    let currLevel = limit;
    if (typeof limit === "undefined") {
        currLevel = -1
    }
    let currCircle = friends.filter(friend => friend.best).sort(comparator);
    let used = new Set();
    let result = [];
    while (currCircle.length > 0 && (typeof limit === "undefined" || currLevel --> 0)) {
        let nextCircle = new Set();
        currCircle.forEach(friend => {
            if (!used.has(friend.name)) {
                result.push(friend)
                used.add(friend.name)
            }
        })
        currCircle.forEach(friend => {
            friend.friends.forEach(nextFriend => {
                nextCircle.add(nextFriend)
            })
        })
        currCircle = friends.filter(friend => !used.has(friend.name) && nextCircle.has(friend.name))
            .sort(comparator);
    }
    return result;
}

/**
 * Итератор по друзьям
 * @constructor
 * @param {Object[]} friends
 * @param {Filter} filter
 */
function Iterator(friends, filter) {
    this.friendsQueue = createOrder(friends, this.limit).filter(filter.validate);
}

Iterator.prototype.done = function () {
    return !this.friendsQueue.length;
}

Iterator.prototype.next = function () {
    return (this.done() ? null : this.friendsQueue.shift());
}

/**
 * Итератор по друзям с ограничением по кругу
 * @extends Iterator
 * @constructor
 * @param {Object[]} friends
 * @param {Filter} filter
 * @param {Number} maxLevel – максимальный круг друзей
 */
function LimitedIterator(friends, filter, maxLevel) {
    this.limit = maxLevel;
    Iterator.call(this, friends, filter);
}

Object.setPrototypeOf(LimitedIterator.prototype, Iterator.prototype);

/**
 * Фильтр друзей
 * @constructor
 */
function Filter() {
    this.validate = (() => true);
}

/**
 * Фильтр друзей
 * @extends Filter
 * @constructor
 */
function MaleFilter() {
    this.validate = (friend => friend.gender === 'male');
}

Object.setPrototypeOf(MaleFilter.prototype, Filter.prototype);

/**
 * Фильтр друзей-девушек
 * @extends Filter
 * @constructor
 */
function FemaleFilter() {
    this.validate = (friend => friend.gender === 'female');
}

Object.setPrototypeOf(FemaleFilter.prototype, Filter.prototype);

exports.Iterator = Iterator;
exports.LimitedIterator = LimitedIterator;

exports.Filter = Filter;
exports.MaleFilter = MaleFilter;
exports.FemaleFilter = FemaleFilter;