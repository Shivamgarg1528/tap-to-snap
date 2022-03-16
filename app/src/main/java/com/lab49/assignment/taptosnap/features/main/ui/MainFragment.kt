package com.lab49.assignment.taptosnap.features.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // this is the process death case where we loosing the data
        // so taking user to splash screen
        // we can implement other options like DB + SF + Bundle to handle this
        // but for keeping it simple we doing this
        if (!sharedVM.isItemsAvailable()) {
            findNavController().navigate(R.id.action_main_fragment_to_splash_fragment)
            return
        }
    }
}