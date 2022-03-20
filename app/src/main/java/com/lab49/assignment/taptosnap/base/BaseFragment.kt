package com.lab49.assignment.taptosnap.base

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.lab49.assignment.taptosnap.SharedViewModel

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

    fun showAlert(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        negativeBtnText: String? = null,
        positiveBtnAction: (() -> Unit)? = null,
        negativeBtnAction: (() -> Unit)? = null,
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveBtnText) { _, _ -> positiveBtnAction?.invoke() }

        if (negativeBtnText.isNullOrEmpty().not()) {
            builder.setNegativeButton(negativeBtnText.toString()) { _, _ -> negativeBtnAction?.invoke() }
        }
        builder.show().setCanceledOnTouchOutside(false)
    }
}