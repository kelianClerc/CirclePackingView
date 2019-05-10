package fr.clercke.circlepacking

import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import kotlin.random.Random

fun dist(x: Double, y: Double, dx: Double, dy: Double): Double {
    return Math.sqrt((dx - x) * (dx - x) + (dy - y) * (dy - y))
}

fun dist(x: Float, y: Float, dx: Float, dy: Float): Double {
    return dist(x.toDouble(), y.toDouble(), dx.toDouble(), dy.toDouble())
}

fun Bitmap.isWhitePixel(x: Int, y: Int, thresholdDetection: Float = 0.8f): Boolean {
    return ColorUtils.calculateLuminance(this.getPixel(x, y)) >= thresholdDetection
}

fun <T> List<T>.randomElement(): T {
    val elementIndex = Random.nextInt(this.size)
    return this[elementIndex]
}
