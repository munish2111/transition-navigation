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

package com.raywenderlich.android.mylittledoggo.presentation.doggodetail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.raywenderlich.android.mylittledoggo.R
import kotlinx.android.synthetic.main.fragment_doggo.*

class DoggoFragment : Fragment() {

  private val args: DoggoFragmentArgs by navArgs()
  private val viewModel: DoggoViewModel by viewModels { DoggoViewModelFactory }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    setHasOptionsMenu(true)

    return inflater.inflate(R.layout.fragment_doggo, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val (picture, isFavorite) = args

    setSharedElementTransitionOnEnter()
    postponeEnterTransition()
    setupFavoriteButton(picture, isFavorite)

    image_view_full_screen_doggo.apply {
      transitionName = picture
      startEnterTransitionAfterLoadingImage(picture, this)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_about, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
  }

  private fun setupFavoriteButton(picture: String, pictureIsFavorite: Boolean) {
    updateButtonBackground(pictureIsFavorite)
    button_favorite.isChecked = pictureIsFavorite
    button_favorite.setOnCheckedChangeListener { _, isChecked ->
      viewModel.updateDoggoFavoriteStatus(picture, isChecked)
      updateButtonBackground(isChecked)
    }
  }

  private fun updateButtonBackground(pictureIsFavorite: Boolean) {
    val buttonImageResource: Int = if (pictureIsFavorite) {
      R.drawable.ic_star_full_42dp
    } else {
      R.drawable.ic_star_border_42dp
    }

    button_favorite.background = resources.getDrawable(buttonImageResource, null)
  }

  private fun setSharedElementTransitionOnEnter() {
    sharedElementEnterTransition = TransitionInflater.from(context)
        .inflateTransition(R.transition.shared_element_transition)
  }

  private fun startEnterTransitionAfterLoadingImage(imageAddress: String, imageView: ImageView) {
    Glide.with(this)
        .load(imageAddress)
        .dontAnimate()
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
              e: GlideException?,
              model: Any?,
              target: Target<Drawable>?,
              isFirstResource: Boolean
          ): Boolean {
            startPostponedEnterTransition()
            return false
          }

          override fun onResourceReady(
              resource: Drawable,
              model: Any,
              target: Target<Drawable>,
              dataSource: DataSource,
              isFirstResource: Boolean
          ): Boolean {
            startPostponedEnterTransition()
            return false
          }
        })
        .into(imageView)
  }
}