package com.qboxus.tictic.simpleclasses

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.qboxus.tictic.R

class CircleDivisionView : View {
    private var paint: Paint? = null

    var strokeWidth: Float = 5F
        set(value) {
            field = value
            postInvalidate()
        }

    var dividerSpace: Int = 6
        set(value) {
            field = value
            postInvalidate()
        }

    var counts: Int = 1
        set(value) {
            field = value

            angle=360/counts

          postInvalidate()

        }

    var strokeLineColor: Int = 0XFFFFFFFF.toInt()
        set(value) {
            field = value
            postInvalidate()
        }

    private var angle = 0
    private val padding =3

    constructor(context: Context?) : super(context) {
        render(null)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        render(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        render(attrs)
        init()
    }


    private fun render(attrs: AttributeSet?) {
        attrs?.let {
            /** set corner radii */
            with(context.obtainStyledAttributes(it, R.styleable.CircleDivisionView)) {
                strokeWidth =
                    getDimensionPixelSize(R.styleable.CircleDivisionView_strokeWidth, 5)
                        .toFloat()
                dividerSpace =
                    getInteger(R.styleable.CircleDivisionView_dividerSpace, dividerSpace)

                counts =
                    getInteger(R.styleable.CircleDivisionView_counts, counts)


                strokeLineColor = getColor(R.styleable.CircleDivisionView_strokeLineColor,  Color.WHITE)

                angle=360/counts
                recycle()
            }
        }
    }


    fun init() {
        paint = Paint()
        paint!!.strokeWidth = strokeWidth
        paint!!.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {


        val width = width
        val height = height
        val radius = Math.min(width, height) / 2 - padding
        val x = width / 2
        val y = height / 2
        val rectF = RectF(
            (x - radius).toFloat(),
            (y - radius).toFloat(),
            (x + radius).toFloat(),
            (y + radius).toFloat()
        )
        for (i in 0 until  (counts)) {

            if(i==0){

                paint!!.color =strokeLineColor
                canvas.drawArc(rectF, ((angle * i)).toFloat(), angle.toFloat(), false, paint!!)

            }
            else if(i==(counts-1)) {
                paint!!.color = strokeLineColor
                canvas.drawArc(
                    rectF,
                    ((angle + dividerSpace) * i).toFloat(),
                    (angle.toFloat()-(dividerSpace*(i+1))),
                    false,
                    paint!!
                )
            }
            else{
                paint!!.color = strokeLineColor
                canvas.drawArc(
                    rectF,
                    ((angle + dividerSpace) * i).toFloat(),
                    angle.toFloat(),
                    false,
                    paint!!
                )
            }

        }

        super.onDraw(canvas)
    }

}