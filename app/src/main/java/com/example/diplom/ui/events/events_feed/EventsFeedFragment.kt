package com.example.diplom.ui.events.events_feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.diplom.R
import com.example.diplom.databinding.FragmentEventsFeedBinding
import com.example.diplom.domain.entity.Event
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.events.events_feed.adapter.EventAdapter
import com.example.diplom.ui.media.MediaLifecycleObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsFeedFragment : Fragment(R.layout.fragment_events_feed) {
    private val viewModel: EventsFeedViewModel by viewModels()
    private val binding by viewBinding(FragmentEventsFeedBinding::bind)
    private val adapter by lazy {
        EventAdapter(
            onEditEventListener = {
                val action = EventsFeedFragmentDirections.actionEventsFeedFragmentToEditEventFragment(event = it)
                findNavController().navigate(action)
            },
            onRemoveEventListener = { viewModel.onRemoveClicked(it) },
            onLikeClickListener = { viewModel.onLikeListener(it) },
            onMapClickListener = { showMap(it) },
            onParticipateClickListener = { viewModel.onParticipateListener(it) },
            onMediaCLickListener = { viewModel.onMediaClicked(it) }

        )
    }
    private val observer: MediaLifecycleObserver by lazy { MediaLifecycleObserver(viewModel::onFinishMedia) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(observer)
        viewModel.onInitView()
        binding.recyclerEvents.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerEvents.context,
            RecyclerView.VERTICAL
        )
        binding.recyclerEvents.addItemDecoration(dividerItemDecoration)
        binding.fabAddEvent.setOnClickListener {
            navigateToAddEvent()
        }
        viewModel.getEvents()
        observeViewModel()
        initListeners()
    }

    private fun observeViewModel() {
        viewModel.eventsData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.playingMediaEvent.observe(viewLifecycleOwner) { event ->
            when {
                event?.attachment == null -> {
                    return@observe
                }
                event.attachment.state == STATE.PAUSE -> {
                    observer.lastTrack = null
                    observer.mediaPlayer?.pause()
                }
                event.attachment.state == STATE.PLAY -> {
                    observer.apply {
                        lastTrack = event.attachment.url
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(event.attachment.url)
                    }.play()
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLayout.root.isVisible = isLoading
        }
    }

    private fun initListeners() {
        binding.fabAddEvent.setOnClickListener {
            findNavController().navigate(R.id.action_eventsFeedFragment_to_addEventFragment)
        }
    }

    private fun navigateToAddEvent() {
        findNavController().navigate(R.id.action_eventsFeedFragment_to_addEventFragment)
    }

    private fun showMap(event: Event) {
        val lan = event.coords?.lat
        val lng = event.coords?.long
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lan,$lng?z=11&q=$lan,$lng(label)"))
        startActivity(intent)
    }
}