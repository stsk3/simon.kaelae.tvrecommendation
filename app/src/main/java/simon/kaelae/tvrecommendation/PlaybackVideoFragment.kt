package simon.kaelae.tvrecommendation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.*
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_exo.view.*
import org.json.JSONArray
import org.json.JSONObject

class PlaybackVideoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.activity_exo, container,
            false)

        val (id, title, _, _, videoUrl, func) = activity?.intent?.getSerializableExtra(DetailsActivity.MOVIE) as Movie

        setUpPlayer(view)
        setUpNetwork()

        prepareVideo(id, title, videoUrl, func)

        return view
    }


    override fun onStop() {
        super.onStop()
        player.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun setUpPlayer(view: View){
        // setup track selector
        val bandwithMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwithMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        // create player
        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
        player.playWhenReady = true
        playerView = view.player_view
        playerView.useController = true
        playerView.requestFocus()
        playerView.player = player
        playerView.hideController()

        dataSourceFactory = DefaultDataSourceFactory(activity, "exoplayer", bandwithMeter)
        hlsMediaSourceFactory = HlsMediaSource.Factory(dataSourceFactory)

        toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    }

    private fun setUpNetwork(){
        requestQueue = RequestQueue(NoCache(), BasicNetwork(HurlStack())).apply {
            start()
        }
    }

    fun channelSwitch(direction: String, showMessage: Boolean){
        lastDirection = direction

        val list = MovieList.list
        //val sharedPreference = activity?.getSharedPreferences("layout", Activity.MODE_PRIVATE)

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

        if(showMessage){
            toast.setText("正在轉台到 " + item.title)
            toast.show()
        }

        prepareVideo(item.id, item.title, item.videoUrl, item.func)
    }

    fun prepareVideo(id: Int, title: String, videoUrl: String, func: String){

            currentVideoID = id

            if(videoUrl.equals("")){
                getVideoUrl(title, func)
            }else{
                playVideo(title, videoUrl)
            }

    }

    fun playVideo(title: String, videoUrl: String) {
//        mTransportControlGlue.title = title
//        playerAdapter.setDataSource(Uri.parse(handleUrl(videoUrl)))
//        mTransportControlGlue.playWhenPrepared()
        try {

            var myClipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var myClip: ClipData = ClipData.newPlainText("note_copy", videoUrl)
            myClipboard.setPrimaryClip(myClip)
            //Toast.makeText(context, "已複製播放網址到剪貼簿", Toast.LENGTH_SHORT).show()
        }catch (e: java.lang.Exception){}
        val sharedPreference = activity?.getSharedPreferences("layout", Context.MODE_PRIVATE)
        if (sharedPreference?.getString("player", "originalplayer") == "originalplayer") {
            val mediaUri = Uri.parse(videoUrl)
            val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaUri)
            player.prepare(mediaSource)
        }else{


        try {
            val playIntent: Intent = Uri.parse(videoUrl).let { uri ->
                Intent(Intent.ACTION_VIEW, uri)
            }
            startActivity(playIntent)
        }catch (e: java.lang.Exception){
            //Toast.makeText(context?.applicationContext,"沒有播放器，建議安裝Mx Player，改用內置播放器",Toast.LENGTH_SHORT).show()
            val mediaUri = Uri.parse(videoUrl)
            val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaUri)
            player.prepare(mediaSource)
        }}
    }

    private fun getVideoUrl(title: String, ch: String) {
        requestQueue.cancelAll(this)

        lateinit var url: String

        if(ch.equals("viutv99") || ch.equals("nowtv332") || ch.equals("nowtv331")){
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
                    try {
                        url = JSONArray(JSONObject(JSONObject(response.get("asset").toString()).get("hls").toString()).get("adaptive").toString()).get(0).toString()
                        playVideo(title, url)
                    }catch (exception: Exception){
                        showPlaybackErrorMessage(title)
                    }
                },
                Response.ErrorListener{ error ->
                    showPlaybackErrorMessage(title)
                }
            )

            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            requestQueue.add(jsonObjectRequest)
        }else if(ch.equals("cabletv109") || ch.equals("cabletv110")){
            url = "https://mobileapp.i-cable.com/iCableMobile/API/api.php"

            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    try {
                        playVideo(title, JSONObject(JSONObject(response).get("result").toString()).get("stream").toString())
                    }catch (exception: Exception){
                        showPlaybackErrorMessage(title)
                    }
                },
                Response.ErrorListener{ error ->
                    showPlaybackErrorMessage(title)
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


    private fun showPlaybackErrorMessage(title: String){
        toast.setText(title + " 暫時未能播放，請稍候再試。")
        toast.show()
        channelSwitch(lastDirection, false)
    }

    companion object {
        private lateinit var player: SimpleExoPlayer
        private lateinit var playerView: SimpleExoPlayerView
        private lateinit var dataSourceFactory: DefaultDataSourceFactory
        private lateinit var hlsMediaSourceFactory: HlsMediaSource.Factory

        var currentVideoID = -1
        private lateinit var requestQueue: RequestQueue
        private var lastDirection = "NEXT"
        private lateinit var toast: Toast
    }
}
