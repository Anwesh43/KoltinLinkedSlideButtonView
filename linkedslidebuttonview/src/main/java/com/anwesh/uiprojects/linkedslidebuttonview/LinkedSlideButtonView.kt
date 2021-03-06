package com.anwesh.uiprojects.linkedslidebuttonview

/**
 * Created by anweshmishra on 30/06/18.
 */

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.MotionEvent
import android.graphics.*

val SB_NODES : Int = 3
val buttonColor : Int = Color.parseColor("#f44336")
val slideColor : Int = Color.parseColor("#F5F5F5")

class LinkedSlideButtonView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    var linkedSlideButtonListener : LinkedSlideButtonListener? = null

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    fun addOnCompleteListener(onComplete : (Int) -> Unit) {
        linkedSlideButtonListener = LinkedSlideButtonListener(onComplete)
    }

    data class State(var j : Int = 0, var dir : Float = 0f, var prevScale : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SBNode (var i : Int, val state : State = State()) {

        private var next : SBNode? = null

        private var prev : SBNode? = null

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < SB_NODES - 1) {
                next = SBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = w / SB_NODES
            val btnW = gap/3
            val getX : (Int) -> Float = {index -> (gap) * state.scales[index]}
            val hBar : Float = h/10
            canvas.save()
            canvas.translate(i * gap, h/2)
            paint.color = slideColor
            canvas.drawRect(RectF(getX(2),-hBar/2, btnW + getX(0),hBar/2), paint)
            paint.color = buttonColor
            canvas.drawRect(RectF(getX(1), -hBar/2, getX(1) + btnW, hBar/2), paint)
            canvas.restore()
        }

        fun getNext(dir : Int, cb : () -> Unit) : SBNode {
            var curr : SBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedSlideButton (var i : Int) {

        private var curr : SBNode = SBNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {j, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(j, scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedSlideButtonView) {

        private val animator : Animator = Animator(view)

        private val lsb : LinkedSlideButton = LinkedSlideButton(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            lsb.draw(canvas, paint)
            animator.animate {
                lsb.update {j, scale ->
                    animator.stop()
                    when(scale) {
                        1f -> view.linkedSlideButtonListener?.onComplete?.invoke(j)
                    }
                }
            }
        }

        fun handleTap() {
            lsb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : LinkedSlideButtonView {
            val view : LinkedSlideButtonView = LinkedSlideButtonView(activity)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            activity.setContentView(view)
            return view
        }
    }

    data class LinkedSlideButtonListener(var onComplete : (Int) -> Unit)
}