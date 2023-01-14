package com.example.diplom.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.diplom.R
import com.example.diplom.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val viewModel: MainViewModel by viewModels()
    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNavigationBar()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.showBottomMenu.observe(viewLifecycleOwner) {
            binding.bottomNavigationView.isVisible = it
        }
    }

    private fun setupBottomNavigationBar() {
        val hostFragment =
            childFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(hostFragment.navController)
    }
}