/**
 * Возвращает новый emitter
 * @returns {Object}
 */
function getEmitter() {
    let events = new Map()
    return {

        /**
         * Подписаться на событие
         * @param {String} event
         * @param {Object} context
         * @param {Function} handler
         */
        on: function (event, context, handler) {
            if (!events.has(event)) {
                events.set(event, new Map())
            }
            if (!events.get(event).has(context)) {
                events.get(event).set(context, [])
            }
            events.get(event).get(context).push({
                handler: handler,
                count: 0,
                special: 0,
                isSeveral: false,
                isThrough: false
            })
            return this
        },

        /**
         * Отписаться от события
         * @param {String} event
         * @param {Object} context
         */
        off: function (event, context) {
            events.forEach((_, key) => {
                if (key === event ||
                    key.startsWith(event + ".")) {
                    events.get(key).delete(context)
                }
            })
            return this
        },

        /**
         * Уведомить о событии
         * @param {String} event
         */
        emit: function (event) {
            while (true) {
                if (events.has(event)) {
                    events.get(event).forEach((handlers, context) => {
                        handlers.forEach(handler => {
                            if (handler.isSeveral && handler.count < handler.special ||
                                handler.isThrough && handler.count % handler.special === 0 ||
                                !handler.isThrough && !handler.isSeveral) {
                                handler.handler.call(context)
                            }
                            handler.count++
                        })
                    })
                }
                let dot = event.lastIndexOf('.')
                if (dot === -1) {
                    break
                }
                event = event.slice(0, dot)
            }
            return this
        },

        /**
         * Подписаться на событие с ограничением по количеству полученных уведомлений
         * @star
         * @param {String} event
         * @param {Object} context
         * @param {Function} handler
         * @param {Number} times – сколько раз получить уведомление
         */
        several: function (event, context, handler, times) {
            if (!events.has(event)) {
                events.set(event, new Map())
            }
            if (!events.get(event).has(context)) {
                events.get(event).set(context, [])
            }
            events.get(event).get(context).push({
                handler: handler,
                count: 0,
                special: times,
                isSeveral: true,
                isThrough: false
            })
            return this
        },

        /**
         * Подписаться на событие с ограничением по частоте получения уведомлений
         * @star
         * @param {String} event
         * @param {Object} context
         * @param {Function} handler
         * @param {Number} frequency – как часто уведомлять
         */
        through: function (event, context, handler, frequency) {
            if (!events.has(event)) {
                events.set(event, new Map())
            }
            if (!events.get(event).has(context)) {
                events.get(event).set(context, [])
            }
            events.get(event).get(context).push({
                handler: handler,
                count: 0,
                special: frequency,
                isSeveral: false,
                isThrough: true
            })
            return this
        }
    };
}

module.exports = {
    getEmitter
};