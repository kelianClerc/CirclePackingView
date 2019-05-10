package fr.clercke.circlepacking

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt


class Circle constructor(var x: Float, var y: Float, val config: Config) {
    var radius = DEFAULT_RADIUS
        private set
    var shouldGrow = true

    private val circlePaint = Paint().apply {
        color = config.circleColor
        style = Paint.Style.STROKE
        strokeWidth = config.strokeWidthPx.toFloat()
    }

    fun isTouchingEdge(width: Int, height: Int): Boolean {
        return x + radius - config.strokeWidthPx > width
            || x - radius - config.strokeWidthPx < 0
            || y + radius - config.strokeWidthPx > height
            || y - radius - config.strokeWidthPx < 0
    }

    fun grow(step: Float = 1f) {
        if (shouldGrow) radius += step
    }

    fun show(c: Canvas) {
        c.drawCircle(x, y, radius, circlePaint)
    }

    companion object {
        private const val DEFAULT_RADIUS = 1f
    }

    data class Config(
        @ColorInt val circleColor: Int,
        val strokeWidthPx: Int
    )
}
