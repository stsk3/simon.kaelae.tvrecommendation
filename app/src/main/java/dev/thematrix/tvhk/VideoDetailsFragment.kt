package dev.thematrix.tvhk

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.DetailsFragment

class VideoDetailsFragment : DetailsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
}