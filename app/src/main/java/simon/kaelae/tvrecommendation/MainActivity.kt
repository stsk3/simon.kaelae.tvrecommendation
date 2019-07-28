package simon.kaelae.tvrecommendation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.phone_layout.*
import simon.kaelae.tvrecommendation.recommendation.DefaultChannelRecommendationJobService
import simon.kaelae.tvrecommendation.recommendation.PROGRAM_QUERY
import android.os.Build



class MainActivity : Activity() {


    val title = arrayOf(
        "ViuTV\n ",
        "now新聞台\n ",
        "now直播台\n ",
        "香港開電視\n ",
        "有線新聞台\n ",
        "有線直播台\n ",
        "港台電視31\n ",
        "港台電視32\n ",
        "更新消息請留意Facebook專頁"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreference =  getSharedPreferences("layout",Context.MODE_PRIVATE)
        sharedPreference.getString("layout","")


        if (isTV() || sharedPreference.getString("layout","") == "TV") {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

            setContentView(R.layout.activity_main)

            val switchlayout = findViewById<Button>(R.id.tophone)
            switchlayout.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    val sharedPreference =  getSharedPreferences("layout", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("layout","phone")
                    editor.apply()

                    recreate();

                }})

        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
//            val addsource = findViewById<Button>(R.id.addsource)
//            addsource.setOnClickListener(object : View.OnClickListener{
//                override fun onClick(v: View?) {
//                    //Your code here
//                }})

            val switchlayout = findViewById<Button>(R.id.toTV)
            switchlayout.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    val sharedPreference =  getSharedPreferences("layout", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("layout","TV")
                    editor.apply()

                    recreate();
                }})
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
