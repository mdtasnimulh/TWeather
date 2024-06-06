package com.tasnimulhasan.home

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.tasnimulhasan.designsystem.R

class CustomComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var indicatorValue: Int = 0
    private var maxIndicatorValue: Int = 500
    private var backgroundIndicatorColor: Int = ContextCompat.getColor(context, R.color.black_opt_60)
    private var foregroundIndicatorColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
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
                foregroundIndicatorColor = getColor(R.styleable.CustomIndicatorView_foregroundIndicatorColor, foregroundIndicatorColor)
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

        foregroundPaint.color = foregroundIndicatorColor
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
