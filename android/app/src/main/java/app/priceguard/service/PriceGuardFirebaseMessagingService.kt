package app.priceguard.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class PriceGuardFirebaseMessagingService : FirebaseMessagingService() {
    // Init 시에도 호출됨
    override fun onNewToken(token: String) {
        Log.d("PriceGuardFirebaseMessagingService", "Refreshed token: $token")
    }
}
