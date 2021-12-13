/**
 * @author Virtsev Daniil
 */
class Solution : AtomicCounter {
    // объявите здесь нужные вам поля
    private val root: Node = Node(0, 0)
    private val last: ThreadLocal<Node> = ThreadLocal.withInitial { root }

    override fun getAndAdd(x: Int): Int {
        var res = 0
        var node = Node(0, x)
        while (last.get() != node) {
            val prev = last.get()
            node = Node(node.seq + 1, prev.arg + x, node.next)
            last.set(prev.next.decide(node))
            res = prev.arg
        }
        return res
    }

    // вам наверняка потребуется дополнительный класс
    private class Node(val seq: Int = 0, val arg: Int, val next: Consensus<Node> = Consensus())
}
