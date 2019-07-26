package simon.kaelae.tvrecommendation.recommendation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.tvprovider.media.tv.TvContractCompat

class RecommendationBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != TvContractCompat.ACTION_INITIALIZE_PROGRAMS) return
    DefaultChannelRecommendationJobService.startJob(context)
  }
}