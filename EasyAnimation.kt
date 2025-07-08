package com.russia.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation

object EasyAnimation {
    fun View.scaleAndFadeAnimation(
        fadeIn: Boolean = true,
        duration: Long = 300,
        onStartAction: (() -> Unit)? = null,
        onEndAction: (() -> Unit)? = null
    ) {
        this.pivotX = this.width / 2f
        this.pivotY = this.height / 2f

        val startScale = if (fadeIn) 2.2f else 1.0f
        val endScale = if (fadeIn) 1.0f else 2.2f
        val startAlpha = if (fadeIn) 0f else 1f
        val endAlpha = if (fadeIn) 1f else 0f


        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", startScale, endScale)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", startScale, endScale)
        val alpha = ObjectAnimator.ofFloat(this, "alpha", startAlpha, endAlpha)

        AnimatorSet().apply {
            interpolator = DecelerateInterpolator()
            this.duration = duration
            playTogether(scaleX, scaleY, alpha)

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    onStartAction?.invoke()
                }

                override fun onAnimationEnd(animation: Animator) {
                    onEndAction?.invoke()
                }
            })

            start()
        }
    }

    fun View.animateVisible(
        isVisible: Boolean,
        withStartAction: (() -> Unit)? = null,
        withEndAction: (() -> Unit)? = null,
        duration: Long = 300L
    ) {
        this.alpha = if (isVisible) 0f else 1f

        this.animate()
            .withStartAction { withStartAction?.invoke() }
            .alpha(if (isVisible) 1f else 0f)
            .setDuration(duration).withEndAction { withEndAction?.invoke() }
            .start()
    }

    fun View.animateClick(
        duration: Long = 100L,
        withStartAction: (() -> Unit)? = null,
        withEndAction: (() -> Unit)? = null
    ) {
        val scaleAnimation = ScaleAnimation(
            1.0f,
            0.8f,
            1.0f,
            0.8f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        scaleAnimation.duration = duration
        scaleAnimation.repeatCount = 1
        scaleAnimation.repeatMode =
            Animation.REVERSE
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                if (withStartAction != null) withStartAction()
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (withEndAction != null) withEndAction()
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        this.setOnClickListener {
            it.startAnimation(scaleAnimation)
        }
    }

    fun View.startInfiniteRotation(duration: Long, isInfinite: Boolean) {
        val rotateAnimation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = duration
        if (isInfinite) rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.interpolator = LinearInterpolator()
        this.startAnimation(rotateAnimation)
    }

    fun View.slideLeftWithFadeOutAnimation(
        translationX: Float? = null,
        duration: Long,
        withEndAction: (() -> Unit)? = null
    ) {
        val translation = translationX ?: (-this.width.toFloat() / 3)
        this.animate()
            .translationX(translation)
            .alpha(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(duration)
            .withEndAction {
                this.translationX = 0f
                this.alpha = 1f
                if (withEndAction != null) withEndAction()
            }
    }

    fun View.animateOpacityAndTranslationX(
        translationX: Float? = null,
        visible: Boolean,
        direction: Int,
        duration: Long,
        withStartAction: (() -> Unit)? = null,
        withEndAction: (() -> Unit)? = null,
    ) {
        val translation = translationX ?: (this.width.toFloat() / 3)
        this.alpha = if (visible) 0f else 1f
        this.translationX = if (visible) direction * translation else 0f

        this.animate()
            .translationX(if (visible) 0f else direction * translation)
            .alpha(if (visible) 1f else 0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(duration)
            .withStartAction {
                withStartAction?.invoke()
            }
            .withEndAction {
                withEndAction?.invoke()
            }
            .start()
    }

    fun View.slideWithFade(
        direction: SlideDirection,
        duration: Long = 300,
        withStartAction: (() -> Unit)? = null,
        withEndAction: (() -> Unit)? = null
    ) {
        val translationAnimator = when (direction) {
            SlideDirection.TOP_TO_BOTTOM -> ObjectAnimator.ofFloat(
                this,
                "translationY",
                -height.toFloat(),
                0f
            )

            SlideDirection.BOTTOM_TO_TOP -> ObjectAnimator.ofFloat(
                this,
                "translationY",
                height.toFloat(),
                0f
            )

            SlideDirection.LEFT_TO_RIGHT -> ObjectAnimator.ofFloat(
                this,
                "translationX",
                -width.toFloat(),
                0f
            )

            SlideDirection.RIGHT_TO_LEFT -> ObjectAnimator.ofFloat(
                this,
                "translationX",
                width.toFloat(),
                0f
            )

            SlideDirection.OUT_SCREEN_DOWN -> ObjectAnimator.ofFloat(
                this,
                "translationY",
                0f,
                2 * height.toFloat()
            )

            SlideDirection.OUT_SCREEN_LEFT -> ObjectAnimator.ofFloat(
                this,
                "translationX",
                0f,
                -width.toFloat()
            )

            SlideDirection.OUT_SCREEN_RIGHT -> ObjectAnimator.ofFloat(
                this,
                "translationX",
                0f,
                width.toFloat()
            )

            SlideDirection.OUT_SCREEN_UP -> ObjectAnimator.ofFloat(
                this,
                "translationY",
                0f,
                -height.toFloat()
            )

            SlideDirection.SCREEN_TO_DEFAULT -> ObjectAnimator.ofFloat(
                this,
                "translationY",
                height.toFloat(),
                0f
            )
        }

        val alphaAnimator = if (direction in setOf(
                SlideDirection.OUT_SCREEN_DOWN,
                SlideDirection.OUT_SCREEN_LEFT,
                SlideDirection.OUT_SCREEN_RIGHT,
                SlideDirection.OUT_SCREEN_UP
            )
        ) {
            ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        } else {
            ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnimator, alphaAnimator)
        animatorSet.duration = duration

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                withStartAction?.invoke()
            }

            override fun onAnimationEnd(animation: Animator) {
                withEndAction?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

    fun View.addRippleEffect(
        rippleColor: Int = Color.parseColor("#FFFFFF"),
        rippleDuration: Long = 150L,
        onClick: (() -> Unit)? = null
    ) {
        this.setOnClickListener {
            val background: Drawable? = this.background

            val rippleDrawable = RippleDrawable(
                ColorStateList.valueOf(rippleColor),
                background,
                null
            )
            this.background = rippleDrawable

            rippleDrawable.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
            Handler(Looper.getMainLooper()).postDelayed({
                rippleDrawable.state = intArrayOf()
                onClick?.invoke()
            }, rippleDuration)
        }
    }

    enum class SlideDirection {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        OUT_SCREEN_DOWN,
        OUT_SCREEN_LEFT,
        OUT_SCREEN_RIGHT,
        OUT_SCREEN_UP,
        SCREEN_TO_DEFAULT
    }
}