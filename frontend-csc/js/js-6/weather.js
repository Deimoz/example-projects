'use strict';

const fetch = require('node-fetch');

const API_KEY = require('./key.json');

/**
 * @typedef {object} TripItem Город, который является частью маршрута.
 * @property {number} geoid Идентификатор города
 * @property {number} day Порядковое число дня маршрута
 */

class TripBuilder {

  constructor(geoIds) {
    this.geoIds = geoIds;
    this.plan = [];
    this.maxDays = 7;
  }

  getDaysOfGeiId(geoId) {
    return fetch(`https://api.weather.yandex.ru/v2/forecast?hours=false&limit=7&geoid=${geoId}`, {
      headers: {
        'X-Yandex-API-Key': API_KEY.key
      }
    }).then(response => response.json())
  }

  /**
   * Метод, добавляющий условие наличия в маршруте
   * указанного количества солнечных дней
   * Согласно API Яндекс.Погоды, к солнечным дням
   * можно приравнять следующие значения `condition`:
   * * `clear`;
   * * `partly-cloudy`.
   * @param {number} daysCount количество дней
   * @returns {object} Объект планировщика маршрута
   */
  sunny(daysCount) {
    for (let i = 0; i < daysCount; i++) {
      this.plan.push("sunny")
    }
    return this;
  }

  /**
   * Метод, добавляющий условие наличия в маршруте
   * указанного количества пасмурных дней
   * Согласно API Яндекс.Погоды, к солнечным дням
   * можно приравнять следующие значения `condition`:
   * * `cloudy`;
   * * `overcast`.
   * @param {number} daysCount количество дней
   * @returns {object} Объект планировщика маршрута
   */
  cloudy(daysCount) {
    for (let i = 0; i < daysCount; i++) {
      this.plan.push("cloudy")
    }
    return this;
  }

  /**
   * Метод, добавляющий условие максимального количества дней.
   * @param {number} daysCount количество дней
   * @returns {object} Объект планировщика маршрута
   */
  max(daysCount) {
    this.maxDays = daysCount
    return this;
  }

  /**
   * Метод, возвращающий Promise с планируемым маршрутом.
   * @returns {Promise<TripItem[]>} Список городов маршрута
   */
  build() {
    return new Promise((resolve, reject) => {
      if (this.plan.length > 7) {
        reject(new Error('Не могу построить маршрут!'));
        return
      }
      this.weatherByDays = []
      for (let i = 0; i < this.plan.length; i++) {
        this.weatherByDays.push(new Set())
      }

      Promise.all(this.geoIds.map(id => this.getDaysOfGeiId(id)))
        .then(data => {
          data.forEach(elem => {
            let id = elem["info"]["geoid"]
            let forecasts = elem["forecasts"]
            for (let i = 0; i < this.plan.length; i++) {
              let condition = forecasts[i]['parts']['day_short']['condition']
              if (this.plan[i] === "sunny" && (condition === "clear" || condition === "partly-cloudy")) {
                this.weatherByDays[i].add(id)
              } else if (this.plan[i] === "cloudy" && (condition === "cloudy" || condition === "overcast")) {
                this.weatherByDays[i].add(id)
              }
            }
          })

          let plan = this.dayCheck(0, [], new Map())

          if (plan.length < this.plan.length) {
            reject(new Error('Не могу построить маршрут!'));
            return
          }
          let res = []
          for (let i = 0; i < plan.length; i++) {
            res.push({
              geoid: plan[i],
              day: i + 1
            })
          }
          resolve(res)
        })
    })
  }

  dayCheck(day, plan, used) {
    if (day === this.plan.length) {
      return plan
    }
    let lastDay = plan[day - 1]
    if (used.get(lastDay) === this.maxDays || !this.weatherByDays[day].has(lastDay)) {
      let iter = this.weatherByDays[day].values()
      for (let i = 0; i < this.weatherByDays[day].size; i++) {
        let id = iter.next().value
        if (!used.has(id)) {
          let newUsed = new Map(used)
          newUsed.set(id, 1)
          let newPlan = this.dayCheck(day + 1, plan.concat([id]), newUsed)
          if (newPlan.length === this.plan.length) {
            return newPlan
          }
        }
      }
      return plan
    }
    used.set(lastDay, used.get(lastDay) + 1)
    plan.push(lastDay)
    return this.dayCheck(day + 1, plan, used)
  }
}

/**
 * Фабрика для получения планировщика маршрута.
 * Принимает на вход список идентификаторов городов, а
 * возвращает планировщик маршрута по данным городам.
 *
 * @param {number[]} geoids Список идентификаторов городов
 * @returns {TripBuilder} Объект планировщика маршрута
 * @see https://yandex.ru/dev/xml/doc/dg/reference/regions-docpage/
 */
function planTrip(geoids) {
  return new TripBuilder(geoids);
}

module.exports = {
  planTrip
};
