package com.example.animation.CustomAnimation

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.View.MeasureSpec.getSize
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnRepeat
import com.example.animation.R

class CustomAnimationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
    : View(context, attrs, defStyleAttr) {
    private var maxJump = 200f
    private var maxPlatform = 100f
    private var pl = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var r = 50f
    private var jump = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var custom_color = Color.GRAY
    init {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomAnimationView, defStyleAttr, defStyleRes)
        try {
            maxJump = a.getFloat(R.styleable.CustomAnimationView_jump, maxJump)
            custom_color = a.getColor(R.styleable.CustomAnimationView_color, custom_color)
            r = a.getFloat(R.styleable.CustomAnimationView_radius, r)
        } finally {
            a.recycle()
        }
    }


    private val customPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = custom_color
    }

    private var anim: Animator? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = getSize(heightMeasureSpec)
        val width: Int
        val height: Int

        width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            Math.min((r * 2).pxToDp(context).toInt() * 2, widthSize)
        } else {
            (r * 2).pxToDp(context).toInt() * 2
        }

        height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            Math.min(((maxJump + r) * 2).pxToDp(context).toInt() * 2, heightSize)
        } else {
            ((maxJump + r) * 2).pxToDp(context).toInt() * 2
        }

        setMeasuredDimension(width, height)
    }

    private fun drawCircle(canvas: Canvas, x0: Float, y0: Float, r: Float) {
        canvas.save()
        canvas.drawCircle(x0, y0, r, customPaint)
        canvas.restore()
    }

    private fun setCircleHeight(f1: Float, f2: Float, d: Long) : ValueAnimator {
        val tempAnim = ValueAnimator.ofFloat(f1, f2).apply {
            duration = d
            addUpdateListener {
                jump = it.animatedValue as Float
            }
        }
        return tempAnim
    }

    private fun drawPlatform(canvas: Canvas, x0: Float, y0: Float, r: Float) {
        canvas.save()
        canvas.drawRect(
            x0 - r,
            y0 - r,
            x0 + r,
            y0 - 2 * r,
            customPaint
        )
        canvas.restore()
    }

    private fun setPlatformHeight(f1: Float, f2: Float, d: Long) : ValueAnimator {
        val tempAnim = ValueAnimator.ofFloat(f1, f2).apply {
            duration = d
            addUpdateListener {
                pl = it.animatedValue as Float
            }
        }
        return tempAnim
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val a: MutableList<ValueAnimator> = mutableListOf()
        a.add(setPlatformHeight(maxPlatform, 0f, 250))
        a.add(setPlatformHeight(0f, maxPlatform, 100))
        a.add(setCircleHeight(0f, maxJump, 250))
        a.add(setCircleHeight(maxJump, 0f, 500))
        a[2].interpolator = DecelerateInterpolator()
        a[3].interpolator = BounceInterpolator()
        anim = AnimatorSet().apply {
            playSequentially(a as List<Animator>?)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    startDelay = 1000
                    start()
                }
            })
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val save = canvas.save()
        val x0 = width
        val y0 = height
        drawCircle(canvas, x0.toFloat() / 2, y0.toFloat() / 2 - jump, r)
        drawPlatform(canvas, x0.toFloat() / 2, y0.toFloat() / 2 + maxJump + r - pl, r)
        canvas.restoreToCount(save)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        anim?.cancel()
    }

    fun Float.pxToDp(context: Context):Float{
        return this / (context.resources
            .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toFloat()
    }
}