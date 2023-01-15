package com.example.diplom.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.diplom.R
import com.example.diplom.databinding.FragmentLoginBinding
import com.example.diplom.ui.extensions.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginViewModel by viewModels()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.confirmButton.setOnClickListener {
            binding.root.hideKeyboard()
            viewModel.onAuthenticateButtonClicked()
        }
        binding.registrationLinkTextview.setOnClickListener {
            navigateToRegistrationFragment()
        }
        binding.loginTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangeLogin(text.toString())
        }
        binding.passwordTextEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.onChangePassword(text.toString())
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

        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            navigateToMainFragment()
        }

        viewModel.authenticateError.observe(viewLifecycleOwner) { errorText ->
            Snackbar.make(binding.root, errorText, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLayout.root.isVisible = isLoading
        }
    }

    private fun navigateToMainFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }

    private fun navigateToRegistrationFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }
}