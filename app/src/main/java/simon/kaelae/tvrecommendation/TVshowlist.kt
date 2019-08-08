package simon.kaelae.tvrecommendation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ArrayAdapter



class TVshowlist : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tvlistview)
        val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
        val original_name:MutableList<String> = sharedPreference.getString("name","")?.split(",")!!.toMutableList()


        val original_url:MutableList<String> = sharedPreference.getString("url","")?.split(",")!!.toMutableList()


        var newlist= mutableListOf<String>()
        for (i in  0 until sharedPreference.getString("name", "")!!.split(",").size) {
            newlist.add(original_name[i]+" : "+original_url[i])


        }

        newlist.removeAt(0)

        var editor = sharedPreference.edit()

        val lvAdapter = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, newlist)

        val lv = findViewById<ListView>(R.id.list)
        lv.adapter = lvAdapter

        lv.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            //Toast.makeText(this,(i+1).toString(),Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"這自定頻道已刪除",Toast.LENGTH_SHORT).show()
            removeSharePreference(i+1)
        }

    }

    fun removeSharePreference(i: Int){


        val sharedPreference = getSharedPreferences("layout", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        val original_name:MutableList<String> = sharedPreference.getString("name","")?.split(",")!!.toMutableList()
        original_name.removeAt(i)
        val original_name_string = original_name.joinToString (separator = ",")
        val original_url:MutableList<String> = sharedPreference.getString("url","")?.split(",")!!.toMutableList()
        original_url.removeAt(i)
        val original_url_string = original_url.joinToString (separator = ",")
        editor.putString("name", original_name_string)
        editor.putString("url", original_url_string)
        editor.apply()
        recreate();
    }
    override
    fun onBackPressed() {

        super.onBackPressed()  // optional depending on your needs
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}