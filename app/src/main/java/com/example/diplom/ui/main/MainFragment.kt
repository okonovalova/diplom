package com.example.diplom.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNavigationBar()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.showBottomMenu.observe(viewLifecycleOwner) {
            binding.bottomNavigationView.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun setupBottomNavigationBar() {
        val hostFragment =
            childFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(hostFragment.navController)
    }
}