package simon.kaelae.tvrecommendation.recommendation

import android.annotation.TargetApi
import android.app.Activity
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
                        res.getResourceEntryName(logoId)
            )
            ChannelLogoUtils.storeChannelLogo(c, channelId, logoUri)
            TvContractCompat.requestChannelBrowsable(c, channelId)
        }

        private fun scheduleProgramJob(c: Context, channelId: Long) {
            val service = ComponentName(c, DefaultChannelRecommendationJobService::class.java)
            val scheduler = c.getSystemService(
                Context.JOB_SCHEDULER_SERVICE
            ) as JobScheduler
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

        val newOrderList : MutableList<Movie> = ArrayList()


        val sharedPreference = getSharedPreferences("layout", Activity.MODE_PRIVATE)
        if (sharedPreference.getString("name", "") == "") {
            newOrderList.add(MovieList.list[1])
            newOrderList.add(MovieList.list[4])
            newOrderList.add(MovieList.list[2])
            newOrderList.add(MovieList.list[5])
            newOrderList.add(MovieList.list[6])
            newOrderList.add(MovieList.list[7])
            newOrderList.add(MovieList.list[0])
            newOrderList.add(MovieList.list[3])
        } else {

            for (i in 0 until sharedPreference.getString("name", "")!!.split(",").size) {

                newOrderList.add(
                    Movie(
                        id = i+7,
                        title = sharedPreference.getString("name", "")!!.split(",")[i],
                        description = "",
                        cardImageUrl = "https://i.imgur.com/XQnIwzp.png",
                        videoUrl = sharedPreference.getString("url", "")!!.split(",")[i],
                        func = ""
                    )
                )
            }
            newOrderList.add(MovieList.list[1])
            newOrderList.add(MovieList.list[4])
            newOrderList.add(MovieList.list[2])
            newOrderList.add(MovieList.list[5])
            newOrderList.add(MovieList.list[6])
            newOrderList.add(MovieList.list[7])
            newOrderList.add(MovieList.list[0])
            newOrderList.add(MovieList.list[3])
        }
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