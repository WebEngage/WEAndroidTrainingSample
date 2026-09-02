package com.webengage.demo.shopping.view.cards

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.webengage.demo.shopping.R

/**
 * Loads a card's artwork into an ImageView. Supports both remote URLs (via Glide)
 * and local drawable names (fallback), so cards.json can carry either form.
 */
object CardImageLoader {

    fun load(imageView: ImageView, imageRes: String?) {
        val value = imageRes.orEmpty()
        if (value.startsWith("http://") || value.startsWith("https://")) {
            Glide.with(imageView)
                .load(value)
                .placeholder(R.drawable.meridian_premium_infinite)
                .error(R.drawable.meridian_premium_infinite)
                .into(imageView)
        } else {
            val resId = imageView.resources.getIdentifier(
                value, "drawable", imageView.context.packageName
            )
            imageView.setImageResource(
                if (resId != 0) resId else R.drawable.meridian_premium_infinite
            )
        }
    }
}
