package simon.kaelae.tvrecommendation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import simon.kaelae.tvrecommendation.recommendation.DefaultChannelRecommendationJobService
import simon.kaelae.tvrecommendation.recommendation.PROGRAM_QUERY

class MainActivity : Activity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this@MainActivity)
        var inreview = "yes"
        val sharedPreference =  getSharedPreferences("inreview",MODE_PRIVATE)
        inreview = sharedPreference.getString("inreview","yes")!!

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("inreview")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)!!
                val editor = sharedPreference.edit()
                editor.putString("inreview",value)
                editor.apply()

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

//        if(inreview == "yes") {
//            Toast.makeText(this, "yes", Toast.LENGTH_LONG).show()
//        }else{
//            Toast.makeText(this, "no", Toast.LENGTH_LONG).show()
//        }
        try {
            DefaultChannelRecommendationJobService.startJob(this)
        }catch (e:Exception){}
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
}
