package simon.kaelae.tvrecommendation

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import com.google.firebase.FirebaseApp
import simon.kaelae.tvrecommendation.recommendation.DefaultChannelRecommendationJobService
import simon.kaelae.tvrecommendation.recommendation.PROGRAM_QUERY

class MainActivity : Activity() {


    val title = arrayOf(
        "ViuTV",
        "now新聞台",
        "now直播台",
        "香港開電視",
        "有線新聞台",
        "有線直播台",
        "港台電視31",
        "港台電視32",
        "更新消息請留意Facebook專頁"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isTV()) {
            setContentView(R.layout.activity_main)
        } else {
            setContentView(R.layout.phone_layout)

            val gridview = findViewById<GridView>(R.id.gridview)

            val adapter = ImageListAdapter(this, R.layout.grid_cell, title)
            gridview.adapter = adapter
            gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->

                if (position == 8) {
                    val openURL = Intent(android.content.Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://www.facebook.com/androidtvhk")
                    startActivity(openURL)
                } else {
                    val intent = Intent(this, PlaybackActivity::class.java)
                    val movie = MovieList.list.find { it.id == id.toInt() }

                    intent.putExtra(DetailsActivity.MOVIE, movie)
                    startActivity(intent)
                }
            }
        }
        FirebaseApp.initializeApp(this@MainActivity)

        try {
            DefaultChannelRecommendationJobService.startJob(this)
        } catch (e: Exception) {
        }
        if (intent != null && intent.data != null) {
            showMovie()
        }
    }

    private fun showMovie() {
        Log.d("MainActivity", "url:${intent.data.toString()}")
        val id: String? = intent.data?.getQueryParameter(PROGRAM_QUERY)
        id ?: return
        val movie = MovieList.list.find { it.id == id.toInt() }
        val intent = Intent(this, PlaybackActivity::class.java)
        intent.putExtra(DetailsActivity.MOVIE, movie)
        startActivity(intent)
    }

    private fun isTV(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
}
