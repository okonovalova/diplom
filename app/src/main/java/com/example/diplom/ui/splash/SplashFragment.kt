package com.example.diplom.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private val viewModel: SplashViewModel by viewModels()

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers(){
        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            navigateToMainFragment()
        }
        viewModel.navigateToLoginFragment.observe(viewLifecycleOwner) {
            navigateToLoginFragment()
        }
    }
    private fun navigateToMainFragment(){
        findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
    }
    private fun navigateToLoginFragment(){
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }
}