package fr.clercke.circlepacking

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt


class Circle constructor(var x: Float, var y: Float, @ColorInt circleColor: Int) {
    var radius = DEFAULT_RADIUS
        private set
    var shouldGrow = true


    private val circlePaint = Paint().apply {
        color = circleColor
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    fun isTouchingEdge(width: Int, height: Int): Boolean {
        return x + radius - STROKE_WIDTH > width
            || x - radius - STROKE_WIDTH < 0
            || y + radius - STROKE_WIDTH > height
            || y - radius - STROKE_WIDTH < 0
    }

    fun grow(step: Float = 1f) {
        if (shouldGrow) radius += step
    }

    fun show(c: Canvas) {
        c.drawCircle(x, y, radius, circlePaint)
    }

    companion object {
        private const val DEFAULT_RADIUS = 1f
        const val STROKE_WIDTH = 4f
    }
}
