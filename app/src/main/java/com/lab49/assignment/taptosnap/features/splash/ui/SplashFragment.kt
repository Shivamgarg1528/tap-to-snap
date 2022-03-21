package com.lab49.assignment.taptosnap.features.splash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.databinding.FragmentSplashBinding
import com.lab49.assignment.taptosnap.features.splash.vm.SplashViewModel
import com.lab49.assignment.taptosnap.util.Constants
import com.lab49.assignment.taptosnap.util.getMessageForUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val splashVM by viewModels<SplashViewModel>()

    override fun getViewBinding(inflater: LayoutInflater) = FragmentSplashBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //1- setup click listener
        binding.btnLetsGo.setOnClickListener { splashVM.getItems() }

        //2- observe events
        viewLifecycleOwner.lifecycleScope.launch {
            splashVM.events
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        SplashViewModel.Event.NoOperation -> {
                            // No-OP
                        }
                        SplashViewModel.Event.Loading -> {
                            binding.progressLayout.progressBar.isVisible = true
                        }
                        SplashViewModel.Event.Empty -> {
                            binding.progressLayout.progressBar.isGone = true
                            sharedVM.postMessage(getString(R.string.no_item_found))
                        }
                        is SplashViewModel.Event.Failed -> {
                            binding.progressLayout.progressBar.isGone = true
                            sharedVM.postMessage(it.exception.getMessageForUi())
                        }
                        is SplashViewModel.Event.Success -> {
                            binding.progressLayout.progressBar.isGone = true
                            findNavController().navigate(
                                resId = R.id.action_splash_fragment_to_main_fragment,
                                args = bundleOf(Constants.KEY.ITEMS to it.items)
                            )
                        }
                    }
                }
        }
    }
}