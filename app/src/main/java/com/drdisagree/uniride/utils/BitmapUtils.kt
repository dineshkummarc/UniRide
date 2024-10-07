package com.drdisagree.uniride.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun toBitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun toBitmapDescriptorWithColor(
    context: Context,
    @DrawableRes drawableResId: Int,
    color: Color
): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, drawableResId)!!
    val bitmap = drawable.toBitmap()
    val tintedBitmap = bitmap.tint(color.toArgb())
    return BitmapDescriptorFactory.fromBitmap(tintedBitmap)
}

fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth.takeIf { it > 0 } ?: 1,
        intrinsicHeight.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun Bitmap.tint(color: Int): Bitmap {
    val paint = Paint().apply {
        this.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
    val tintedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    Canvas(tintedBitmap).drawBitmap(this, 0f, 0f, paint)
    return tintedBitmap
}