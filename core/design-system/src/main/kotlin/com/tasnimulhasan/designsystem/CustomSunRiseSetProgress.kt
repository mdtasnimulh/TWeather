package com.tasnimulhasan.designsystem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CustomSunRiseSetProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var indicatorValue: Int = 0
    private var maxIndicatorValue: Int = 5
    private var backgroundIndicatorColor: Int = ContextCompat.getColor(context, R.color.green_light_200)
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
    private var sweepAngle: Float = 0f
    private val rect = RectF()

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val foregroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        // Add dotted line effect
        pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)  // Adjust the dash and gap sizes
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
                val colorsArrayId = getResourceId(R.styleable.CustomIndicatorView_foregroundIndicatorColors, 0)
                if (colorsArrayId != 0) {
                    foregroundIndicatorColors = context.resources.getIntArray(colorsArrayId)
                }
                backgroundIndicatorStrokeWidth = getDimension(R.styleable.CustomIndicatorView_backgroundIndicatorStrokeWidth, backgroundIndicatorStrokeWidth)
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
        val radius = (rect.width() / 2) - foregroundIndicatorStrokeWidth / 2

        // Calculate the x and y position for the sun drawable
        val sunX = (centerX + radius * Math.cos(radians)).toFloat() - (sunDrawable.intrinsicWidth / 2)
        val sunY = (centerY + radius * Math.sin(radians)).toFloat() - (sunDrawable.intrinsicHeight / 2)

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
        // Animate the progress indicator
        val animator = ValueAnimator.ofFloat(indicatorValue.toFloat(), value.toFloat())
        animator.duration = 1500
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            indicatorValue = animatedValue.toInt()
            sweepAngle = (indicatorValue.toFloat() / maxIndicatorValue) * 240f
            invalidate() // Trigger redraw
        }
        animator.start()
    }
}
