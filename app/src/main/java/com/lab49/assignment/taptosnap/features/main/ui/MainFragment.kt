package com.lab49.assignment.taptosnap.features.main.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.databinding.FragmentMainBinding
import com.lab49.assignment.taptosnap.SharedViewModel
import com.lab49.assignment.taptosnap.features.main.adapter.ItemsAdapter
import com.lab49.assignment.taptosnap.util.Constants.FTG
import com.lab49.assignment.taptosnap.util.Constants.SWW
import com.lab49.assignment.taptosnap.util.toBase64String
import com.lab49.assignment.taptosnap.util.toDp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var pictureLauncher: ActivityResultLauncher<Void?>

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                val wrapper = sharedVM.tappedItem
                if (wrapper != null &&
                    bitmap != null &&
                    sharedVM.updateImage(wrapper.item.name, bitmap)
                ) {
                    val request = ItemRequest(wrapper.item.name, bitmap.toBase64String())
                    sharedVM.postImage(request = request)
                    return@registerForActivityResult
                }
                sharedVM.postMessage(FTG)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!sharedVM.areItemsAvailable()) {
            findNavController().navigate(R.id.action_main_fragment_to_splash_fragment)
            return
        }
        val itemsAdapter = ItemsAdapter { tappedItem ->
            try {
                sharedVM.cacheTappedItem(tappedItem)
                pictureLauncher.launch(null)
            } catch (e: Exception) {
                sharedVM.cacheTappedItem(null)
                sharedVM.postMessage(e.message.orEmpty().ifEmpty { SWW })
            }
        }
        binding.rvItems.apply {
            itemAnimator = null
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    outRect.set(8.toDp, 8.toDp, 8.toDp, 8.toDp)
                }
            })
            adapter = itemsAdapter
        }

        //2- events listing
        viewLifecycleOwner.lifecycleScope.launch {
            sharedVM.events
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collect {
                    when (it) {
                        is SharedViewModel.Event.DataList -> {
                            itemsAdapter.submitList(it.items)
                        }
                        is SharedViewModel.Event.Timer -> {
                            binding.tvTimer.text = it.time
                        }
                        SharedViewModel.Event.Lost -> {
                            showAlert(context = requireContext(),
                                title = getString(R.string.game_over),
                                message = getString(R.string.better_luck),
                                positiveBtnText = getString(R.string.restart),
                                positiveBtnAction = { sharedVM.restartGame() },
                                negativeBtnText = getString(R.string.exit),
                                negativeBtnAction = { requireActivity().finish() }
                            )
                        }
                        SharedViewModel.Event.Won -> {
                            showAlert(context = requireContext(),
                                title = getString(R.string.nice_job),
                                message = getString(R.string.game_won),
                                positiveBtnText = getString(R.string.restart),
                                positiveBtnAction = { sharedVM.restartGame() },
                                negativeBtnText = getString(R.string.exit),
                                negativeBtnAction = { requireActivity().finish() }
                            )
                        }
                    }
                }
        }

        //3- start timer
        sharedVM.startTimer()
        //4- post data
        sharedVM.postItemsOnUi()
    }
}