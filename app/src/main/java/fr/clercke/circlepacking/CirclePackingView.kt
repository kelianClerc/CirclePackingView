package fr.clercke.circlepacking

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.roundToInt
import kotlin.random.Random

class CirclePackingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    param: Int = 0
): View(context, attrs, param) {

    private var animation: ValueAnimator? = null

    private val content = Rect()
    private val circles: MutableList<Circle> = mutableListOf()
    private val spots: MutableList<Spot> = mutableListOf()
    private val circleColorList: List<Int> = listOf(
        ContextCompat.getColor(context, R.color.light_grey_blue),
        ContextCompat.getColor(context, R.color.white),
        ContextCompat.getColor(context, R.color.light_grey_v2),
        ContextCompat.getColor(context, R.color.silver)
    )
    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        if(width == oldw && height == oldh) return

        circles.clear()
        spots.clear()
        animation?.cancel()

        sizeBackground(width, height)
        computeAvailableSpots(height, width)

        animatePacking()

        super.onSizeChanged(width, height, oldw, oldh)
    }

    private fun sizeBackground(width: Int, height: Int) {
        content.right = width
        content.bottom = height
    }

    private fun computeAvailableSpots(height: Int, width: Int) {
        val d = loadAndResizeBitmap(height)

        d?.let {
            computeSpots(it, width)
        }
    }

    private fun loadAndResizeBitmap(height: Int): Bitmap? {
        val dr = resources.getDrawable(R.drawable.data)
        val bitmap = dr.toBitmap()
        val widthHeightRatio = bitmap.width.toFloat() / bitmap.height

        val (scaledWidth, scaledHeight) = if (widthHeightRatio > 1) {
            val nextWidth = (width * IMAGE_LARGEST_SIZE_RATIO).roundToInt()
            val nextHeight =  (nextWidth / widthHeightRatio).roundToInt()
            Pair(nextWidth, nextHeight)
        } else {
            val nextHeight =  (height * IMAGE_LARGEST_SIZE_RATIO).roundToInt()
            val nextWidth = (nextHeight * widthHeightRatio).roundToInt()
            Pair(nextWidth, nextHeight)
        }

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }

    private fun computeSpots(bitmap: Bitmap, width: Int) {
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if (bitmap.isWhitePixel(x, y)) {
                    val centeredX = x + (width / 2 - bitmap.width / 2)
                    val centeredY = y + (height / 2 - bitmap.height / 2)
                    spots.add(Spot(centeredX, centeredY))
                }
            }
        }
    }

    private fun animatePacking() {
        animation = ValueAnimator.ofFloat(0f, 100f).apply {
            addUpdateListener {
                addABunchOfCircles()
                invalidate()
            }
            repeatCount = INFINITE
            start()
        }
    }

    private fun addABunchOfCircles() {
        if (circles.size < NUMBER_OF_CIRCLE) {
            var count = 0
            var attempt = 0
            do {
                attempt++
                if (tryToAddACircle()) {
                    count++
                }
            } while (count < CIRCLE_ADDED_PER_CYCLE && attempt < ATTEMPT_THRESHOLD)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        canvas.drawRect(content, backgroundPaint)
        circles.forEach { circle ->
            if (circle.shouldGrow) {
                handleCircleCollision(circle)
            }
            circle.grow()
            circle.show(canvas)
        }
    }

    private fun handleCircleCollision(circle: Circle) {
        if (circle.isTouchingEdge(content.right, content.bottom)) {
            circle.shouldGrow = false
            return
        }

        for (other in circles) {
            if (other == circle) continue

            val dist = dist(circle.x, circle.y, other.x, other.y)
            if (dist - Circle.STROKE_WIDTH <= circle.radius + other.radius) {
                other.shouldGrow = false
                circle.shouldGrow = false
            }
        }
    }

    private fun tryToAddACircle(): Boolean {
        val pick = Random.nextInt(spots.size)
        val x = spots[pick].x.toFloat()
        val y = spots[pick].y.toFloat()

        if (isPointInsideACircle(x, y)) {
            return false
        }

        circles.add(Circle(x, y, circleColorList.random()))
        return true
    }

    private fun isPointInsideACircle(x: Float, y: Float): Boolean {
        return circles.any { dist(x, y, it.x, it.y) < it.radius }
    }

    fun dispose() {
        animation?.end()
        animation = null
    }

    companion object {
        private const val NUMBER_OF_CIRCLE = 1000
        private const val ATTEMPT_THRESHOLD = 500
        private const val CIRCLE_ADDED_PER_CYCLE = 3
        private const val IMAGE_LARGEST_SIZE_RATIO = 0.6
    }

    data class Spot(val x: Int, val y: Int)
}
