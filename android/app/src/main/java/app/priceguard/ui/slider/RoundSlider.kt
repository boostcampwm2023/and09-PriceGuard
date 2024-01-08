package app.priceguard.ui.slider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.min
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

    private var btnRadius = Dp(20F).toPx(context).value
    private var btnMarginTop = Dp(24F).toPx(context).value
    private var btnMarginBottom = Dp(12F).toPx(context).value

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

    private var slideBarMargin = Dp(12F).toPx(context).value + controllerRadius

    private var maxPecentValue = 100F

    private var startDegree = 270F
    private var endDegree = 300F

    private var textValueSize = Sp(32F)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 뷰 크기 모드 체크
        val viewWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val viewHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        // 뷰 크기 값 체크
        val viewWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeightSize = MeasureSpec.getSize(heightMeasureSpec)

        width = if (viewWidthMode == MeasureSpec.EXACTLY) {
            viewWidthSize.toFloat()
        } else if (viewHeightMode == MeasureSpec.EXACTLY) {
            (viewHeightSize.toFloat() - controllerRadius - btnRadius * 2 - btnMarginTop) * 2
        } else {
            700F
        }
        width = min(width, viewWidthSize.toFloat())

        height = if (viewHeightMode == MeasureSpec.EXACTLY) {
            viewHeightSize.toFloat()
        } else {
            width / 2 + btnRadius * 2 + btnMarginTop + btnMarginBottom
        }
        height = min(height, viewHeightSize.toFloat())

        setMeasuredDimension(width.toInt(), height.toInt())

        // 높이가 필요한 크기보다 클 경우 크기 줄이기 (중앙 정렬을 위함)
        if (viewHeightMode == MeasureSpec.EXACTLY) {
            val temp = width / 2 + btnRadius * 2 + btnMarginTop + btnMarginBottom
            if (height > temp) {
                height -= (height - temp) / 2
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        slideBarPointX = width / 2
        slideBarPointY = height - btnRadius * 2 - btnMarginTop - btnMarginBottom

        slideBarRadius = if (isHeightEnough()) {
            width / 2 - slideBarMargin
        } else {
            height - btnRadius * 2 - btnMarginTop - slideBarMargin - btnMarginBottom
        }

        val rad = (180 / maxPecentValue * (maxPecentValue - sliderValue)).toRadian()
        controllerPointX = slideBarPointX + cos(rad) * slideBarRadius
        controllerPointY = slideBarPointY + sin(-rad) * slideBarRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSlideBar(canvas)
        drawPointSlideBar(canvas)
        drawController(canvas)
        drawSlideValueText(canvas)
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
        canvas.drawArc(oval, startDegree, endDegree - startDegree, false, slideBarPaint)
    }

    private fun drawController(canvas: Canvas) {
        controllerPaint.style = Paint.Style.FILL
        controllerPaint.color = Color.BLUE

        canvas.drawCircle(controllerPointX, controllerPointY, controllerRadius, controllerPaint)
    }

    private fun drawSlideValueText(canvas: Canvas) {
        sliderValuePaint.textSize = textValueSize.toPx(context).value
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

    private fun isHeightEnough(): Boolean {
        return height >= width / 2 + btnMarginBottom + btnRadius * 2 + btnMarginTop
    }

    private fun calculateRadToPoint(x: Float, y: Float): Float {
        val arcTan = atan((x - slideBarPointX) / (y - slideBarPointY))
        return (pi / 2) + arcTan
    }

    private fun degreeToValue(degree: Float): Int {
        return ((180F - degree) / (180F / maxPecentValue)).toInt()
    }

    fun setValue(value: Int) {
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

    fun setPointRange(startValue: Int, endValue: Int) {
        startDegree = (180 / maxPecentValue * startValue) + 180
        endDegree = (180 / maxPecentValue * endValue) + 180
        invalidate()
    }

    fun setSliderValueChangeListener(listener: sliderValueChangeListener) {
        customViewClickListener = listener
    }

    private fun handleValueChangeEvent(value: Int) {
        customViewClickListener?.invoke(value)
    }
}
