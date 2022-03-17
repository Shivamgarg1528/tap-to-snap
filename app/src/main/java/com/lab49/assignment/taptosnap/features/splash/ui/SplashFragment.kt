package com.lab49.assignment.taptosnap.features.splash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.databinding.FragmentSplashBinding
import com.lab49.assignment.taptosnap.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun getViewBinding(inflater: LayoutInflater) = FragmentSplashBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val owner = viewLifecycleOwner
        owner.lifecycleScope.launch {
            sharedVM.itemsListEvent
                .flowWithLifecycle(owner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Resource.Loading -> {
                            showProgress()
                        }
                        is Resource.Failure -> {
                            hideProgress()
                            sharedVM.postMessage(event.throwable.getMessageForUi())
                        }
                        is Resource.Success -> {
                            sharedVM.cacheResponse(event.result)
                            if (!sharedVM.areItemsAvailable()) {
                                sharedVM.postMessage(getString(R.string.no_item_found))
                            } else {
                                hideProgress()
                                findNavController().navigate(R.id.action_splash_fragment_to_main_fragment)
                            }
                        }
                    }
                }
        }
        binding.btnLetsGo.clickWithDebounce { sharedVM.getItems() }
    }

    override fun showProgress() {
        super.showProgress()
        binding.progressBar.visible()
    }

    override fun hideProgress() {
        super.hideProgress()
        binding.progressBar.gone()
    }
}