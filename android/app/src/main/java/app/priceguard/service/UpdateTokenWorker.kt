package app.priceguard.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

class UpdateTokenWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val token = Firebase.messaging.token.await()
        return storeToken(token)
    }

    private fun storeToken(token: String): Result {
        // TODO: 토큰과 현재 타임스탬프 서버로 전송 및 응답 반환
        return Result.success()
    }
}
