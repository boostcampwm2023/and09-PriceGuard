package app.priceguard.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.token.TokenRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UpdateTokenWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val tokenRepository: TokenRepository
) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val token = Firebase.messaging.token.await()
        return storeToken(token)
    }

    private suspend fun storeToken(token: String): Result {
        return try {
            when (tokenRepository.updateFirebaseToken(token)) {
                is RepositoryResult.Error -> {
                    Result.failure()
                }

                is RepositoryResult.Success -> {
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Log.e("Update Token Error", e.message.toString())
            Result.failure()
        }
    }
}
