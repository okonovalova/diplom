package com.example.diplom.ui.registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.databinding.FragmentRegistrationBinding
import com.example.diplom.ui.utils.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var binding: FragmentRegistrationBinding
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onNewPictureSet(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()

    }

    private fun initListeners() {
        binding.confirmButton.setOnClickListener {
            viewModel.onRegistrateButtonClicked()
        }
        binding.loginTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangeLogin(text.toString())
        }
        binding.passwordTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangePassword(text.toString())
        }
        binding.nameTextEdit.doOnTextChanged { text, start, before, count ->
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
                error = if (it) "Введите логин" else null
            }
        }
        viewModel.passwordError.observe(viewLifecycleOwner) {
            with(binding.passwordTextInput) {
                isErrorEnabled = it
                error = if (it) "Введите пароль" else null
            }
        }
        viewModel.nameError.observe(viewLifecycleOwner) {
            with(binding.nameTextInput) {
                isErrorEnabled = it
                error = if (it) "Введите имя" else null
            }
        }

        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            navigateToMainFragment()
        }
        viewModel.downloadedImage.observe(viewLifecycleOwner) {
            binding.showMediaImageview.visible(it != null)
            Glide
                .with(binding.root)
                .load(it)
                .into(binding.showMediaImageview)
        }

    }

    private fun navigateToMainFragment() {
        findNavController().navigate(R.id.action_registrationFragment_to_mainFragment)
    }
}