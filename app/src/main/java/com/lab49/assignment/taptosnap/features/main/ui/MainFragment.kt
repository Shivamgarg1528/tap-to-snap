package com.lab49.assignment.taptosnap.features.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.databinding.FragmentMainBinding
import com.lab49.assignment.taptosnap.util.SWW
import com.lab49.assignment.taptosnap.util.clickWithDebounce
import com.lab49.assignment.taptosnap.util.toBase64String
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var pictureLauncher: ActivityResultLauncher<Void>

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            binding.target.setImageBitmap(it)
            sharedVM.uploadItem(request = ItemRequest(
                imageLabel = sharedVM.tookFirstItem().item.name,
                image = it.toBase64String())
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!sharedVM.isItemsAvailable()) {
            findNavController().navigate(R.id.action_main_fragment_to_splash_fragment)
            return
        }
        binding.capture.clickWithDebounce {
            try {
                pictureLauncher.launch(null)
            } catch (e: Exception) {
                sharedVM.postMessage(e.message.orEmpty().ifEmpty { SWW })
            }
        }
    }
}