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

package com.raywenderlich.android.mylittledoggo.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.mylittledoggo.R
import com.raywenderlich.android.mylittledoggo.domain.Doggo
import com.raywenderlich.android.mylittledoggo.presentation.extensions.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_view_doggo_item.*

class DoggosAdapter(
    private val onDoggoClickListener: ((view: View, doggo: Doggo) -> Unit)? = null
) : ListAdapter<Doggo, DoggosAdapter.DoggoViewHolder>(ITEM_COMPARATOR) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoggoViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    return DoggoViewHolder(
        inflater.inflate(
            R.layout.recycler_view_doggo_item,
            parent,
            false
        )
    )
  }

  override fun onBindViewHolder(holder: DoggoViewHolder, position: Int) {
    val item: Doggo = getItem(position)
    holder.bind(item, onDoggoClickListener)
  }

  // Need to implement LayoutContainer so that views are cached correctly
  class DoggoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
      LayoutContainer {

    override val containerView: View?
      get() = itemView

    fun bind(
        item: Doggo,
        onDoggoClickListener: ((view: View, image: Doggo) -> Unit)?
    ) {
      with(image_view_doggo) {
        load(item.picture) {
          onDoggoClickListener?.let {
            it(this, item)
          }
        }
        transitionName = item.picture
      }
    }
  }
}

private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Doggo>() {
  override fun areItemsTheSame(oldItem: Doggo, newItem: Doggo): Boolean {
    return oldItem.picture == newItem.picture
  }

  override fun areContentsTheSame(oldItem: Doggo, newItem: Doggo): Boolean {
    return oldItem == newItem
  }
}