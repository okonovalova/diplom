package com.example.diplom.ui.registration

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.databinding.FragmentRegistrationBinding
import com.example.diplom.ui.extensions.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    private val viewModel: RegistrationViewModel by viewModels()
    private val binding by viewBinding(FragmentRegistrationBinding::bind)
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onNewPictureSet(it)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.confirmButton.setOnClickListener {
            binding.root.hideKeyboard()
            viewModel.onRegistrateButtonClicked()
        }
        binding.loginTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangeLogin(text.toString())
        }
        binding.passwordTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangePassword(text.toString())
        }
        binding.nameTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangeName(text.toString())
        }
        binding.addMediaButton.setOnClickListener {
            selectImageFromGalleryResult.launch("image/*")
        }
    }

    private fun initObservers() {
        viewModel.loginError.observe(viewLifecycleOwner) {
            with(binding.loginTextInput) {
                isErrorEnabled = it
                error = if (it) context.getString(R.string.error_login) else null
            }
        }
        viewModel.passwordError.observe(viewLifecycleOwner) {
            with(binding.passwordTextInput) {
                isErrorEnabled = it
                error = if (it) context.getString(R.string.error_password) else null
            }
        }
        viewModel.nameError.observe(viewLifecycleOwner) {
            with(binding.nameTextInput) {
                isErrorEnabled = it
                error = if (it) context.getString(R.string.error_name) else null
            }
        }

        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            navigateToMainFragment()
        }

        viewModel.registrationError.observe(viewLifecycleOwner) { errorText ->
            Snackbar.make(binding.root, errorText, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.downloadedImage.observe(viewLifecycleOwner) {
            binding.showMediaImageview.isVisible = it != null
            Glide
                .with(binding.root)
                .load(it)
                .into(binding.showMediaImageview)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLayout.root.isVisible = isLoading
        }
    }

    private fun navigateToMainFragment() {
        findNavController().navigate(R.id.action_registrationFragment_to_mainFragment)
    }
}