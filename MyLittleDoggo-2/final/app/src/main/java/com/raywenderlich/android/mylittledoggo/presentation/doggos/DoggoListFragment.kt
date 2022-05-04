/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.mylittledoggo.presentation.doggos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.raywenderlich.android.mylittledoggo.R
import com.raywenderlich.android.mylittledoggo.presentation.DoggosAdapter
import kotlinx.android.synthetic.main.fragment_doggos_list.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class DoggoListFragment : Fragment() {

  private val viewModel: DoggoListViewModel by viewModels { DoggoListViewModelFactory }
  private var isLoadingMoreItems = false

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_doggos_list, container, false)
    val doggosAdapter = createAdapter()

    setupRecyclerView(view, doggosAdapter)
    observeViewModel(doggosAdapter)

    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setExitToFullScreenTransition()
    setReturnFromFullScreenTransition()
  }

  private fun createAdapter(): DoggosAdapter {
    return DoggosAdapter { view, doggo ->
      val extraInfoForSharedElement = FragmentNavigatorExtras(
          view to doggo.picture
      )
      val toDoggoFragment =
          DoggoListFragmentDirections.toDoggoFragment(doggo.picture, doggo.isFavourite)

      navigate(toDoggoFragment, extraInfoForSharedElement)
    }
  }

  private fun setupRecyclerView(view: View, doggosAdapter: DoggosAdapter) {
    view.recycler_view_doggos.run {
      adapter = doggosAdapter

      setHasFixedSize(true)
      addOnScrollListener(createInfiniteScrollListener(layoutManager as GridLayoutManager))

      postponeEnterTransition()
      viewTreeObserver.addOnPreDrawListener {
        startPostponedEnterTransition()
        true
      }
    }
  }

  private fun createInfiniteScrollListener(
      gridLayoutManager: GridLayoutManager
  ): RecyclerView.OnScrollListener {
    return object : InfiniteScrollListener(gridLayoutManager, DoggoListViewModel.PAGE_SIZE) {
      override fun loadMoreItems() {
        isLoadingMoreItems = true
        viewModel.getMoreDoggos(DoggoListViewModel.PAGE_SIZE)
      }

      override fun isLoading(): Boolean = isLoadingMoreItems
    }
  }

  private fun observeViewModel(doggosAdapter: DoggosAdapter) {
    viewModel.doggoList.observe(viewLifecycleOwner) {
      doggosAdapter.submitList(it)
      isLoadingMoreItems = false
    }
  }

  private fun setExitToFullScreenTransition() {
    exitTransition =
        TransitionInflater.from(context).inflateTransition(R.transition.doggo_list_exit_transition)
  }

  private fun setReturnFromFullScreenTransition() {
    reenterTransition =
        TransitionInflater.from(context).inflateTransition(R.transition.doggo_list_return_transition)
  }

  private fun navigate(destination: NavDirections, extraInfo: FragmentNavigator.Extras) = with(findNavController()) {
    currentDestination?.getAction(destination.actionId)
        ?.let { navigate(destination, extraInfo) }
  }
}