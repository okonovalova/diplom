package com.example.diplom.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initListeners(){
        binding.confirmButton.setOnClickListener {
           viewModel.onAunthentificateButtonClicked()
        }
        binding.registrationLinkTextview.setOnClickListener {
            navigateToRegistrationFragment()
        }
        binding.loginTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangeLogin(text.toString())
        }
        binding.passwordTextEdit.doOnTextChanged { text, start, before, count ->
            viewModel.onChangePassword(text.toString())
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

        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            navigateToMainFragment()
        }

    }

    private fun navigateToMainFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }

    private fun navigateToRegistrationFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }
}