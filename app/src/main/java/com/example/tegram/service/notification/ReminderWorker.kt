package com.example.tegram.service.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showReminderNotification(
            title = "Đã đến giờ học rồi! 📚",
            message = "Vào Tegram để hoàn thành kế hoạch học tập hôm nay nhé."
        )
        return Result.success()
    }
}
