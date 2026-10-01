package com.example.androidnativeshowcase.services.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowcaseMessagingService : FirebaseMessagingService() {
    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Showcase update"
        val body = message.notification?.body ?: message.data["body"] ?: "Background data changed"
        notificationHelper.show(title, body)
    }

    override fun onNewToken(token: String) {
        notificationHelper.ensureChannel()
    }
}
