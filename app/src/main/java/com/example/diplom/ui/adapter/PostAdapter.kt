package com.example.diplom.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.PopupMenu
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.diplom.R
import com.example.diplom.databinding.ItemPostBinding
import com.example.diplom.domain.entity.AttachmentType
import com.example.diplom.domain.entity.Post
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.utils.visible


class PostAdapter(
    private val onEditPostListener: (Post) -> Unit,
    private val onRemovePostListener: (Post) -> Unit,
    private val onLikeClickListener: (Post) -> Unit,
    private val onMapClickListener: (Post) -> Unit,
    private val onMediaCLickListener: (Post) -> Unit,
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(
            binding,
            onEditPostListener,
            onRemovePostListener,
            onLikeClickListener,
            onMapClickListener,
            onMediaCLickListener,
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onEditPostListener: (Post) -> Unit,
        private val onRemovePostListener: (Post) -> Unit,
        private val onLikeClickListener: (Post) -> Unit,
        private val onMapClickListener: (Post) -> Unit,
        private val onMediaCLickListener: (Post) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.textviewContent.text = post.content
            binding.authorNameTextview.text = post.author
            binding.authotJobPositionTextview.text = post.authorJob
            binding.mediaImageview.visible(post.attachment != null)
            binding.menuImageview.visible(post.ownedByMe)
            binding.mapImageview.visible(post.coords != null)

            bindPostLike(post)
            Glide
                .with(binding.root)
                .load(post.authorAvatar)
                .apply(RequestOptions().transform(CenterCrop(), CircleCrop()))
                .error(R.drawable.ic_account)
                .into(binding.avatarImageview)

            when (post.attachment?.type) {
                AttachmentType.IMAGE -> bindMediaImageLayout(post)
                AttachmentType.VIDEO -> bindMediaVideoLayout(post)
                AttachmentType.AUDIO -> bindMediaAudioLayout()
                else -> bindLayoutWithoutMedia()
            }

            if (post.attachment?.state == STATE.PLAY) {
                binding.playImageview.setImageResource(R.drawable.ic_stop)
            } else {
                binding.playImageview.setImageResource(R.drawable.ic_play)
            }

            initListeners(post)
        }

        private fun initListeners(post: Post) {
            binding.menuImageview.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_edit -> onEditPostListener.invoke(post)
                            else -> onRemovePostListener.invoke(post)
                        }
                        true
                    }
                }.show()
            }
            binding.likeImageview.setOnClickListener {
                onLikeClickListener.invoke(post)
            }
            binding.mapImageview.setOnClickListener {
                onMapClickListener.invoke(post)
            }
            binding.playImageview.setOnClickListener {
                onMediaCLickListener.invoke(post)
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

        private fun bindMediaImageLayout(post: Post) {
            binding.mediaImageview.visible(true)
            Glide
                .with(binding.root)
                .load(post.attachment?.url)
                .error(R.drawable.ic_error)
                .into(binding.mediaImageview)
            binding.mediaVideoview.visible(false)
            binding.playImageview.visible(false)
            binding.playVideoImageview.visible(false)


        }

        private fun bindMediaVideoLayout(post: Post) {
            binding.mediaImageview.visible(false)
            binding.playImageview.visible(false)
            binding.playVideoImageview.visible(true)
            binding.mediaVideoview.visible(true)
            val videoView: VideoView = binding.mediaVideoview
            videoView.setVideoURI(Uri.parse(post.attachment?.url))
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
            with(binding) {
                mediaVideoview.visible(false)
                playVideoImageview.visible(false)
                mediaImageview.visible(true)
                mediaImageview.setImageResource(R.drawable.background_audio)
                playImageview.visible(true)
            }
        }

        private fun bindPostLike(post: Post) {
            if (post.likedByMe) {
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
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}