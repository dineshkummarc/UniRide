package com.drdisagree.uniride.utils

import android.animation.ValueAnimator
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

class AnimationQueue(
    @Volatile private var startPosition: LatLng,
    private val scope: CoroutineScope,
    private val updatePosition: (LatLng) -> Unit
) {

    companion object {
        private const val ANIMATION_DURATION: Long = 600
    }

    private val items = Collections.synchronizedList(mutableListOf<LatLng>())

    init {
        Log.d("AnimationQueue", "recomposed")
    }

    fun addToQueue(newPosition: LatLng, threshold: Float) {
        if (!isSignificantChange(startPosition, newPosition, threshold)) return

        items.add(newPosition)
        if (items.size == 1) { // Start animating when the first item is added
            scope.launch { processQueue() }
        }
    }

    private suspend fun processQueue() {
        while (items.isNotEmpty()) {
            val targetPosition = items.removeAt(0)
            animateMarker(startPosition, targetPosition, updatePosition = updatePosition)
            startPosition = targetPosition
        }
    }

    private fun isSignificantChange(
        startPosition: LatLng, newPosition: LatLng, threshold: Float
    ): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            startPosition.latitude,
            startPosition.longitude,
            newPosition.latitude,
            newPosition.longitude,
            results
        )
        return results[0] > threshold // Distance in meters
    }

    private fun interpolateLatLng(start: LatLng, end: LatLng, fraction: Float): LatLng {
        val lat = (end.latitude - start.latitude) * fraction + start.latitude
        val lng = (end.longitude - start.longitude) * fraction + start.longitude
        return LatLng(lat, lng)
    }

    private suspend fun animateMarker(
        startPosition: LatLng, targetPosition: LatLng, updatePosition: (LatLng) -> Unit
    ) = withContext(Dispatchers.Main) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.setDuration(ANIMATION_DURATION)

        animator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            val interpolatedLatLng = interpolateLatLng(startPosition, targetPosition, fraction)
            updatePosition(interpolatedLatLng)
        }

        animator.start()
        animator.addUpdateListener {
            if (!animator.isRunning) {
                // Ensure the final position is set at the end of the animation
                updatePosition(targetPosition)
            }
        }
    }
}