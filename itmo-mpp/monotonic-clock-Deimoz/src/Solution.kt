/**
 * В теле класса решения разрешено использовать только переменные делегированные в класс RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author :TODO: Virtsev Daniil
 */
class Solution : MonotonicClock {
    private var h1 by RegularInt(0)
    private var m1 by RegularInt(0)
    private var h2 by RegularInt(0)
    private var m2 by RegularInt(0)
    private var s by RegularInt(0)

    override fun write(time: Time) {
        // write right-to-left
        h2 = time.d1
        m2 = time.d2
        s = time.d3
        m1 = time.d2
        h1 = time.d1
    }

    override fun read(): Time {
        // read left-to-right
        val h1Temp = h1
        val m1Temp = m1
        val sTemp = s
        val m2Temp = m2
        val h2Temp = h2
        if (h1Temp == h2Temp) {
            if (m1Temp == m2Temp) {
                return Time(h2Temp, m2Temp, sTemp)
            }
            return Time(h2Temp, m2Temp, 0)
        }
        return Time(h2Temp, 0, 0)
    }
}