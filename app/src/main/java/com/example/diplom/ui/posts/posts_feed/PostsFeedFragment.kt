package com.example.diplom.ui.posts.posts_feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.diplom.R
import com.example.diplom.databinding.FragmentPostsFeedBinding
import com.example.diplom.domain.entity.Post
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.adapter.PostAdapter
import com.example.diplom.ui.media.MediaLifecycleObserver
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostsFeedFragment : Fragment(R.layout.fragment_posts_feed) {
    private val viewModel: PostsFeedViewModel by viewModels()
    private val binding by viewBinding(FragmentPostsFeedBinding::bind)
    private val adapter by lazy {
        PostAdapter(
            onEditPostListener = {
                val action = PostsFeedFragmentDirections.actionPostsFeedFragmentToEditPostFragment(post = it)
                findNavController().navigate(action)
            },
            onRemovePostListener = { viewModel.onRemoveClicked(it) },
            onLikeClickListener = { viewModel.onLikeClicked(it) },
            onMapClickListener = { showMap(it) },
            onMediaCLickListener = { viewModel.onMediaClicked(it) },
        )
    }
    private val observer: MediaLifecycleObserver by lazy { MediaLifecycleObserver(viewModel::onFinishMedia) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(observer)
        viewModel.onInitView()
        binding.recyclerPosts.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerPosts.context,
            RecyclerView.VERTICAL
        )
        binding.recyclerPosts.addItemDecoration(dividerItemDecoration)

        binding.fabAddPost.setOnClickListener {
            navigateToAddPost()
        }
        observeViewModel()
        viewModel.getPosts()
    }

    private fun observeViewModel() {
        viewModel.postsData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.playingMediaPost.observe(viewLifecycleOwner) { post ->
            when {
                post?.attachment == null -> {
                    return@observe
                }
                post.attachment.state == STATE.PAUSE -> {
                    observer.lastTrack = null
                    observer.mediaPlayer?.pause()
                }
                post.attachment.state == STATE.PLAY -> {
                    observer.apply {
                        lastTrack = post.attachment.url
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(post.attachment.url)
                    }.play()
                }
            }
        }
        viewModel.navigateToAddPost.observe(viewLifecycleOwner) {
            navigateToAddPost()
        }
    }

    private fun navigateToAddPost() {
        findNavController().navigate(R.id.action_postsFeedFragment_to_addPostFragment)
    }

    private fun showMap(post: Post) {
        val lan = post.coords?.lat
        val lng = post.coords?.long
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lan,$lng?z=11&q=$lan,$lng(label)"))
        startActivity(intent)
    }
}