package simon.kaelae.tvrecommendation.recommendation

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import simon.kaelae.tvrecommendation.BuildConfig
import simon.kaelae.tvrecommendation.Movie

const val RECOMMENDATION_AUTHORITY = BuildConfig.AUTHORITY + ".recommendation"
const val PROGRAM_QUERY = "id"

fun createChannel(displayName: String): Channel {
  val appUri = Uri.Builder()
      .scheme(BuildConfig.APP_SCHEME)
      .authority(RECOMMENDATION_AUTHORITY)
      .build()
  return Channel.Builder()
      .setType(TvContractCompat.Channels.TYPE_PREVIEW)
      .setDisplayName(displayName)
      .setAppLinkIntentUri(appUri)
      .build()
}

fun createPrograms(channelId: Long, contents: List<Movie>): List<PreviewProgram> {
  return contents.map {
    val uri = Uri.Builder()
        .scheme(BuildConfig.APP_SCHEME)
        .authority(BuildConfig.AUTHORITY)
        .appendQueryParameter(PROGRAM_QUERY, it.id.toString())
        .build()
    PreviewProgram.Builder().setChannelId(channelId)
        .setType(TvContractCompat.PreviewPrograms.TYPE_CHANNEL)
        .setTitle(it.title)
        .setPosterArtUri(Uri.parse(it.cardImageUrl))
        .setPreviewVideoUri(Uri.parse(it.videoUrl))
        .setIntentUri(uri)
        .setInternalProviderId(it.title.toString())
        .build()
  }.toList()
}

fun loadChannels(cr: ContentResolver,
    projection: Array<String>? = arrayOf(),
    selection: String? = "",
    selectionArgs: Array<String>? = arrayOf(),
    sortOrder: String? = ""): List<Channel> {
  val cursor = cr.query(TvContractCompat.Channels.CONTENT_URI, projection, selection, selectionArgs,
      sortOrder)
  if (cursor == null || cursor.count == 0) return emptyList()
  val channels = arrayListOf<Channel>()
  cursor.moveToFirst()
  do {
    channels.add(Channel.fromCursor(cursor))
  } while (cursor.moveToNext())
  return channels
}

fun insertChannel(cr: ContentResolver, channel: Channel): Long {
  val uri = cr.insert(TvContractCompat.Channels.CONTENT_URI, channel.toContentValues())
  return ContentUris.parseId(uri)
}

fun bulkInsertChannels(cr: ContentResolver, channels: List<Channel>) {
  channels.map { it.toContentValues() }
      .toList()
      .let {
        cr.bulkInsert(TvContractCompat.Channels.CONTENT_URI, it.toTypedArray())
      }
}

fun updateChanel(cr: ContentResolver, channel: Channel) {
  cr.update(TvContractCompat.buildChannelUri(channel.id), channel.toContentValues(), null, null)
}

fun deleteChannel(cr: ContentResolver, channel: Channel) {
  cr.delete(TvContractCompat.buildChannelUri(channel.id), null, null)
}

fun loadPrograms(cr: ContentResolver,
    channelId: Long,
    projection: Array<String>? = arrayOf(),
    selection: String? = "",
    selectionArgs: Array<String>? = arrayOf(),
    sortOrder: String? = ""): List<PreviewProgram> {
  val cursor = cr.query(TvContractCompat.PreviewPrograms.CONTENT_URI,
      projection, selection, selectionArgs, sortOrder)
  cursor ?: return emptyList()
  cursor.moveToFirst()
  val programs = arrayListOf<PreviewProgram>()
  do {
    val program = PreviewProgram.fromCursor(cursor)
    if (program.channelId != channelId) continue
    programs.add(program)
  } while (cursor.moveToNext())
  return programs
}

fun insertProgram(cr: ContentResolver, program: PreviewProgram) {
  cr.insert(TvContractCompat.PreviewPrograms.CONTENT_URI, program.toContentValues())
}

fun bulkInsertPrograms(cr: ContentResolver, programs: List<PreviewProgram>) {
  programs.map { it.toContentValues() }
      .let {
        cr.bulkInsert(TvContractCompat.PreviewPrograms.CONTENT_URI, it.toTypedArray())
      }
}

fun updateProgram(cr: ContentResolver, program: PreviewProgram) {
  cr.update(TvContractCompat.buildPreviewProgramUri(program.id),
      program.toContentValues(), null, null)
}

fun deleteProgram(cr: ContentResolver, program: PreviewProgram) {
  cr.delete(TvContractCompat.buildPreviewProgramUri(program.id), null, null)
}

fun deleteProgramsByChannel(cr: ContentResolver, channelId: Long) {
  cr.delete(TvContractCompat.buildChannelUri(channelId), null, null)
}

fun deleteAllPrograms(cr: ContentResolver) {
  cr.delete(TvContractCompat.PreviewPrograms.CONTENT_URI, null, null)
}

fun insertProgramToWatchNext(cr: ContentResolver, program: WatchNextProgram) {
  cr.insert(TvContractCompat.WatchNextPrograms.CONTENT_URI, program.toContentValues())
}

fun bulkInsertProgramsToWatchNext(cr: ContentResolver, programs: List<WatchNextProgram>) {
  programs.map { it.toContentValues() }
      .let {
        cr.bulkInsert(TvContractCompat.WatchNextPrograms.CONTENT_URI, it.toTypedArray())
      }
}

fun deleteWatchNextProgram(cr: ContentResolver, program: WatchNextProgram) {
  cr.delete(TvContractCompat.buildWatchNextProgramUri(program.id), null, null)
}

fun deleteWatchNextPrograms(cr: ContentResolver) {
  cr.delete(TvContractCompat.WatchNextPrograms.CONTENT_URI, null, null)
}