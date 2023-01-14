package com.example.diplom.ui.home

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.diplom.R
import com.example.diplom.databinding.FragmentHomeBinding
import com.example.diplom.domain.entity.Job
import com.example.diplom.domain.entity.Post
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.adapter.PostAdapter
import com.example.diplom.ui.home.adapter.JobAdapter
import com.example.diplom.ui.media.MediaLifecycleObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val jobsAdapter by lazy {
        JobAdapter(
            onEditJobListener = {
                val action = HomeFragmentDirections.actionHomeFragmentToEditJobFragment(job = it)
                findNavController().navigate(action)
            },
            onRemoveJobListener = { viewModel.onRemoveJobClicked(it) },
            onOpenJobLinkListener = { openJobLink(it) }
        )
    }
    private val postsAdapter by lazy {
        PostAdapter(
            onEditPostListener = {
                val action = HomeFragmentDirections.actionHomeFragmentToEditPostFragment(post = it)
                findNavController().navigate(action)
            },
            onRemovePostListener = { viewModel.onRemovePostClicked(it) },
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
        binding.recyclerJobs.adapter = jobsAdapter
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerJobs.context,
            RecyclerView.VERTICAL
        )
        binding.recyclerJobs.addItemDecoration(dividerItemDecoration)

        binding.recyclerPosts.adapter = postsAdapter
        val dividerItemDecoration1 = DividerItemDecoration(
            binding.recyclerPosts.context,
            RecyclerView.VERTICAL
        )
        binding.recyclerPosts.addItemDecoration(dividerItemDecoration1)

        viewModel.getUserInfo()
        observeViewModel()
        viewModel.getJobs()
        viewModel.getPosts()

        binding.addJobTextview.setOnClickListener {
            navigateToAddJob()
        }
        binding.addPostTextview.setOnClickListener {
            navigateToAddPost()
        }
    }

    private fun observeViewModel() {
        viewModel.userData.observe(viewLifecycleOwner) {
            binding.authorNameTextview.text = it.name
            Glide
                .with(binding.root)
                .load(it.avatar)
                .apply(RequestOptions().transform(CenterCrop(), CircleCrop()))
                .error(R.drawable.ic_account)
                .into(binding.avatarImageview)
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
        viewModel.jobsData.observe(viewLifecycleOwner) {
            jobsAdapter.submitList(it)
        }

        viewModel.postsData.observe(viewLifecycleOwner) {
            postsAdapter.submitList(it)
        }

        viewModel.navigateToAddJob.observe(viewLifecycleOwner) {
            navigateToAddJob()
        }

        viewModel.navigateToAddPost.observe(viewLifecycleOwner) {
            navigateToAddPost()
        }
    }

    private fun navigateToAddJob() {
        findNavController().navigate(R.id.action_homeFragment_to_addJobFragment)
    }

    private fun navigateToAddPost() {
        findNavController().navigate(R.id.action_homeFragment_to_addPostFragment)
    }

    private fun showMap(post: Post) {
        val lan = post.coords?.lat
        val lng = post.coords?.long
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lan,$lng?z=11&q=$lan,$lng(label)"))
        startActivity(intent)
    }

    private fun openJobLink(job: Job) {
        val url = job.link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}