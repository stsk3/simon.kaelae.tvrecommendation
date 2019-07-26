package simon.kaelae.tvrecommendation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.DetailsFragment

class VideoDetailsFragment : DetailsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
}