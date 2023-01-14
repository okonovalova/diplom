package com.example.diplom.ui.events.edit_event

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import com.example.diplom.databinding.FragmentEditEventBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class EditEventFragment : Fragment(R.layout.fragment_edit_event), OnMapReadyCallback, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val viewModel: EditEventViewModel by viewModels()
    private val binding by viewBinding(FragmentEditEventBinding::bind)
    private lateinit var mMap: GoogleMap
    private val args: EditEventFragmentArgs by navArgs()

    private var dateTimeShow = ""
    private var dateTimeData = ""

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
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                enableMyLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                enableMyLocation()
            }
            else -> {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onInitView()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.inflateMenu(R.menu.add_post_done)
        args.event.let {
            binding.contentTextEdit.setText(it.content)
            viewModel.onChangeContent(it.content)
            viewModel.onSendCoordsChecked(it.coords != null)
            if (it.attachment != null){
                viewModel.setImageFromEvent(it.attachment.url)
            }
            binding.dateTimeValueTextview.text = convertDateFromStringTime(it.datetime)
            viewModel.setEventDateTime(it.datetime)
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
                error = if (it) context.getString(R.string.error_event_content) else null
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
                R.id.action_add ->  {
                    viewModel.onDoneButtonClicked(args.event)
                }
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

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (R.id.radio_online == checkedId) {
                viewModel.setEventType("ONLINE")
            } else {
                viewModel.setEventType("OFFLINE")
            }
        }

        binding.addDateTimeImageview.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(requireContext(), this@EditEventFragment, year, month, day)
            datePickerDialog.show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            context, this, hour, minute,
            DateFormat.is24HourFormat(context)
        )
        val dayPart = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        val monthPart = if (month +1 < 10 ) "0${month + 1}" else "${month + 1}"
        dateTimeShow = "$dayPart.$monthPart.$year "
        dateTimeData = "$year-$monthPart-$dayPart "
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val hourPart = if (hourOfDay <10) "0$hourOfDay" else "$hourOfDay"
        val minutePart = if (minute <10) "0$minute" else "$minute"
        dateTimeShow += "$hourPart:$minutePart"
        dateTimeData += "$hourPart:$minutePart"

        binding.dateTimeValueTextview.text = dateTimeShow
        viewModel.setEventDateTime(dateTimeData)
    }


    private fun checkPermissions() {
        if (hasLocationPermission()) {
            enableMyLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateFromIsoToDateTime(dateTimeString: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'") : String? {
        return try {
            val localDateTime: LocalDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern))
            val currentZoneId: ZoneId = ZoneId.systemDefault()
            val currentZonedDateTime: ZonedDateTime = localDateTime.atZone(currentZoneId)
            val format: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            format.format(currentZonedDateTime)
        } catch (e: Exception){
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateFromStringTime(dateTimeString: String) : String {
        return convertDateFromIsoToDateTime(dateTimeString, "yyyy-MM-dd'T'HH:mm:ss'Z'") ?: convertDateFromIsoToDateTime(
            dateTimeString,
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        ).orEmpty()
    }

}
