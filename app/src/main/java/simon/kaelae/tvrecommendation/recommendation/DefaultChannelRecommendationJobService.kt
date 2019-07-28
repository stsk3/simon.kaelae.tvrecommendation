package simon.kaelae.tvrecommendation.recommendation

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.tvprovider.media.tv.ChannelLogoUtils
import androidx.tvprovider.media.tv.TvContractCompat

import simon.kaelae.tvrecommendation.JobIdManager
import simon.kaelae.tvrecommendation.Movie
import simon.kaelae.tvrecommendation.MovieList
import simon.kaelae.tvrecommendation.R
import simon.kaelae.tvrecommendation.ext.loadDefaultChannelId
import simon.kaelae.tvrecommendation.ext.loadDeletedIds
import simon.kaelae.tvrecommendation.ext.saveDefaultChannelId
import java.util.concurrent.TimeUnit

class DefaultChannelRecommendationJobService : JobService() {

  companion object {
    @JvmStatic
    fun startJob(c: Context) {
      val channels = loadChannels(c.contentResolver)
      var defaultChannelId = c.loadDefaultChannelId()
      if (channels.isEmpty()) {
        //create default channel
        val channel = createChannel(c.getString(R.string.app_name))
        defaultChannelId = insertChannel(c.contentResolver, channel)
        c.saveDefaultChannelId(defaultChannelId)
        setDefaultChannel(c, defaultChannelId)
      }
      scheduleProgramJob(c, defaultChannelId)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun setDefaultChannel(c: Context, channelId: Long) {
      val res = c.resources
      val logoId = R.mipmap.ic_launcher
      val logoUri = Uri.parse(
          ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
              res.getResourcePackageName(logoId) + "/" +
              res.getResourceTypeName(logoId) + "/" +
              res.getResourceEntryName(logoId))
      ChannelLogoUtils.storeChannelLogo(c, channelId, logoUri)
      TvContractCompat.requestChannelBrowsable(c, channelId)
    }

    private fun scheduleProgramJob(c: Context, channelId: Long) {
      val service = ComponentName(c, DefaultChannelRecommendationJobService::class.java)
      val scheduler = c.getSystemService(
          Context.JOB_SCHEDULER_SERVICE) as JobScheduler
      val jobId = JobIdManager.getJobId(JobIdManager.TYPE_CHANNEL_PROGRAMS, channelId.toInt())
      val builder = JobInfo.Builder(jobId, service)
      scheduler.schedule(builder.setPeriodic(TimeUnit.MINUTES.toMillis(15)).build())
    }
  }

  /**
   * Clear all data and reinsert programs. If deleted program is exist, do not reinsert it.
   */
  override fun onStartJob(params: JobParameters?): Boolean {
    val defaultChannelId = loadDefaultChannelId()

    var newOrderList = MovieList.list
    newOrderList = listOf<Movie>(MovieList.list[1],MovieList.list[4],MovieList.list[2],MovieList.list[5],MovieList.list[6],MovieList.list[7],MovieList.list[0],MovieList.list[3])

 //   newOrderList = listOf<Movie>(MovieList.list[1],MovieList.list[4],MovieList.list[2],MovieList.list[5],MovieList.list[0],MovieList.list[3],MovieList.list[6],MovieList.list[7])


    val programs = createPrograms(defaultChannelId, newOrderList)
    deleteAllPrograms(contentResolver)
    val deletedIds = loadDeletedIds()
    programs.filter { !deletedIds.contains(it.internalProviderId) }
        .let {
          bulkInsertPrograms(contentResolver, it)
          jobFinished(params, true)
        }
    return false
  }

  override fun onStopJob(params: JobParameters?): Boolean {
    jobFinished(params, false)
    return false
  }
}