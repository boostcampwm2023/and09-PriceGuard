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
        val productShop = inputData.getString(PRODUCT_SHOP) ?: return Result.failure()
        val productCode = inputData.getString(PRODUCT_CODE) ?: return Result.failure()

        return updateAlarm(productShop, productCode)
    }

    private suspend fun updateAlarm(productShop: String, productCode: String): Result {
        return try {
            when (productRepository.switchAlert(productShop, productCode)) {
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
        const val PRODUCT_CODE = "productCode"
        const val PRODUCT_SHOP = "productShop"

        fun createWorkRequest(inputString: Pair<String, String>): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putString(PRODUCT_SHOP, inputString.first)
                .putString(PRODUCT_CODE, inputString.second)
                .build()
            val constraints = Constraints.Builder().build()
            return OneTimeWorkRequestBuilder<UpdateAlarmWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()
        }
    }
}
