package dev.thematrix.tvhk

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class PlaybackVideoFragment : VideoSupportFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (id, title, _, videoUrl, func) = activity?.intent?.getSerializableExtra(DetailsActivity.MOVIE) as Movie

        setUpPlayer()

        prepareVideo(id, title, videoUrl, func)
    }

    override fun onPause() {
        super.onPause()
        mTransportControlGlue.pause()
    }

    private fun setUpPlayer(){
        playerAdapter = MediaPlayerAdapter(activity)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
        mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
        mTransportControlGlue.host = glueHost

        mTransportControlGlue.isControlsOverlayAutoHideEnabled = false
        hideControlsOverlay(false)
        mTransportControlGlue.isSeekEnabled = false
        mTransportControlGlue.playWhenPrepared()
    }

    fun onKeyDown(keyCode: Int): Boolean{
        if(
            keyCode == KeyEvent.KEYCODE_DPAD_UP ||
            keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS ||
            keyCode == KeyEvent.KEYCODE_MEDIA_REWIND ||
            keyCode == KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD ||
            keyCode == KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD ||
            keyCode == KeyEvent.KEYCODE_NAVIGATE_PREVIOUS ||
            keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT
        ){
            channelSwitch("PREVIOUS")
        }else if(
            keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
            keyCode == KeyEvent.KEYCODE_MEDIA_NEXT ||
            keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD ||
            keyCode == KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD ||
            keyCode == KeyEvent.KEYCODE_MEDIA_STEP_FORWARD ||
            keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT ||
            keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT
        ){
            channelSwitch("NEXT")
        }else{
            return false
        }

        return true
    }

    private fun channelSwitch(direction: String){
        val list = MovieList.list

        var videoId = currentVideoID
            if(direction.equals("PREVIOUS")){
            videoId--
        }else if(direction.equals("NEXT")) {
            videoId++
        }

        val channelCount = list.count()
        if(videoId < 0){
            videoId = channelCount - 1
        }else if(videoId >= channelCount){
            videoId = 0
        }

        val item = list[videoId]
        prepareVideo(item.id, item.title, item.videoUrl, item.func)
    }

    private fun prepareVideo(id: Int, title: String, videoUrl: String, func: String){
        if(videoUrl.equals("")){
            getVideoUrl(id, title, func)
        }else{
            playVideo(id, title, videoUrl)
        }
    }

    fun playVideo(id: Int, title: String, videoUrl: String) {
        mTransportControlGlue.title = title
        playerAdapter.setDataSource(Uri.parse(videoUrl))
        mTransportControlGlue.playWhenPrepared()

        currentVideoID = id
    }

    private fun getVideoUrl(id: Int, title: String, ch: String) {
        val cacheDir = File(activity?.cacheDir, "")
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        if(ch.equals("viutv99") or ch.equals("nowtv332") or ch.equals("nowtv331")){
            var url = ""
            val params = JSONObject()

            if(ch.equals("viutv99")){
                url = "https://api.viu.now.com/p8/2/getLiveURL"

                params.put("channelno", "099")

                params.put("deviceId", "7849989bff631f5888")
                params.put("deviceType", "5")
            }else{
                url = "https://hkt-mobile-api.nowtv.now.com/09/1/getLiveURL"

                if(ch.equals("nowtv332")){
                    params.put("channelno", "332")
                }else if(ch.equals("nowtv331")){
                    params.put("channelno", "331")
                }

                params.put("audioCode", "")
            }

            params.put("callerReferenceNo", "20190625160500")
            params.put("format", "HLS")
            params.put("mode", "prod")

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                Response.Listener { response ->
                    playVideo(id, title, JSONArray(JSONObject(JSONObject(response.get("asset").toString()).get("hls").toString()).get("adaptive").toString()).get(0).toString())
                },
                Response.ErrorListener{ error ->
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show()
                }
            )

            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )

            requestQueue.add(jsonObjectRequest)
        }else if(ch.equals("cabletv")){
            val stringRequest = object: StringRequest(
                Method.POST,
                "https://mobileapp.i-cable.com/iCableMobile/API/api.php",
                Response.Listener { response ->
                    playVideo(id, title, JSONObject(JSONObject(response).get("result").toString()).get("stream").toString())
                },
                Response.ErrorListener{ error ->
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show()
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val params =  mutableMapOf<String, String>()

                    params.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; F8132 Build/35.0.A.1.282)")

                    return params
                }

                override fun getParams(): MutableMap<String, String> {
                    val params =  mutableMapOf<String, String>()

                    params.put("device", "aos_mobile")
                    params.put("channel_no", "_9")
                    params.put("method", "streamingGenerator2")
                    params.put("quality", "m")
                    params.put("uuid", "")
                    params.put("vlink", "_9")
                    params.put("is_premium", "0")
                    params.put("network", "wifi")
                    params.put("platform", "1")
                    params.put("deviceToken", "")
                    params.put("appVersion", "6.3.4")
                    params.put("market", "G")
                    params.put("lang", "zh_TW")
                    params.put("version", "6.3.4")
                    params.put("osVersion", "23")
                    params.put("channel_id", "106")
                    params.put("deviceModel", "AndroidTV")
                    params.put("type", "live")

                    return params
                }
            }

            requestQueue.add(stringRequest)
        }
    }

    companion object {
        private var currentVideoID = -1
        private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
        private lateinit var playerAdapter: MediaPlayerAdapter
    }
}