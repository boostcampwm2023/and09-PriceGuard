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
import kotlin.math.roundToInt
import kotlin.math.sin

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

    private var controllerPointX = 0F
    private var controllerPointY = 0F

    private var slideBarPointX = 0F
    private var slideBarPointY = 0F

    private var sliderValueStepSize = 10

    private var customViewClickListener: sliderValueChangeListener? = null

    private var state = false

    private var sliderMode = RoundSliderState.ACTIVE

    private val pi = Math.PI

    private var sliderValue = 0
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

    private var maxPercentValue = 100

    private var startDegree = 0F
    private var endDegree = 0F

    private var textValueSize = Sp(32F)

    private var currentRad = pi

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

        val viewWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val viewHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val viewWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeightSize = MeasureSpec.getSize(heightMeasureSpec)

        width = if (viewWidthMode == MeasureSpec.EXACTLY) {
            viewWidthSize.toFloat()
        } else if (viewHeightMode == MeasureSpec.EXACTLY) {
            (viewHeightSize.toFloat() - controllerRadius - slideBarMargin) * 2
        } else {
            700F
        }
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

        slideBarRadius = if (isHeightEnough()) {
            width / 2 - slideBarMargin
        } else {
            height - slideBarMargin * 2
        }

        currentRad =
            (180 / maxPercentValue.toDouble() * (maxPercentValue - sliderValue)).toRadian().coerceIn(0.0, pi)
        controllerPointX = slideBarPointX + cos(currentRad).toFloat() * slideBarRadius
        controllerPointY = slideBarPointY + sin(-currentRad).toFloat() * slideBarRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSlideBar(canvas)
        drawAxis(canvas)
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
        canvas.drawArc(oval, 180F, 180 - currentRad.toDegree().toFloat(), false, activeSlideBarPaint)
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

    private fun drawAxis(canvas: Canvas) {
        axisCirclePaint.color = colorOnPrimaryContainer

        val step = (180F / (maxPercentValue / sliderValueStepSize)).toInt()

        for (i: Int in step until 180 step (step)) {
            val rad = i.toRadian()
            val axisPointX = slideBarPointX + cos(rad).toFloat() * slideBarRadius
            val axisPointY = slideBarPointY + sin(-rad).toFloat() * slideBarRadius

            canvas.drawCircle(axisPointX, axisPointY, Dp(2F).toPx(context).value, axisCirclePaint)
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
                    parent.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (event.y > slideBarPointY) {
                        if (state) {
                            if (event.x >= slideBarPointX) {
                                controllerPointX = slideBarPointX + slideBarRadius
                                controllerPointY = slideBarPointY
                                sliderValue = degreeToValue(0.0)
                            } else {
                                controllerPointX = slideBarPointX - slideBarRadius
                                controllerPointY = slideBarPointY
                                sliderValue = degreeToValue(180.0)
                            }
                            invalidate()
                            state = false
                        }
                        return true
                    }
                    state = true
                    currentRad = findCloseSliderValueRadian(calculateRadToPoint(event.x, event.y))

                    controllerPointX = slideBarPointX + cos(currentRad).toFloat() * slideBarRadius
                    controllerPointY = slideBarPointY + sin(-currentRad).toFloat() * slideBarRadius

                    sliderValue = degreeToValue(currentRad.toDegree())

                    invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    state = false
                }
            }
        }
        return true
    }

    private fun isHeightEnough() = height >= width / 2 + slideBarMargin

    private fun calculateRadToPoint(x: Float, y: Float): Double {
        val arcTan = atan((x - slideBarPointX) / (y - slideBarPointY))
        return (pi / 2) + arcTan
    }

    private fun degreeToValue(degree: Double) = (((180F - degree) * maxPercentValue / 180F)).roundToInt()

    fun setValue(value: Int) {
        currentRad = (180 / maxPercentValue.toDouble() * (maxPercentValue - value)).toRadian().coerceIn(0.0..pi)
        // cos(rad) = -cos(value * pi / maxPercentValue)
        controllerPointX = slideBarPointX + cos(currentRad).toFloat() * slideBarRadius
        controllerPointY = slideBarPointY + sin(-currentRad).toFloat() * slideBarRadius
        sliderValue = value
        invalidate()
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

    fun setSliderValueChangeListener(listener: sliderValueChangeListener) {
        customViewClickListener = listener
    }

    fun setSliderMode(mode: RoundSliderState) {
        sliderMode = mode
    }

    private fun handleValueChangeEvent(value: Int) {
        customViewClickListener?.invoke(value)
    }

    private fun findCloseSliderValueRadian(radian: Double): Double {
        val degree = radian.toDegree()
        if (degree > 180 || degree < 0) return 0.0
        var minDifference = 180
        var prevDifference = 180

        val step = (180F / (maxPercentValue / sliderValueStepSize)).toInt()

        for (value: Int in 0..180 step (step)) {
            val difference = abs(degree.toInt() - value)

            if (difference > prevDifference) {
                return (value - step).toDouble().toRadian()
            }

            if (difference < minDifference) {
                minDifference = difference
            }
            prevDifference = difference
        }
        return 180.toRadian()
    }
}
