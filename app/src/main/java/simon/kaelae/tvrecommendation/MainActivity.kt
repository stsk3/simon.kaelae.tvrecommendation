package simon.kaelae.tvrecommendation

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import simon.kaelae.tvrecommendation.recommendation.DefaultChannelRecommendationJobService
import simon.kaelae.tvrecommendation.recommendation.PROGRAM_QUERY
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import kotlinx.android.synthetic.main.phone_layout.*


class MainActivity : Activity() {


    var fblink = Uri.parse("https://www.facebook.com/androidtvhk")
    var title = mutableListOf(
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
        FirebaseApp.initializeApp(this@MainActivity)
        val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
        sharedPreference.getString("layout", "")


        if (isTV() || sharedPreference.getString("layout", "") == "TV") {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            setTheme(R.style.AppTheme)
            setContentView(R.layout.activity_main)

            val switchlayout = findViewById<Button>(R.id.tophone)
            switchlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("layout", "phone")
                    editor.apply()

                    recreate();

                }
            })

            val add_customsource = findViewById<Button>(R.id.custom_video)
            add_customsource.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    AlertDialog.Builder(this@MainActivity, R.style.Theme_AppCompat).apply {
                        val dialogView = this@MainActivity.layoutInflater.inflate(R.layout.dialog_new_category, null)


                        setView(dialogView)

                        setTitle("新增影片來源")
                        setMessage("輸入名稱及影片網址")

                        setPositiveButton("Save") { _, _ ->
                            if(dialogView.findViewById<EditText>(R.id.url).text.toString() == ""){
                                Toast.makeText(this@MainActivity,"串流網止不可留空",Toast.LENGTH_SHORT).show()
                            }else {
                                val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
                                var editor = sharedPreference.edit()

                                val original_name = sharedPreference.getString("name", "")
                                val original_url = sharedPreference.getString("url", "")


                                var new_name =
                                    original_name + "`" + dialogView.findViewById<EditText>(R.id.name).text.toString()
                                var new_url =
                                    original_url + "`" + dialogView.findViewById<EditText>(R.id.url).text.toString()

                                editor.putString("name", new_name)
                                editor.putString("url", new_url)
                                editor.apply()

                                recreate();
                            }
                        }

                        setNegativeButton("Cancel") { _, _ ->
                            //pass
                        }
                        setNeutralButton("編輯自訂台") { _, _ ->
                            val intent = Intent(this@MainActivity, TVshowlist::class.java)
                            startActivity(intent)
                        }

                    }.create().show()
                }
            })

        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setTheme(R.style.mytheme)
            setContentView(R.layout.phone_layout)
            val gridview = findViewById<GridView>(R.id.gridview)

            val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)

            if (sharedPreference.getString("name", "") == "") {
            } else {
                //.makeText(this@MainActivity,sharedPreference.getString("name", "")!!.split("`")[1],Toast.LENGTH_SHORT).show()
                //Toast.makeText(this@MainActivity,sharedPreference.getString("name", "")!!.split("`").size.toString(),Toast.LENGTH_SHORT).show()

                for (i in  0 until sharedPreference.getString("name", "")!!.split("`").size) {
                    title.add(sharedPreference.getString("name", "")!!.split("`")[i])

                }
                title.removeAt(9)
            }

            val adapter = ImageListAdapter(this, R.layout.grid_cell, title)
            gridview.adapter = adapter


            gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                if (position == 8) {
                    val openURL = Intent(android.content.Intent.ACTION_VIEW)
                    openURL.data = fblink
                    startActivity(openURL)
                }
                if (position>8) {

                    val intent = Intent(this, PlaybackActivity::class.java)
                    val movie = Movie(
                        id.toInt(),
                        title = sharedPreference.getString("name", "")!!.split("`")[position-8],
                        description = "",
                        cardImageUrl = "",
                        videoUrl = sharedPreference.getString("url", "")!!.split("`")[position-8],
                        func = ""
                    )
                    intent.putExtra(DetailsActivity.MOVIE, movie)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, PlaybackActivity::class.java)
                    val movie = MovieList.list.find { it.id == id.toInt() }
                    intent.putExtra(DetailsActivity.MOVIE, movie)
                    startActivity(intent)
                }
            }

            gridview.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, position, arg3 ->

                if(position>8){
                    //Toast.makeText(this@MainActivity,sharedPreference.getString("url", "")!!.split("`")[position-8],Toast.LENGTH_SHORT).show()

                    lateinit var dialog:AlertDialog
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("刪除此自定項目?")

                    val dialogClickListener = DialogInterface.OnClickListener{_,which ->
                        when(which){
                            DialogInterface.BUTTON_POSITIVE -> removeSharePreference(position-8)
                        }
                    }
                    builder.setPositiveButton("YES",dialogClickListener)
                    builder.setNegativeButton("CANCEL",dialogClickListener)
                    dialog = builder.create()
                    dialog.show()
                }
                true
            })


            val addsource = findViewById<Button>(R.id.addsource)

            addsource.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    AlertDialog.Builder(this@MainActivity, R.style.AppTheme).apply {
                        val dialogView = this@MainActivity.layoutInflater.inflate(R.layout.dialog_new_category, null)


                        setView(dialogView)

                        setTitle("新增影片來源")
                        setMessage("輸入名稱及影片網址")

                        setPositiveButton("Save") { _, _ ->
                            if(dialogView.findViewById<EditText>(R.id.url).text.toString() == ""){
                                Toast.makeText(this@MainActivity,"串流網止不可留空",Toast.LENGTH_SHORT).show()
                            }else{
                                val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
                                var editor = sharedPreference.edit()

                                val original_name = sharedPreference.getString("name","")
                                val original_url= sharedPreference.getString("url","")


                                var new_name = original_name+"`"+dialogView.findViewById<EditText>(R.id.name).text.toString()
                                var new_url = original_url+"`"+dialogView.findViewById<EditText>(R.id.url).text.toString()

                                editor.putString("name", new_name)
                                editor.putString("url", new_url)
                                editor.apply()

                                recreate();
                            }

                        }

                        setNegativeButton("Cancel") { _, _ ->
                            //pass
                        }

                    }.create().show()
                }
            })

            val switchlayout = findViewById<Button>(R.id.toTV)
            switchlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("layout", "TV")
                    editor.apply()

                    recreate();
                }
            })

            val database = FirebaseDatabase.getInstance()
            val notice = findViewById<TextView>(R.id.notice)
            database.getReference("notice").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.getValue(String::class.java) ==""){
                        notice.visibility = GONE
                    } else {
                        notice.text = dataSnapshot.getValue(String::class.java)
                        notice.visibility = VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

            database.getReference("link").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    fblink = Uri.parse(dataSnapshot.getValue(String::class.java))
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }


        try {
            DefaultChannelRecommendationJobService.startJob(this)
        } catch (e: Exception) {
        }
        if (intent != null && intent.data != null) {
            showMovie()

        }
    }

    override fun onResume() {
        super.onResume()

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


    fun removeSharePreference(i: Int){


        val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        val original_name:MutableList<String> = sharedPreference.getString("name","")?.split("`")!!.toMutableList()
        original_name.removeAt(i)
        val original_name_string = original_name.joinToString (separator = "`")



        val original_url:MutableList<String> = sharedPreference.getString("url","")?.split("`")!!.toMutableList()
        original_url.removeAt(i)
        val original_url_string = original_url.joinToString (separator = "`")



        editor.putString("name", original_name_string)

        editor.putString("url", original_url_string)
        editor.apply()

        recreate();
    }


}
