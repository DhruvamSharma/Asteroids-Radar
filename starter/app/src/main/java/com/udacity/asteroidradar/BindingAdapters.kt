package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.main.AsteroidListAdapter
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.repository.FeedStatus

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("feedStatus")
fun bindFeedStatus(progressBar: ProgressBar, feedStatus: FeedStatus) {
    when (feedStatus) {
        FeedStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        FeedStatus.ERROR -> {
            progressBar.visibility = View.GONE
        }
        FeedStatus.LOADED -> {
            progressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: MainViewModel) {
    val adapter = recyclerView.adapter as AsteroidListAdapter
    adapter.submitList(data.asteroids.value)
    if(data.asteroids.value?.isNotEmpty() == true) {
        data.updateFeedStatus(FeedStatus.LOADED)
    }
}

@BindingAdapter("asteroidOfTheDayImage")
fun bindAsteroidOfTheDayImage(imageView: ImageView, picture: PictureOfDay?) {
    if (picture != null) {
        if (picture.mediaType == "image") {
            Picasso.get()
                .load(picture.url)
                .placeholder(R.drawable.offline_image_of_the_day)
                .error(R.drawable.offline_image_of_the_day)
                .into(imageView)
        }
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
