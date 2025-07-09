import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * ShowTimeService - return prev or no prev time. check lastExecutionDateTime
 *
 *
 */
@Service
class ShowTimeService {
    fun showTime(lastExecutionDateTime: LocalDateTime?): LocalDateTime {
        println()
        lastExecutionDateTime?.let {
            println("Previous execution time: $it")
        } ?: println("No previous executions found")
        val now = LocalDateTime.now()
        println("Current execution time: $now")
        return now;
    }
}
