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
import app.priceguard.R
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class RoundSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var width = 0F
    private var height = 0F

    private val slideBarPaint = Paint()
    private val activeSlideBarPaint = Paint()
    private val controllerPaint = Paint()
    private val sliderValuePaint = Paint()
    private val axisCirclePaint = Paint()

    // 슬라이더 컨트롤러 좌표
    private var controllerPointX = 0F
    private var controllerPointY = 0F

    // 슬라이더 바의 중심 좌표 (호의 중점)
    private var slideBarPointX = 0F
    private var slideBarPointY = 0F

    // 슬라이더 틱 간격
    private var sliderValueStepSize = 0

    private var sliderValueChangeListener: SliderValueChangeListener? = null

    // 드래그 중인지, 드래그 중인 좌표가 슬라이더 뷰 안에 있는지 확인
    private var isDraggingOnSlider = false

    // 모드에 따른 슬라이더 바 및 활성화 색상 변경
    private var sliderMode = RoundSliderState.ACTIVE

    private val pi = Math.PI

    var sliderValue = 0
        private set(value) { // 슬라이더 value가 변경되면 리스너에 변경된 값과 함께 이벤트 보내기
            if (field != value) {
                field = value
                handleValueChangeEvent(value)
            }
        }

    private var slideBarStrokeWidth = Dp(8F).toPx(context).value

    private var slideBarRadius = 0F
    private var controllerRadius = Dp(12F).toPx(context).value

    private var slideBarMargin = Dp(12F).toPx(context).value + controllerRadius

    private var maxPercentValue = 100

    // 하이라이트된 슬라이더 바를 그리기 위한 시작과 끝 각도
    private var startDegree = 0F
    private var endDegree = 0F

    private var textValueSize = Sp(32F)

    private val touchSizeMargin = Dp(8F).toPx(context).value

    private var isTouchedOnSlideBar = false

    // 현재 sliderValue값으로 위치해야하는 컨트롤러 좌표에 대한 라디안 값
    private var currentRad = pi
        set(value) {
            field = value.coerceIn(0.0..pi)
            updateValueWithRadian(field)
            updateControllerPointWithRadian(field)
            invalidate()
        }

    private var colorPrimary: Int
    private var colorOnPrimaryContainer: Int
    private var colorSurfaceVariant: Int
    private var colorError: Int

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundSlider,
            defStyleAttr,
            0
        )

        colorPrimary = typedArray.getColor(
            R.styleable.RoundSlider_colorPrimary,
            Color.BLUE
        )

        colorOnPrimaryContainer = typedArray.getColor(
            R.styleable.RoundSlider_colorOnPrimaryContainer,
            Color.BLACK
        )

        colorSurfaceVariant = typedArray.getColor(
            R.styleable.RoundSlider_colorSurfaceVariant,
            Color.GRAY
        )

        colorError = typedArray.getColor(
            R.styleable.RoundSlider_colorError,
            Color.RED
        )

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // wrap_content인지 match_parent인지 고정된 값인지 확인
        val viewWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val viewHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        // 값이 있으면 설정된 값 할당 없으면 최대 값 할당
        val viewWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeightSize = MeasureSpec.getSize(heightMeasureSpec)

        width = if (viewWidthMode == MeasureSpec.EXACTLY) {
            viewWidthSize.toFloat()
        } else if (viewHeightMode == MeasureSpec.EXACTLY) { // height만 크기 값이 설정된 경우 height에 맞게 width 설정
            (viewHeightSize.toFloat() - controllerRadius - slideBarMargin) * 2
        } else { // width, height 모두 wrap_content와 같이 설정된 크기 값이 없을 경우 700으로 고정
            700F
        }
        // width의 크기가 최대 크기보다 작거나 같아야 함
        width = min(width, viewWidthSize.toFloat())

        height = if (viewHeightMode == MeasureSpec.EXACTLY) {
            viewHeightSize.toFloat()
        } else {
            width / 2 + slideBarMargin
        }
        height = min(height, viewHeightSize.toFloat())

        setMeasuredDimension(width.toInt(), height.toInt())

        // 높이가 필요한 크기보다 클 경우 크기 줄이기 (중앙 정렬을 위함)
        if (viewHeightMode == MeasureSpec.EXACTLY) {
            val temp = width / 2 + slideBarMargin
            if (height > temp) {
                height -= (height - temp) / 2
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        slideBarPointX = width / 2
        slideBarPointY = height - slideBarMargin

        slideBarRadius = if (isHeightEnough()) { // 높이가 충분히 높을 경우 너비에 맞게 슬라이더 크기 설정
            width / 2 - slideBarMargin
        } else { // 높이가 충분히 높지 않을 경우 높이에 맞게 슬라이더 크기 설정
            height - slideBarMargin * 2
        }

        // 슬라이더 value에 맞게 라디안 설정
        currentRad = valueToDegree(sliderValue).toRadian()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSlideBar(canvas)
        drawSliderBarTick(canvas)
        drawController(canvas)
        drawSlideValueText(canvas)
    }

    private fun drawSlideBar(canvas: Canvas) {
        slideBarPaint.style = Paint.Style.STROKE
        slideBarPaint.strokeWidth = slideBarStrokeWidth
        slideBarPaint.color = colorSurfaceVariant

        activeSlideBarPaint.style = Paint.Style.STROKE
        activeSlideBarPaint.strokeWidth = slideBarStrokeWidth
        activeSlideBarPaint.color = when (sliderMode) {
            RoundSliderState.ACTIVE -> colorPrimary
            RoundSliderState.INACTIVE -> colorSurfaceVariant
            RoundSliderState.ERROR -> colorError
        }

        val oval = RectF()
        oval.set(
            slideBarPointX - slideBarRadius,
            slideBarPointY - slideBarRadius,
            slideBarPointX + slideBarRadius,
            slideBarPointY + slideBarRadius
        )

        canvas.drawArc(oval, 180F, 180F, false, slideBarPaint)
        drawHighlightSlider(canvas)
        canvas.drawArc(
            oval,
            180F,
            180 - currentRad.toDegree().toFloat(),
            false,
            activeSlideBarPaint
        )
    }

    private fun drawHighlightSlider(canvas: Canvas) {
        slideBarPaint.color = Color.parseColor("#FFD7F3")
        slideBarPaint.alpha

        val oval = RectF()
        oval.set(
            slideBarPointX - slideBarRadius,
            slideBarPointY - slideBarRadius,
            slideBarPointX + slideBarRadius,
            slideBarPointY + slideBarRadius
        )

        canvas.drawArc(oval, startDegree, endDegree - startDegree, false, slideBarPaint)
    }

    private fun drawSliderBarTick(canvas: Canvas) {
        axisCirclePaint.color = colorOnPrimaryContainer

        // stepSize에 맞게 틱 그리기
        for (i in sliderValueStepSize until maxPercentValue step sliderValueStepSize) {
            val rad = valueToDegree(i).toRadian()
            val tickPointX = slideBarPointX + cos(rad).toFloat() * slideBarRadius
            val tickPointY = slideBarPointY - sin(rad).toFloat() * slideBarRadius

            canvas.drawCircle(tickPointX, tickPointY, Dp(2F).toPx(context).value, axisCirclePaint)
        }
    }

    private fun drawController(canvas: Canvas) {
        controllerPaint.style = Paint.Style.FILL
        controllerPaint.color = when (sliderMode) {
            RoundSliderState.ACTIVE -> colorPrimary
            RoundSliderState.INACTIVE -> colorSurfaceVariant
            RoundSliderState.ERROR -> colorError
        }

        canvas.drawCircle(controllerPointX, controllerPointY, controllerRadius, controllerPaint)
    }

    private fun drawSlideValueText(canvas: Canvas) {
        sliderValuePaint.textSize = textValueSize.toPx(context).value
        sliderValuePaint.color = when (sliderMode) {
            RoundSliderState.ACTIVE -> colorOnPrimaryContainer
            RoundSliderState.INACTIVE -> colorSurfaceVariant
            RoundSliderState.ERROR -> colorError
        }
        val bounds = Rect()
        val textString = "$sliderValue%"
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
                MotionEvent.ACTION_DOWN -> {
                    isTouchedOnSlideBar = isTouchOnSlideBar(event.x, event.y)
                    parent.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_MOVE -> {
                    if(!isTouchedOnSlideBar) return true

                    if (event.y > slideBarPointY) { // 드래그 좌표가 슬라이더를 벗어난 경우 value를 x좌표에 맞게 최소 or 최대 값으로 설정
                        if (isDraggingOnSlider) {
                            isDraggingOnSlider = false
                            currentRad = if (event.x >= slideBarPointX) 0.0 else pi
                        }
                    } else {
                        isDraggingOnSlider = true
                        updateRadianWithTouchPoint(event.x, event.y)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    isDraggingOnSlider = false
                }
            }
        }
        return true
    }

    private fun isTouchOnSlideBar(x: Float, y: Float): Boolean {
        val deltaX = abs(slideBarPointX - x)
        val deltaY = abs(slideBarPointY - y)

        val distance = sqrt(deltaX.pow(2) + deltaY.pow(2))

        val minDistance = slideBarRadius - slideBarStrokeWidth - touchSizeMargin
        val maxDistance = slideBarRadius + slideBarStrokeWidth + touchSizeMargin

        return distance in minDistance..maxDistance
    }

    private fun updateControllerPointWithRadian(rad: Double) {
        controllerPointX = slideBarPointX + cos(rad).toFloat() * slideBarRadius
        controllerPointY = slideBarPointY - sin(rad).toFloat() * slideBarRadius
    }

    private fun updateValueWithRadian(rad: Double) {
        sliderValue = degreeToValue(rad.toDegree())
    }

    private fun calculateRadianWithPoint(x: Float, y: Float): Double {
        val arcTanRadian = atan((x - slideBarPointX) / (y - slideBarPointY))
        return (pi / 2) + arcTanRadian
    }

    private fun updateRadianWithTouchPoint(x: Float, y: Float) {
        val rad = calculateRadianWithPoint(x, y)
        currentRad = findCloseStepValueRadian(degreeToValue(rad.toDegree()))
    }

    private fun isHeightEnough() = height >= width / 2 + slideBarMargin

    private fun degreeToValue(degree: Double) =
        (((180F - degree) * maxPercentValue / 180F)).roundToInt()

    private fun valueToDegree(value: Int) =
        (180 - ((180 * value.toDouble()) / maxPercentValue))

    private fun findCloseStepValueRadian(currentValue: Int): Double {
        var minDifference = maxPercentValue
        var prevDifference = maxPercentValue

        for (value in 0..maxPercentValue step sliderValueStepSize) {
            val difference = abs(currentValue - value)

            if (difference > prevDifference) { // difference 증가할 경우 이전 radian 반환
                return valueToDegree(value - sliderValueStepSize).toRadian()
            }

            if (difference < minDifference) {
                minDifference = difference
            }
            prevDifference = difference
        }
        return 0.0
    }

    fun setValue(value: Int) {
        currentRad = (180 / maxPercentValue.toDouble() * (maxPercentValue - value)).toRadian()
        sliderValue = value
    }

    fun setSliderStrokeWidth(value: Int) {
        slideBarStrokeWidth = Dp(value.toFloat()).toPx(context).value
        invalidate()
    }

    fun setMaxPercentValue(value: Int) {
        maxPercentValue = value
        invalidate()
    }

    fun setHighlightRange(startValue: Int, endValue: Int) {
        startDegree = (180F / maxPercentValue * startValue) + 180
        endDegree = (180F / maxPercentValue * endValue) + 180
        invalidate()
    }

    fun setSliderValueChangeListener(listener: SliderValueChangeListener) {
        sliderValueChangeListener = listener
    }

    fun setSliderMode(mode: RoundSliderState) {
        sliderMode = mode
    }

    fun setStepSize(step: Int) {
        if (maxPercentValue.mod(step) != 0) return
        sliderValueStepSize = step
        invalidate()
    }

    private fun handleValueChangeEvent(value: Int) {
        sliderValueChangeListener?.invoke(value)
    }
}
