package dev.thematrix.tvhk

import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import com.android.volley.*
import com.android.volley.toolbox.*
import org.json.JSONArray
import org.json.JSONObject

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
    }

    private fun setUpNetwork(){
        requestQueue = RequestQueue(NoCache(), BasicNetwork(HurlStack())).apply {
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
            getVideoUrl(title, func)
        }else{
            playVideo(title, videoUrl)
        }
    }

    fun playVideo(title: String, videoUrl: String) {
        mTransportControlGlue.title = title
        playerAdapter.setDataSource(Uri.parse(videoUrl))
        mTransportControlGlue.playWhenPrepared()
    }

    private fun getVideoUrl(title: String, ch: String) {
        requestQueue.cancelAll(this)

        if(ch.equals("viutv99") || ch.equals("nowtv332") || ch.equals("nowtv331")){
            var url = ""
            val params = JSONObject()

            if(ch.equals("viutv99")){
                url = "https://api.viu.now.com/p8/2/getLiveURL"

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
                    playVideo(title, JSONArray(JSONObject(JSONObject(response.get("asset").toString()).get("hls").toString()).get("adaptive").toString()).get(0).toString())
                },
                Response.ErrorListener{ error ->
                }
            )

            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            requestQueue.add(jsonObjectRequest)
        }else if(ch.equals("cabletv109") || ch.equals("cabletv110")){
            val stringRequest = object: StringRequest(
                Method.POST,
                "https://mobileapp.i-cable.com/iCableMobile/API/api.php",
                Response.Listener { response ->
                    playVideo(title, JSONObject(JSONObject(response).get("result").toString()).get("stream").toString())
                },
                Response.ErrorListener{ error ->
                }
            ){
                override fun getRetryPolicy(): RetryPolicy {
                    return DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                }

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