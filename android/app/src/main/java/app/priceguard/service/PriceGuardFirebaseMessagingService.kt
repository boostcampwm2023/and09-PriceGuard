package app.priceguard.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PriceGuardFirebaseMessagingService : FirebaseMessagingService() {
    // Init 시에도 호출됨
    override fun onNewToken(token: String) {
        Log.d("PriceGuardFirebaseMessagingService", "Refreshed token: $token")

        /* TODO: If you want to send messages to this application instance
            or manage this apps subscriptions on the server side,
            send the FCM registration token to your app server. */
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle received FCM messages here
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Message Body: ${it.body}")
            // Handle the notification message here
        }

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Data Payload: ${remoteMessage.data}")
            // Handle the data payload here
        }
    }
}
