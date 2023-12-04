package app.priceguard.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class PriceGuardFirebaseMessagingService : FirebaseMessagingService() {
    // Init 시에도 호출됨
    override fun onNewToken(token: String) {
        Log.d("PriceGuardFirebaseMessagingService", "Refreshed token: $token")

        /* TODO: If you want to send messages to this application instance
            or manage this apps subscriptions on the server side,
            send the FCM registration token to your app server. */
    }
}
