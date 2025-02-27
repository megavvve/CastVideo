// MainActivity.kt
package com.example.castvideo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.*

class MainActivity : AppCompatActivity() {
    private lateinit var mediaRouteButton: MediaRouteButton
    private var castContext: CastContext? = null

    private val sessionManagerListener = object : SessionManagerListener<CastSession> {
        override fun onSessionStarted(session: CastSession, sessionId: String) {
            playVideo(session)
        }

        override fun onSessionEnded(session: CastSession, error: Int) {
            Log.d("Cast", "Session ended")
        }

        // Остальные обязательные методы интерфейса
        override fun onSessionStarting(session: CastSession) {}
        override fun onSessionStartFailed(session: CastSession, error: Int) {}
        override fun onSessionEnding(session: CastSession) {}
        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {}
        override fun onSessionResuming(session: CastSession, sessionId: String) {}
        override fun onSessionResumeFailed(session: CastSession, error: Int) {}
        override fun onSessionSuspended(session: CastSession, reason: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // 2. Инициализируем CastContext
            castContext = CastContext.getSharedInstance(this)
        } catch (e: RuntimeException) {
            Log.e("CastError", "CastContext init failed: ${e.message}")
            showErrorDialog("Ошибка инициализации Cast")
            return
        }

        // 3. Настраиваем Cast кнопку
        mediaRouteButton = findViewById(R.id.mediaRouteButton)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, mediaRouteButton)
    }

    override fun onResume() {
        super.onResume()
        // 4. Безопасный вызов с проверкой null
        castContext?.sessionManager?.apply {
            addSessionManagerListener(sessionManagerListener, CastSession::class.java)
        }
    }

    override fun onPause() {
        super.onPause()
        castContext?.sessionManager?.apply {
            removeSessionManagerListener(sessionManagerListener, CastSession::class.java)
        }
    }

    private fun playVideo(castSession: CastSession) {
        castSession.remoteMediaClient?.load(
            MediaLoadRequestData.Builder()
                .setMediaInfo(
                    MediaInfo.Builder("https://example.com/video.mp4")
                        .setContentType("video/mp4")
                        .build()
                )
                .build()
        )?.setResultCallback { result ->
            if (result.status.isSuccess) {
                Log.d("Cast", "Media loaded")
            } else {
                Log.e("Cast", "Error: ${result.status.statusCode}")
            }
        }
    }

    private fun showErrorDialog(message: String) {
        // Реализация показа диалога с ошибкой
    }
}