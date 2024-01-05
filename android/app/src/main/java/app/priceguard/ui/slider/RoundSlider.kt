package app.priceguard.ui.slider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class RoundSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var width = 0f
    private var height = 0f

    private val slideBarPaint = Paint()
    private val controllerPaint = Paint()
    private val sliderValuePaint = Paint()

    private var controllerPointX = 0f
    private var controllerPointY = 0f

    private var slideBarPointX = 0f
    private var slideBarPointY = 0f

    private var customViewClickListener: sliderValueChangeListener? = null

    private var state = false

    private val pi = Math.PI.toFloat()

    private var sliderValue = 50
        set(value) {
            if (field != value) {
                field = value
                handleValueChangeEvent(value)
            }
        }

    private var slideBarStrokeWidth = Dp(8F).toPx(context).value

    private var slideBarRadius = 0F
    private var controllerRadius = Dp(12F).toPx(context).value

    private var margin = Dp(24F).toPx(context).value

    private var maxPecentValue = 100F

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("CustomSlider", "onMeasure")

        // 뷰 크기 모드 체크
        val viewWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val viewHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        // 뷰 크기 값 체크
        val viewWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeightSize = MeasureSpec.getSize(heightMeasureSpec)


        // 크기 모드에 따라 setMeasuredDimension() 메서드로 뷰의 영역 크기를 설정한다.
        if (viewWidthMode == MeasureSpec.EXACTLY && viewHeightMode == MeasureSpec.EXACTLY) {
            // XML에서 뷰의 크기가 특정 값으로 설정된 경우, 그대로 사용한다.
            setMeasuredDimension(viewWidthSize, viewHeightSize)
            width = viewWidthSize.toFloat()
            height = viewHeightSize.toFloat()
        } else {
            // wrap_content이거나, 지정되지 않은 경우, 뷰의 크기를 내부에서 지정해주어야 한다
            width = 1000F
            height = width / 2 + margin * 2 + controllerRadius
            setMeasuredDimension(width.toInt(), height.toInt())
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)


        slideBarPointX = width / 2
        slideBarPointY = height - margin - controllerRadius

        slideBarRadius = width / 2 - margin

        val rad = (180 / maxPecentValue * (maxPecentValue - sliderValue)).toRadian()
        controllerPointX = slideBarPointX + cos(rad) * slideBarRadius
        controllerPointY = slideBarPointY + sin(-rad) * slideBarRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("CustomSlider", "onDraw")

        drawSlideBar(canvas)
        drawPointSlideBar(canvas)
        drawController(canvas)
        drawSlideValueText(canvas)
    }

    private fun drawController(canvas: Canvas) {
        controllerPaint.style = Paint.Style.FILL
        controllerPaint.color = Color.BLUE

        canvas.drawCircle(controllerPointX, controllerPointY, controllerRadius, controllerPaint)
    }

    private fun drawSlideBar(canvas: Canvas) {
        slideBarPaint.style = Paint.Style.STROKE
        slideBarPaint.strokeWidth = slideBarStrokeWidth
        slideBarPaint.color = Color.GRAY

        val oval = RectF()
        oval.set(
            slideBarPointX - slideBarRadius,
            slideBarPointY - slideBarRadius,
            slideBarPointX + slideBarRadius,
            slideBarPointY + slideBarRadius
        )

        canvas.drawArc(oval, 180F, 180F, false, slideBarPaint)
    }

    private fun drawPointSlideBar(canvas: Canvas) {
        slideBarPaint.color = Color.RED

        val oval = RectF()
        oval.set(
            slideBarPointX - slideBarRadius,
            slideBarPointY - slideBarRadius,
            slideBarPointX + slideBarRadius,
            slideBarPointY + slideBarRadius
        )

        canvas.drawArc(oval, 270F, 30F, false, slideBarPaint)
    }

    private fun drawSlideValueText(canvas: Canvas) {
        sliderValuePaint.textSize = 88f
        val bounds = Rect()

        val textString = sliderValue.toString()
        sliderValuePaint.getTextBounds(textString, 0, textString.length, bounds)

        val textWidth = bounds.width()
        val textHeight = bounds.height()
        canvas.drawText(
            textString,
            slideBarPointX - textWidth / 2,
            slideBarPointY - slideBarRadius / 2 + textHeight,
            sliderValuePaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    // 터치 좌표 x에 맞게 컨트롤러의 y좌표 설정 (y좌표만 움직이면 controller 이동하지 않음)

                    if (event.y > slideBarPointY) {
                        if (state) {
                            if (event.x >= slideBarPointX) {
                                controllerPointX = slideBarPointX + slideBarRadius
                                controllerPointY = slideBarPointY
                                sliderValue = degreeToValue(0F)
                            } else {
                                controllerPointX = slideBarPointX - slideBarRadius
                                controllerPointY = slideBarPointY
                                sliderValue = degreeToValue(180F)
                            }
                            invalidate()
                            state = false
                        }
                        return true
                    }

                    state = true
                    val rad = calculateRadToPoint(event.x, event.y)

                    controllerPointX = slideBarPointX + cos(rad) * slideBarRadius
                    controllerPointY = slideBarPointY + sin(-rad) * slideBarRadius

                    sliderValue = degreeToValue(rad.toDegree())

                    invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    state = false
                }
            }
        }
        return true
    }

    private fun Float.toRadian(): Float {
        return this * pi / 180F
    }

    private fun Float.toDegree(): Float {
        return this * 180F / pi
    }

    private fun calculateRadToPoint(x: Float, y: Float): Float {
        val arcTan = atan((x - slideBarPointX) / (y - slideBarPointY))
        return (pi / 2) + arcTan
    }

    private fun degreeToValue(degree: Float): Int {
        return ((180F - degree) / (180F / maxPecentValue)).toInt()
    }

    fun setValue(value: Int) {
        Log.d("CustomSlider", "setValue")
        controllerPointX = slideBarPointX + cos(value * pi / maxPecentValue) * slideBarRadius
        controllerPointY = slideBarPointY + sin(-value * pi / maxPecentValue) * slideBarRadius
        sliderValue = value
        invalidate()
    }

    fun setSliderStrokeWidth(value: Int) {
        slideBarStrokeWidth = Dp(value.toFloat()).toPx(context).value
        invalidate()
    }

    fun setMaxPercentValue(value: Int) {
        maxPecentValue = value.toFloat()
        invalidate()
    }


    private fun handleValueChangeEvent(value: Int) {
        customViewClickListener?.invoke(value)
    }

    fun setSliderValueChangeListener(listener: sliderValueChangeListener) {
        customViewClickListener = listener
    }
}
