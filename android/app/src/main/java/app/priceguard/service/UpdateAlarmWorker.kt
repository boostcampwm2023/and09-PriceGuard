package app.priceguard.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.product.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productRepository: ProductRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val inputData = inputData.getString(ARGUMENT_KEY) ?: return Result.failure()

        return updateAlarm(inputData)
    }

    private suspend fun updateAlarm(productCode: String): Result {
        return try {
            when (productRepository.switchAlert(productCode)) {
                is RepositoryResult.Error -> {
                    Result.failure()
                }

                is RepositoryResult.Success -> {
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Log.e("Update Alarm Error", e.message.toString())
            Result.failure()
        }
    }

    companion object {
        const val ARGUMENT_KEY = "productCode"
        fun createWorkRequest(inputString: String): OneTimeWorkRequest {
            val inputData = Data.Builder().putString(ARGUMENT_KEY, inputString).build()
            val constraints = Constraints.Builder().build()
            return OneTimeWorkRequestBuilder<UpdateAlarmWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()
        }
    }
}
