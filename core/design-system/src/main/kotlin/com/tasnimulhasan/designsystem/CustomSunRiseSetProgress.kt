package com.tasnimulhasan.designsystem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin

class CustomSunRiseSetProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var indicatorValue: Int = 0
    private var maxIndicatorValue: Int = 0
    private var minIndicatorValue: Int = 0
    private var backgroundIndicatorColor: Int = ContextCompat.getColor(context, R.color.green_light_200)
    private var drawableIndicatorColor: Int = ContextCompat.getColor(context, R.color.white_n_gray_light_900)
    private var foregroundIndicatorColors: IntArray = intArrayOf(
        ContextCompat.getColor(context, R.color.green_light_700),
        ContextCompat.getColor(context, R.color.green_light_800),
        ContextCompat.getColor(context, R.color.yellow_light_700),
        ContextCompat.getColor(context, R.color.yellow_light_800),
        ContextCompat.getColor(context, R.color.yellow_light_700),
        ContextCompat.getColor(context, R.color.primary_orange_light_800),
    )
    private var backgroundIndicatorStrokeWidth: Float = 100f
    private var foregroundIndicatorStrokeWidth: Float = 100f
    private var drawableBackgroundIndicatorStrokeWidth: Float = 100f
    private var sweepAngle: Float = 0f
    private val rect = RectF()

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(10f, 40f), 0f)  // Dashed effect with gaps for the background
    }

    private val foregroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(10f, 40f), 0f)  // Dashed effect with gaps for the foreground
    }

    private val drawableBackgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(10f, 40f), 0f)  // Dashed effect with gaps for the foreground
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomIndicatorView,
            0, 0
        ).apply {
            try {
                indicatorValue = getInt(R.styleable.CustomIndicatorView_indicatorValue, indicatorValue)
                maxIndicatorValue = getInt(R.styleable.CustomIndicatorView_maxIndicatorValue, maxIndicatorValue)
                backgroundIndicatorColor = getColor(R.styleable.CustomIndicatorView_backgroundIndicatorColor, backgroundIndicatorColor)
                drawableIndicatorColor = getColor(R.styleable.CustomIndicatorView_drawableBackgroundIndicatorColors, drawableIndicatorColor)
                val colorsArrayId = getResourceId(R.styleable.CustomIndicatorView_foregroundIndicatorColors, 0)
                if (colorsArrayId != 0) {
                    foregroundIndicatorColors = context.resources.getIntArray(colorsArrayId)
                }
                backgroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_backgroundIndicatorStrokeWidth, backgroundIndicatorStrokeWidth)
                drawableBackgroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_drawableBackgroundIndicatorStrokeWidth, drawableBackgroundIndicatorStrokeWidth)
                foregroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_foregroundIndicatorStrokeWidth, foregroundIndicatorStrokeWidth)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set dimensions for the circular progress bar
        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()
        val leftMargin = 25f
        val rightMargin = 25f
        val padding = backgroundIndicatorStrokeWidth / 2
        val availableWidth = screenWidth - leftMargin - rightMargin
        val componentSize = availableWidth.coerceAtMost(screenHeight) - padding * 2
        val left = leftMargin + (availableWidth - componentSize) / 2
        val top = (screenHeight - componentSize) / 2 + padding
        val right = left + componentSize
        val bottom = top + componentSize

        rect.set(left, top, right, bottom)

        // Set up the background arc
        backgroundPaint.color = backgroundIndicatorColor
        backgroundPaint.strokeWidth = backgroundIndicatorStrokeWidth

        // Set up the drawable background arc
        drawableBackgroundPaint.color = drawableIndicatorColor
        drawableBackgroundPaint.strokeWidth = drawableBackgroundIndicatorStrokeWidth

        // Set up the foreground arc with gradient colors
        val gradientPositions = floatArrayOf(0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)
        foregroundPaint.shader = LinearGradient(
            rect.left, rect.bottom, rect.right, rect.bottom,
            foregroundIndicatorColors, gradientPositions, Shader.TileMode.CLAMP
        )
        foregroundPaint.strokeWidth = foregroundIndicatorStrokeWidth

        // Draw the background and foreground arcs
        canvas.drawArc(rect, 150f, 240f, false, backgroundPaint)
        canvas.drawArc(rect, 150f, sweepAngle, false, foregroundPaint)
        canvas.drawArc(rect, 150f, sweepAngle, false, drawableBackgroundPaint)

        // Draw the sun drawable that moves along the arc
        val sunDrawable = ContextCompat.getDrawable(context, R.drawable.ic_sun_solid) ?: return

        // Calculate the current progress angle (starting from 150 degrees)
        val progressAngle = 150f + sweepAngle

        // Convert angle to radians for trigonometric calculation
        val radians = Math.toRadians(progressAngle.toDouble())

        // Calculate the center of the arc
        val centerX = rect.centerX()
        val centerY = rect.centerY()

        // Calculate the radius adjusted for stroke width
        val radius = (rect.width() / 2) - (drawableBackgroundIndicatorStrokeWidth / 2)

        // Calculate the x and y position for the sun drawable
        val sunX = (centerX + radius * cos(radians)).toFloat() - (sunDrawable.intrinsicWidth / 2)
        val sunY = (centerY + radius * sin(radians)).toFloat() - (sunDrawable.intrinsicHeight / 2)

        // Set the bounds for the drawable
        sunDrawable.setBounds(
            sunX.toInt(),
            sunY.toInt(),
            (sunX + sunDrawable.intrinsicWidth).toInt(),
            (sunY + sunDrawable.intrinsicHeight).toInt()
        )

        // Draw the sun drawable at the calculated position
        sunDrawable.draw(canvas)
    }

    fun setIndicatorValue(value: Int) {
        // Ensure the value is within bounds
        val newValue = value.coerceIn(minIndicatorValue, maxIndicatorValue)

        // Animate the progress indicator
        val animator = ValueAnimator.ofFloat(indicatorValue.toFloat(), newValue.toFloat())
        animator.duration = 1500
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            indicatorValue = animatedValue.toInt()
            sweepAngle = ((indicatorValue - minIndicatorValue).toFloat() / (maxIndicatorValue - minIndicatorValue)) * 240f
            invalidate() // Trigger redraw
        }
        animator.start()
    }

    fun setMaxIndicatorValue(maxValue: Int) {
        maxIndicatorValue = maxValue
        invalidate() // Trigger redraw if necessary
    }

    fun setMinIndicatorValue(minValue: Int) {
        minIndicatorValue = minValue
        invalidate() // Trigger redraw if necessary
    }

}
