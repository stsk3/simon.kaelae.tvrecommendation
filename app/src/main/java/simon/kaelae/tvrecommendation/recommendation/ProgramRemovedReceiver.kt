package simon.kaelae.tvrecommendation.recommendation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.tvprovider.media.tv.TvContractCompat
import android.util.Log
import simon.kaelae.tvrecommendation.ext.loadDefaultChannelId
import simon.kaelae.tvrecommendation.ext.saveDeletedIds

class ProgramRemovedReceiver : BroadcastReceiver() {
  override fun onReceive(c: Context, i: Intent) {
    if (i.action == TvContractCompat.ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED) {
      Log.d("ProgramRemovedReceiver", "Received ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED")
      // save content ids
      val contents = loadPrograms(c.contentResolver, c.loadDefaultChannelId())
      contents.filter { !it.isBrowsable }
          .map { it.title }
          .toList()
          .let {
            Log.d("ProgramRemovedReceiver", it.toString())
            c.saveDeletedIds(it)
          }
    }
  }
}
