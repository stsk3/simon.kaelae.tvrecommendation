package simon.kaelae.tvrecommendation

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.util.Rational
import android.view.View
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.annotation.DrawableRes
import java.util.ArrayList


class PlaybackActivity : FragmentActivity() {
    private val labelPlay: String by lazy { getString(R.string.play) }
    private val labelPause: String by lazy { getString(R.string.pause) }


    private val ACTION_MEDIA_CONTROL = "media_control"

    /** Intent extra for media controls from Picture-in-Picture mode.  */
    private val EXTRA_CONTROL_TYPE = "control_type"

    /** The request code for play action PendingIntent.  */
    private val REQUEST_PLAY = 1

    /** The request code for pause action PendingIntent.  */
    private val REQUEST_PAUSE = 2

    /** The request code for info action PendingIntent.  */
    private val REQUEST_INFO = 3

    /** The intent extra value for play action.  */
    private val CONTROL_TYPE_PLAY = 1

    /** The intent extra value for pause action.  */
    private val CONTROL_TYPE_PAUSE = 2





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, PlaybackVideoFragment())
                .commit()

            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


    }
    private fun isTV(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
    override fun onUserLeaveHint() {
        if(isTV()||android.os.Build.VERSION.SDK_INT <=25 ){}
        else {
                val params = PictureInPictureParams.Builder()
                    .build()
                enterPictureInPictureMode(params)

        }
    }
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean,
                                               newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
        } else {
            // Restore the full-screen UI.
            adjustFullScreen(newConfig)
        }
    }

    override fun onResume() {
        super.onResume()
        val decorView = window?.decorView
        decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun onPause() {
        super.onPause()
        // If called while in PIP mode, do not pause playback
        if (isInPictureInPictureMode) {
            // Continue playback
                supportFragmentManager
                    .beginTransaction()
                    .replace(android.R.id.content, PlaybackVideoFragment())
                    .commit()

        } else {
            // Use existing playback logic for paused Activity behavior.
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustFullScreen(newConfig)
    }

    private fun adjustFullScreen(config: Configuration) {
        val decorView = window.decorView
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

            adjustFullScreen(resources.configuration)

    }


}
