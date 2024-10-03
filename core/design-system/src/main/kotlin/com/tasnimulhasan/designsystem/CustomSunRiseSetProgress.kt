package com.tasnimulhasan.designsystem

import android.animation.ValueAnimator
import android.annotation.SuppressLint
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
    private var drawableIndicatorColor: Int = ContextCompat.getColor(context, R.color.transparent)
    private var foregroundIndicatorColors = intArrayOf()
    private var backgroundIndicatorStrokeWidth: Float = 100f
    private var foregroundIndicatorStrokeWidth: Float = 100f
    private var drawableBackgroundIndicatorStrokeWidth: Float = 100f
    private var sweepAngle: Float = 0f
    private val rect = RectF()
    private var startX = 0f
    private var endX = 0f
    private var startY = 0f
    private var endY = 0f
    private var gradient: LinearGradient? = null
    private val gradientPositions = floatArrayOf(0.0f, 0.15f, 0.15f, 0.20f, 0.25f, 1.0f)
    private val backgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(10f, 46f), 0f)  // Dashed effect with gaps for the background
    }
    private val foregroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(10f, 46f), 0f)  // Dashed effect with gaps for the foreground
    }
    private val drawableBackgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(10f, 46f), 0f)  // Dashed effect with gaps for the foreground
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomIndicatorView, 0, 0).apply {
            try {
                indicatorValue = getInt(R.styleable.CustomIndicatorView_indicatorValue, indicatorValue)
                maxIndicatorValue = getInt(R.styleable.CustomIndicatorView_maxIndicatorValue, maxIndicatorValue)
                backgroundIndicatorColor = getColor(R.styleable.CustomIndicatorView_backgroundIndicatorColor, backgroundIndicatorColor)
                drawableIndicatorColor = getColor(R.styleable.CustomIndicatorView_drawableBackgroundIndicatorColors, drawableIndicatorColor)
                val colorsArrayId = getResourceId(R.styleable.CustomIndicatorView_foregroundIndicatorColors, 0)
                if (colorsArrayId != 0) { foregroundIndicatorColors = context.resources.getIntArray(colorsArrayId) }
                backgroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_backgroundIndicatorStrokeWidth, backgroundIndicatorStrokeWidth)
                drawableBackgroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_drawableBackgroundIndicatorStrokeWidth, drawableBackgroundIndicatorStrokeWidth)
                foregroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_foregroundIndicatorStrokeWidth, foregroundIndicatorStrokeWidth)
            } finally { recycle() }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Set dimensions for the circular progress bar
        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()
        val leftMargin = 25f
        val rightMargin = 25f
        val topMargin = 100f  // Increased top margin to avoid cropping the sun drawable
        val bottomMargin = 25f
        val padding = backgroundIndicatorStrokeWidth / 2
        val availableWidth = screenWidth - leftMargin - rightMargin
        val availableHeight = screenHeight - topMargin - bottomMargin
        val componentSize = availableWidth.coerceAtMost(availableHeight) - padding * 2
        val left = leftMargin + (availableWidth - componentSize) / 2
        val top = topMargin + (availableHeight - componentSize) / 2
        val right = left + componentSize
        val bottom = top + componentSize
        // Setting the foreground color based on sunrise to sunset
        foregroundIndicatorColors = if (indicatorValue != maxIndicatorValue) {
            intArrayOf(
                ContextCompat.getColor(context, R.color.yellow_dark_200),
                ContextCompat.getColor(context, R.color.yellow_dark_1000),
                ContextCompat.getColor(context, R.color.yellow),
                ContextCompat.getColor(context, R.color.yellow_dark_1000),
                ContextCompat.getColor(context, R.color.yellow_dark_800),
                ContextCompat.getColor(context, R.color.warning_color))
        } else {
            intArrayOf(
                ContextCompat.getColor(context, R.color.black_26),
                ContextCompat.getColor(context, R.color.black_26),
                ContextCompat.getColor(context, R.color.black_26),
                ContextCompat.getColor(context, R.color.black_26),
                ContextCompat.getColor(context, R.color.black_26),
                ContextCompat.getColor(context, R.color.black_26))
        }

        rect.set(left, top, right, bottom)

        // Calculate the radius adjusted for stroke width
        val radius = (rect.width() / 2) - (drawableBackgroundIndicatorStrokeWidth / 2)

        // Set up the background arc
        backgroundPaint.color = backgroundIndicatorColor
        backgroundPaint.strokeWidth = backgroundIndicatorStrokeWidth
        // Set up the drawable background arc
        drawableBackgroundPaint.color = drawableIndicatorColor
        drawableBackgroundPaint.strokeWidth = drawableBackgroundIndicatorStrokeWidth

        // Calculate the start and end points for the gradient based on the arc's geometry
        val startAngleRad = Math.toRadians(152.0)
        val endAngleRad = Math.toRadians(240.0)

        startX = (rect.centerX() + radius * cos(startAngleRad)).toFloat()
        startY = (rect.centerY() + radius * sin(startAngleRad)).toFloat()
        endX = (rect.centerX() + radius * cos(endAngleRad)).toFloat()
        endY = (rect.centerY() + radius * sin(endAngleRad)).toFloat()

        gradient = LinearGradient(startX, startY, endX, endY, foregroundIndicatorColors, gradientPositions, Shader.TileMode.CLAMP)
        foregroundPaint.shader = gradient
        foregroundPaint.strokeWidth = foregroundIndicatorStrokeWidth

        // Draw the background and foreground arcs
        canvas.drawArc(rect, 152f, 240f, false, backgroundPaint)
        canvas.drawArc(rect, 152f, sweepAngle, false, foregroundPaint)

        if (indicatorValue != maxIndicatorValue) {
            canvas.drawArc(rect, 152f, sweepAngle, false, drawableBackgroundPaint)
            val sunDrawable = ContextCompat.getDrawable(context, R.drawable.ic_sun) ?: return
            // Calculate the current progress angle (starting from 150 degrees)
            val progressAngle = 152f + sweepAngle
            // Convert angle to radians for trigonometric calculation
            val radians = Math.toRadians(progressAngle.toDouble())
            // Calculate the center of the arc
            val centerX = rect.centerX()
            val centerY = rect.centerY()
            // Calculate the x and y position for the sun drawable
            val sunX = (centerX + radius * cos(radians)).toFloat() - (sunDrawable.intrinsicWidth / 2)
            val sunY = (centerY + radius * sin(radians)).toFloat() - (sunDrawable.intrinsicHeight / 2)
            // Save the canvas state before applying rotation
            canvas.save()
            // Calculate the rotation angle based on progress
            val rotationAngle = (indicatorValue.toFloat() / maxIndicatorValue.toFloat()) * 360f
            // Apply rotation to the canvas, rotating around the center of the drawable
            canvas.rotate(rotationAngle, sunX + (sunDrawable.intrinsicWidth / 2), sunY + (sunDrawable.intrinsicHeight / 2))
            // Set the bounds for the drawable
            sunDrawable.setBounds(
                sunX.toInt(),
                sunY.toInt(),
                (sunX + sunDrawable.intrinsicWidth).toInt(),
                (sunY + sunDrawable.intrinsicHeight).toInt()
            )
            // Draw the sun drawable at the calculated position with rotation
            sunDrawable.draw(canvas)
            // Restore the canvas to its original state (undo the rotation)
            canvas.restore()
        }
    }

    fun setIndicatorValue(value: Int) {
        // Ensure the value is within bounds
        val newValue = value.coerceIn(minIndicatorValue, maxIndicatorValue)
        // Animate the progress indicator
        val animator = ValueAnimator.ofFloat(indicatorValue.toFloat(), newValue.toFloat())
        animator.duration = 5000
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