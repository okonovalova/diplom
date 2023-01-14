package com.example.diplom.ui.posts.edit_post

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.databinding.FragmentEditPostBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : Fragment(R.layout.fragment_edit_post), OnMapReadyCallback {
    private val viewModel: EditPostViewModel by viewModels()
    private val binding by viewBinding(FragmentEditPostBinding::bind)
    private lateinit var mMap: GoogleMap
    private val args: EditPostFragmentArgs by navArgs()

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onNewPictureSet(it)
            }
        }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                enableMyLocation()
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                enableMyLocation()
            }
            else -> {
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onInitView(args.post)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.inflateMenu(R.menu.add_post_done)


        args.post.let {
            binding.contentTextEdit.setText(it.content)
            viewModel.onSendCoordsChecked(it.coords != null)
            if (it.attachment != null) {
                viewModel.setImageFromPost(it.attachment.url)
            }
        }
        initListeners()
        initObservers()
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapview) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initObservers() {
        viewModel.contentError.observe(viewLifecycleOwner) {
            with(binding.contentTextInput) {
                isErrorEnabled = it
                error = if (it) context.getString(R.string.error_post_content) else null
            }
        }
        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        viewModel.downloadedImage.observe(viewLifecycleOwner) {
            val hasContent = it.uri != null || it.url != null
            binding.showMediaImageview.isVisible = hasContent
            binding.addMediaButton.isVisible = !hasContent
            if (!hasContent) return@observe
            Glide
                .with(binding.root)
                .load(it.uri ?: it.url)
                .into(binding.showMediaImageview)
        }
        viewModel.sendCoords.observe(viewLifecycleOwner) {
            binding.coordsCheckbox.isChecked = it
            binding.mapview.isVisible = it
        }

    }

    private fun initListeners() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> viewModel.onDoneButtonClicked(args.post)
            }
            true
        }
        binding.contentTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangeContent(text.toString())
        }
        binding.addMediaButton.setOnClickListener {
            selectImageFromGalleryResult.launch("image/*")
        }
        binding.coordsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSendCoordsChecked(isChecked)
        }
        binding.cancelMediaImageview.setOnClickListener {
            viewModel.onCancelMedia()
        }
    }

    private fun checkPermissions() {
        if (hasLocationPermission()) {
            enableMyLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        checkPermissions()


        mMap.setOnMapClickListener { latLng ->
            viewModel.setCoords(latLng.latitude, latLng.longitude)
        }
        viewModel.coords.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            val marker = MarkerOptions()
                .position(LatLng(it.lat.toDouble(), it.long.toDouble()))

            mMap.clear()
            mMap.addMarker(marker)
        }
    }
}