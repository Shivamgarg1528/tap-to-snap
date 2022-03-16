package com.lab49.assignment.taptosnap.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.lab49.assignment.taptosnap.features.SharedViewModel

abstract class BaseFragment<T : ViewBinding>(id: Int) : Fragment(id) {

    private var _binding: T? = null
    protected val binding: T
        get() {
            if (view == null) {
                throw IllegalStateException("Can't access the binding when getView() is null i.e.before onCreateView() or after onDestroyView()")
            }
            return _binding!!
        }

    protected val sharedVM by activityViewModels<SharedViewModel>()

    abstract fun getViewBinding(inflater: LayoutInflater): T

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun showProgress() {}

    protected open fun hideProgress() {}
}