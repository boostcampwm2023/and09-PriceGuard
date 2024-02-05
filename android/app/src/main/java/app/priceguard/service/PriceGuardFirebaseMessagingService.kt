package app.priceguard.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.priceguard.R
import app.priceguard.ui.detail.DetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PriceGuardFirebaseMessagingService : FirebaseMessagingService() {
    // Init 시에도 호출됨
    override fun onNewToken(token: String) {
        Log.d("PriceGuardFirebaseMessagingService", "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            sendNotification(
                it.title ?: return,
                it.body ?: return,
                it.imageUrl ?: return,
                message.data["shop"] ?: return,
                message.data["productCode"] ?: return
            )
        }
    }

    private fun sendNotification(title: String, body: String, imageUrl: Uri, shop: String, code: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("productShop", shop)
        intent.putExtra("productCode", code)
        intent.putExtra("directed", true)

        val requestCode = getRequestCode()
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    buildNotification(title, body, resource, pendingIntent, requestCode)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun buildNotification(
        title: String,
        body: String,
        image: Bitmap,
        pendingIntent: PendingIntent,
        requestCode: Int
    ) {
        val channelId = getString(R.string.price_notification)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_priceguard_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setLargeIcon(image)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            getString(R.string.priceguard_push_alarm_title),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(requestCode, notificationBuilder.build())
    }

    companion object {
        private var requestCode = 0

        fun getRequestCode(): Int {
            requestCode = (requestCode + 1) % 100
            return requestCode
        }
    }
}
