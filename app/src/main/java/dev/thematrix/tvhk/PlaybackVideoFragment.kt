package dev.thematrix.tvhk

import android.net.Uri
import android.os.Bundle
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
        setUpNetwork()

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

        mTransportControlGlue.isControlsOverlayAutoHideEnabled = true
        hideControlsOverlay(false)
        mTransportControlGlue.isSeekEnabled = false
        mTransportControlGlue.playWhenPrepared()
    }

    private fun setUpNetwork(){
        val cacheDir = File(activity?.cacheDir, "")
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        requestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }

    fun channelSwitch(direction: String){
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
        currentVideoID = id

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
    }

    private fun getVideoUrl(id: Int, title: String, ch: String) {
        requestQueue.cancelAll(this)

        if(ch.equals("viutv99") || ch.equals("nowtv332") || ch.equals("nowtv331")){
            var url = ""
            val params = JSONObject()

            if(ch.equals("viutv99")){
                url = "http://api.viu.now.com/p8/2/getLiveURL"

                params.put("channelno", "099")

                params.put("deviceId", "AndroidTV")
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

            params.put("callerReferenceNo", "")
            params.put("format", "HLS")
            params.put("mode", "prod")

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                Response.Listener { response ->
                    playVideo(id, title, JSONArray(JSONObject(JSONObject(response.get("asset").toString()).get("hls").toString()).get("adaptive").toString()).get(0).toString().replace("https://", "http://"))
                },
                Response.ErrorListener{ error ->
                    Toast.makeText(this.activity, error.toString(), Toast.LENGTH_SHORT).show()
                }
            )

            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )

            requestQueue.add(jsonObjectRequest)
        }else if(ch.equals("cabletv109") || ch.equals("cabletv110")){
            val stringRequest = object: StringRequest(
                Method.POST,
                "http://mobileapp.i-cable.com/iCableMobile/API/api.php",
                Response.Listener { response ->
                    playVideo(id, title, JSONObject(JSONObject(response).get("result").toString()).get("stream").toString().replace("https://", "http://"))
                },
                Response.ErrorListener{ error ->
                    Toast.makeText(this.activity, error.toString(), Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val params =  mutableMapOf<String, String>()

                    params.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; AndroidTV Build/35.0.A.1.282)")

                    return params
                }

                override fun getParams(): MutableMap<String, String> {
                    val params =  mutableMapOf<String, String>()

                    if(ch.equals("cabletv109")){
                        params.put("channel_no", "_9")
                        params.put("vlink", "_9")
                    }else if(ch.equals("cabletv110")){
                        params.put("channel_no", "_10")
                        params.put("vlink", "_10")
                    }

                    params.put("device", "aos_mobile")
                    params.put("method", "streamingGenerator2")
                    params.put("quality", "h")
                    params.put("uuid", "")
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
        private lateinit var requestQueue: RequestQueue
    }
}