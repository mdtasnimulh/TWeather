package com.tasnimulhasan.designsystem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CustomCircularProgress @JvmOverloads constructor(
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

        backgroundPaint.color = backgroundIndicatorColor
        backgroundPaint.strokeWidth = backgroundIndicatorStrokeWidth

        val gradientPositions = floatArrayOf(0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)
        foregroundPaint.shader = LinearGradient(
            rect.left, rect.bottom, rect.right, rect.bottom,
            foregroundIndicatorColors, gradientPositions, Shader.TileMode.CLAMP
        )
        foregroundPaint.strokeWidth = foregroundIndicatorStrokeWidth

        canvas.drawArc(rect, 150f, 240f, false, backgroundPaint)
        canvas.drawArc(rect, 150f, sweepAngle, false, foregroundPaint)
    }

    fun setIndicatorValue(value: Int) {
        val animator = ValueAnimator.ofFloat(indicatorValue.toFloat(), value.toFloat())
        animator.duration = 1500
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            indicatorValue = animatedValue.toInt()
            sweepAngle = (indicatorValue.toFloat() / maxIndicatorValue) * 240f
            invalidate()
        }
        animator.start()
    }
}

