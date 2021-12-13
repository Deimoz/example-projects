interface MessagingService {
/* ... */
    /**
     * Smart cast не может быть произведен, так как функции данных свойств могут быть
     * переопределены в потомках, из-за чего тип их возвращаемого значения не гарантирован
     * При использовании !! мы вынуждаем компилятор не обращать на это внимания и
     * можем подвергнуться проблемам с типом в будущем
     */

    val serviceVisitorsStats: ServiceVisitorsStats?
    val loggingService: LoggingService?

    fun handleRequest(clientId: String) {
        loggingService?.let { logServ ->
            logServ.log("Request from $clientId")

            serviceVisitorsStats?.let { visitStats ->
                visitStats.registerVisit(clientId)

                visitStats.visitorsCounter?.let { visCount ->
                    logServ.log("Visitors count: ${visCount.uniqueVisitorsCount}")
                }
            }
        }
    }
}

interface ServiceVisitorsStats {
/* ... */

    val visitorsCounter: VisitorsCounter?

    fun registerVisit(clientId: String) {
        visitorsCounter?.registerVisit(clientId)
    }
}

interface LoggingService {
    fun log(logMessage: String)
}

class VisitorsCounter {
    var uniqueVisitorsCount: Int = 0
        private set

    fun registerVisit(clientId: String) {
// Пока что считаем всех посетителей уникальными
        uniqueVisitorsCount++
    }
}