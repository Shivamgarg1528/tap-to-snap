package com.lab49.assignment.taptosnap.features.main.ui

import android.view.LayoutInflater
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }
}