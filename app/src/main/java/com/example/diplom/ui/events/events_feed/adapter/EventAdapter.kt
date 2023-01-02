package com.example.diplom.ui.events.events_feed.adapter

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.PopupMenu
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.diplom.R
import com.example.diplom.databinding.ItemEventBinding
import com.example.diplom.domain.entity.AttachmentType
import com.example.diplom.domain.entity.Event
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.utils.visible
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class EventAdapter(
    private val onEditEventListener: (Event) -> Unit,
    private val onRemoveEventListener: (Event) -> Unit,
    private val onLikeClickListener: (Event) -> Unit,
    private val onMapClickListener: (Event) -> Unit,
    private val onParticipateClickListener: (Event) -> Unit,
    private val onMediaCLickListener: (Event) -> Unit,
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(
            binding,
            onEditEventListener,
            onRemoveEventListener,
            onLikeClickListener,
            onMapClickListener,
            onParticipateClickListener,
            onMediaCLickListener,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onEditEventListener: (Event) -> Unit,
        private val onRemoveEventListener: (Event) -> Unit,
        private val onLikeClickListener: (Event) -> Unit,
        private val onMapClickListener: (Event) -> Unit,
        private val onParticipateClickListener: (Event) -> Unit,
        private val onMediaCLickListener: (Event) -> Unit,

        ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(event: Event) {
            binding.textviewContent.text = event.content
            binding.authorNameTextview.text = event.author
            binding.authotJobPositionTextview.text = event.authorJob
            val requestOptions = RequestOptions().transform(CenterCrop(), CircleCrop())
            Glide
                .with(binding.root)
                .load(event.authorAvatar)
                .apply(requestOptions)
                .error(R.drawable.ic_account)
                .into(binding.avatarImageview)
            binding.dateTimeValueTextview.text = convertDateFromIsoToDateTime(event.datetime)
            binding.eventTypeValueTextview.text = event.type
            when (event.attachment?.type) {
                AttachmentType.IMAGE -> bindMediaImageLayout(event)
                AttachmentType.VIDEO -> bindMediaVideoLayout(event)
                AttachmentType.AUDIO -> bindMediaAudioLayout()
                else -> bindLayoutWithoutMedia()
            }
            binding.eventCoordsTextview.visible(event.coords != null)
            binding.mapImageview.visible(event.coords != null)

            bindEventLike(event)
            bindParticipateButton(event)

            binding.menuImageview.visible(event.ownedByMe)

            if (event.attachment?.state == STATE.PLAY) {
                binding.playImageview.setImageResource(R.drawable.ic_stop)
            } else {
                binding.playImageview.setImageResource(R.drawable.ic_play)
            }
            initListeners(event)

        }

        private fun initListeners(event: Event) {
            binding.menuImageview.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_edit -> onEditEventListener.invoke(event)
                            else -> onRemoveEventListener.invoke(event)
                        }
                        true
                    }
                }.show()
            }
            binding.mapImageview.setOnClickListener {
                onMapClickListener.invoke(event)
            }
            binding.likeImageview.setOnClickListener {
                onLikeClickListener.invoke(event)
            }
            binding.participateButton.setOnClickListener {
                onParticipateClickListener.invoke(event)
            }
            binding.playImageview.setOnClickListener {
                onMediaCLickListener.invoke(event)
            }
            binding.playVideoImageview.setOnClickListener {
                binding.mediaVideoview.start()
                binding.playVideoImageview.visible(false)
            }
        }

        private fun bindLayoutWithoutMedia() {
            with(binding) {
                mediaImageview.visible(false)
                playImageview.visible(false)
                mediaVideoview.visible(false)
                playVideoImageview.visible(false)
            }
        }

        private fun bindMediaImageLayout(event: Event) {
            Glide
                .with(binding.root)
                .load(event.attachment?.url)
                .error(R.drawable.ic_error)
                .into(binding.mediaImageview)
            binding.mediaVideoview.visible(false)
            binding.playImageview.visible(false)
            binding.playVideoImageview.visible(false)
        }

        private fun bindMediaVideoLayout(event: Event) {
            binding.mediaImageview.visible(false)
            binding.playImageview.visible(false)
            binding.playVideoImageview.visible(true)
            binding.mediaVideoview.visible(true)
            val videoView: VideoView = binding.mediaVideoview
            videoView.setVideoURI(Uri.parse(event.attachment?.url))
            val mediaController = MediaController(binding.root.context)

            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            videoView.setOnCompletionListener {
                videoView.stopPlayback()
                binding.playVideoImageview.visible(true)
                binding.playVideoImageview.setImageResource(R.drawable.ic_play)
            }
        }

        private fun bindMediaAudioLayout() {
            binding.mediaVideoview.visible(false)
            binding.playVideoImageview.visible(false)
            binding.mediaImageview.visible(true)
            binding.playImageview.visible(true)
        }

        private fun bindEventLike(event: Event) {
            if (event.likedByMe) {
                binding.likeImageview.setImageResource(R.drawable.ic_like)
                binding.likeImageview.setColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red_brand_main
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.likeImageview.setImageResource(R.drawable.ic_favorite_border)
                binding.likeImageview.setColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.purple_200
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        private fun bindParticipateButton(event: Event) {
            if (event.participatedByMe) {
                binding.participateButton.text = "Не участвовать"
            } else {
                binding.participateButton.text = "Участвовать"
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertDateFromIsoToDateTime(dateTimeString: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): String? {
            return try {
                val localDateTime: LocalDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern))
                val currentZoneId: ZoneId = ZoneId.systemDefault()
                val currentZonedDateTime: ZonedDateTime = localDateTime.atZone(currentZoneId)
                val format: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                format.format(currentZonedDateTime)
            } catch (e: Exception) {
                null
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertDateFromIsoToDateTime(dateTimeString: String): String {
            return convertDateFromIsoToDateTime(dateTimeString, "yyyy-MM-dd'T'HH:mm:ss'Z'") ?: convertDateFromIsoToDateTime(
                dateTimeString,
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
            ).orEmpty()
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}