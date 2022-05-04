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

package com.raywenderlich.android.mylittledoggo

import android.app.Application
import androidx.room.Room
import com.raywenderlich.android.mylittledoggo.data.DogCeoDoggoRepository
import com.raywenderlich.android.mylittledoggo.data.api.Api
import com.raywenderlich.android.mylittledoggo.data.api.ConnectionManager
import com.raywenderlich.android.mylittledoggo.data.cache.DoggosDatabase
import com.raywenderlich.android.mylittledoggo.data.cache.RoomCache
import com.raywenderlich.android.mylittledoggo.domain.DoggoRepository
import com.raywenderlich.android.mylittledoggo.presentation.doggodetail.DoggoViewModelFactory
import com.raywenderlich.android.mylittledoggo.presentation.doggos.DoggoListViewModelFactory
import com.raywenderlich.android.mylittledoggo.presentation.favorites.FavoritesViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class App : Application() {

  override fun onCreate() {
    super.onCreate()

    val repository = createRepository()
    ConnectionManager.setContext(this)

    DoggoListViewModelFactory.inject(repository)
    DoggoViewModelFactory.inject(repository)
    FavoritesViewModelFactory.inject(repository)
  }

  private fun createRepository(): DoggoRepository {
    val doggosApi = Api.create()
    val database = createDatabase()
    val doggosDao = database.doggosDao()
    val doggosCache = RoomCache(doggosDao)

    return DogCeoDoggoRepository(doggosApi, doggosCache)
  }

  private fun createDatabase(): DoggosDatabase =
      Room.databaseBuilder(this, DoggosDatabase::class.java, "myLittleDoggo.db")
          .build()
}