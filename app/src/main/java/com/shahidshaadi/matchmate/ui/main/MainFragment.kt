// File: ui/main/MainFragment.kt

package com.shahidshaadi.matchmate.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shahidshaadi.matchmate.databinding.FragmentMainBinding
import com.shahidshaadi.matchmate.ui.main.adapters.LoadingAdapter
import com.shahidshaadi.matchmate.ui.main.adapters.MatchAdapter
import com.shahidshaadi.matchmate.ui.state.MainUiState
import com.shahidshaadi.matchmate.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private val matchAdapter by lazy { MatchAdapter(::onAccept, ::onDecline) }

    private val loadingAdapter = LoadingAdapter()

    private val concatAdapter = ConcatAdapter(matchAdapter, loadingAdapter)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeLoadMore()
        observeViewModel()
        observeNetwork()
    }

    private fun observeLoadMore() {
        var wasLoading = false
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingMore.collect { isLoading ->
                    loadingAdapter.isVisible = isLoading

                    if (wasLoading && !isLoading) {
                        binding.recyclerView.post {
                            val lm = binding.recyclerView.layoutManager as? LinearLayoutManager
                            val lastVisible = lm?.findLastVisibleItemPosition() ?: -1
                            val totalItems = matchAdapter.itemCount

                            if (lastVisible >= totalItems - 3 && totalItems > 0) {
                                val targetPosition = (totalItems - 1).coerceAtLeast(0)
                                lm?.scrollToPositionWithOffset(targetPosition, 0)

                                val cardHeightPx = 200
                                binding.recyclerView.smoothScrollBy(0, cardHeightPx)
                            }
                        }
                    }
                    wasLoading = isLoading
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = layoutManager as? LinearLayoutManager ?: return
                    val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItems = matchAdapter.itemCount

                    if (lastVisible >= totalItems - 2 && totalItems > 0) {
                        viewModel.loadMore()
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainUiState.Loading -> {
                            binding.recyclerView.visibility = View.GONE
                        }

                        is MainUiState.Success -> {
                            binding.recyclerView.visibility = View.VISIBLE
                            matchAdapter.submitList(state.matches)
                        }

                        is MainUiState.Error -> {
                            binding.recyclerView.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun onAccept(id: String) {
        viewModel.onAccept(id)
    }

    private fun onDecline(id: String) {
        viewModel.onDecline(id)
    }

    private fun observeNetwork() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.networkState.collect { isConnected ->
                    binding.offlineBanner.visibility =
                        if (isConnected == false) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}