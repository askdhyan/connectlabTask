package com.practicalconnectlab.app.ui.podcast.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicalconnectlab.app.R
import com.practicalconnectlab.app.base.BaseRecyclerViewAdapter
import com.practicalconnectlab.app.ui.podcast.models.PodCastModel
import com.tatwadeep.phonicplayer.views.PhonicPlayerView
import java.util.*

class PodCastAdapter :
    BaseRecyclerViewAdapter<PodCastModel, PodCastAdapter.MatchesViewHolder>() {

    inner class MatchesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val audioPlayer by lazy { view.findViewById(R.id.audioPlayer) as PhonicPlayerView }
        val tvPodCastTitle by lazy { view.findViewById(R.id.tvPodCastTitle) as TextView }
        val tvTotalTime by lazy { view.findViewById(R.id.tvTotalTime) as TextView }
        val tvPodCastTime by lazy { view.findViewById(R.id.tvPodCastTime) as TextView }
    }

    override fun createItemViewHolder(parent: ViewGroup): MatchesViewHolder {
        return MatchesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_podcast, parent, false)
        )
    }

    override fun bindItemViewHolder(viewHolder: MatchesViewHolder, position: Int) {
        items[position].let {
            viewHolder.apply {
                tvPodCastTitle.text = it.podCastTitle
                tvTotalTime.text = it.podCastTotalTime
                tvPodCastTime.text = getTimeAgo(it.podCastSaveTime)
                audioPlayer.setAudioTarget(Uri.parse(it.podCastPath))
//                audioPlayer.setAudioTarget(Uri.parse("/data/user/0/com.practicalconnectlab.app/cache/Audio_8822804232452594459.mp3"))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getTimeAgo(milliseconds: Long): String {
        val SECOND_MILLIS = 1000
        val MINUTE_MILLIS = 60 * SECOND_MILLIS
        val HOUR_MILLIS = 60 * MINUTE_MILLIS
        val DAY_MILLIS = 24 * HOUR_MILLIS
        try {
            var time = milliseconds
            if (time < 1000000000000L) {
                time *= 1000
            }

            val now = currentDate().time
            if (time > now || time <= 0) {
                return " Just now"
            }

            val diff = now - time
            return when {
                diff < MINUTE_MILLIS -> " Just now"
                diff < 2 * MINUTE_MILLIS -> "1 minute ago"
                diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
                diff < 2 * HOUR_MILLIS -> "1 hour ago"
                diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
                diff < 48 * HOUR_MILLIS -> " Yesterday"
                else -> "${diff / DAY_MILLIS} days ago"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }
}