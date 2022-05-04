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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.mylittledoggo.data.api.ConnectionManager
import com.raywenderlich.android.mylittledoggo.domain.Doggo
import com.raywenderlich.android.mylittledoggo.domain.DoggoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class DoggoListViewModel(
    private val repository: DoggoRepository
) : ViewModel() {

  companion object {
    const val PAGE_SIZE = 10
  }

  val doggoList: LiveData<List<Doggo>>
    get() = _doggoList

  private val _doggoList: MutableLiveData<List<Doggo>> = MutableLiveData()

  init {
    observeCacheUpdates()
  }

  fun getMoreDoggos(numberOfDoggos: Int) {
    updateCacheWithDoggosFromApi(numberOfDoggos)
  }

  private fun updateCacheWithDoggosFromApi(numberOfDoggos: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val doggos = repository.getApiDoggos(numberOfDoggos)
      repository.updateCachedDoggos(doggos)
    }
  }

  private fun observeCacheUpdates() {
    viewModelScope.launch {
      repository.getCachedDoggos()
          .onEach {
            if (it.isEmpty() && ConnectionManager.isConnected()) {
              getMoreDoggos(PAGE_SIZE)
            }
          }
          .flowOn(Dispatchers.IO)
          .collect { handleDoggos(it) }
    }
  }

  private fun handleDoggos(doggos: List<Doggo>) {
    _doggoList.value = doggos
  }
}