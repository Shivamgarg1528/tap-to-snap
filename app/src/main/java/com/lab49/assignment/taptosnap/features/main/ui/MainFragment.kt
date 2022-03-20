package com.lab49.assignment.taptosnap.features.main.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.base.BaseFragment
import com.lab49.assignment.taptosnap.databinding.FragmentMainBinding
import com.lab49.assignment.taptosnap.features.main.adapter.ItemsAdapter
import com.lab49.assignment.taptosnap.features.main.ui.vm.MainViewModel
import com.lab49.assignment.taptosnap.util.Constants.SWW
import com.lab49.assignment.taptosnap.util.toDp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val mainVM by viewModels<MainViewModel>()

    private lateinit var pictureLauncher: ActivityResultLauncher<Void?>

    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pictureLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
            mainVM::captured
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //1- setup adapter
        val itemsAdapter = ItemsAdapter { tappedItem ->
            try {
                mainVM.cacheTappedItem(tappedItem)
                pictureLauncher.launch(null)
            } catch (e: Exception) {
                mainVM.cacheTappedItem(null)
                sharedVM.postMessage(e.message.orEmpty().ifEmpty { SWW })
            }
        }
        //2- setup rv
        binding.rvItems.apply {
            adapter = itemsAdapter
            itemAnimator = null
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) = outRect.set(8.toDp, 8.toDp, 8.toDp, 8.toDp)
            })
        }
        //3- observe events
        viewLifecycleOwner.lifecycleScope.launch {
            mainVM.events
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collectLatest {
                    when (it) {
                        is MainViewModel.Events.Items -> {
                            itemsAdapter.submitList(it.items)
                        }
                        is MainViewModel.Events.Timer -> {
                            binding.tvTimer.text = it.time
                        }
                        MainViewModel.Events.Lost -> {
                            showAlert(context = requireContext(),
                                title = getString(R.string.game_over),
                                message = getString(R.string.better_luck),
                                positiveBtnText = getString(R.string.restart),
                                positiveBtnAction = { mainVM.restart() },
                                negativeBtnText = getString(R.string.exit),
                                negativeBtnAction = { mainVM.exit() }
                            )
                        }
                        MainViewModel.Events.Won -> {
                            showAlert(context = requireContext(),
                                title = getString(R.string.nice_job),
                                message = getString(R.string.game_won),
                                positiveBtnText = getString(R.string.restart),
                                positiveBtnAction = { mainVM.restart() },
                                negativeBtnText = getString(R.string.exit),
                                negativeBtnAction = { mainVM.exit() }
                            )
                        }
                        MainViewModel.Events.Empty -> {
                            findNavController().navigate(R.id.action_main_fragment_to_splash_fragment)
                        }
                        MainViewModel.Events.Exit -> {
                            requireActivity().finish()
                        }
                        is MainViewModel.Events.Message -> {
                            sharedVM.postMessage(it.message)
                        }
                    }
                }
        }
    }
}