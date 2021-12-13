abstract class IntPair2(var valueX: Int, var valueY: Int) {

    private constructor() : this(-1, -1)

    open fun sum(): Int = valueX + valueY

    fun prod(): Int = valueX * valueY

    abstract fun gcd(): Int
}

class DerivedIntPair2(x: Int, y: Int) : IntPair2(x, y) {
    override fun gcd(): Int {
        var a = valueX
        var b = valueY
        while (a != 0 && b != 0) {
            if (a > b) {
                a %= b
            } else {
                b %= a
            }
        }
        return a + b
    }
}