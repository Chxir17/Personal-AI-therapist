
import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.quartz.PersistJobDataAfterExecution
import org.springframework.scheduling.quartz.QuartzJobBean
import java.time.LocalDateTime

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class Scheduler(
    private val showTimeService: ShowTimeService
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        val dataMap = context.jobDetail.jobDataMap
        val lastExecutionDateTime = dataMap["lastExecutionDateTime"] as LocalDateTime?
        dataMap["lastExecutionDateTime"] = showTimeService.showTime(lastExecutionDateTime)
    }
}