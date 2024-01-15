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
    private val activeSlideBarPaint = Paint()
    private val controllerPaint = Paint()
    private val sliderValuePaint = Paint()

    private var controllerPointX = 0f
    private var controllerPointY = 0f

    private var slideBarPointX = 0f
    private var slideBarPointY = 0f

    private var customViewClickListener: sliderValueChangeListener? = null

    private var state = false

    private var sliderMode = RoundSliderState.ACTIVE

    private val pi = Math.PI.toFloat()

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

    private var maxPercentValue = 100F

    private var startDegree = 0F
    private var endDegree = 0F

    private var textValueSize = Sp(32F)

    private var rad = pi

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

        rad = (180 / maxPercentValue * (maxPercentValue - sliderValue)).toRadian().coerceIn(0F, pi)
        controllerPointX = slideBarPointX + cos(rad) * slideBarRadius
        controllerPointY = slideBarPointY + sin(-rad) * slideBarRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSlideBar(canvas)
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
        canvas.drawArc(oval, 180F, 180 - rad.toDegree(), false, activeSlideBarPaint)
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
                    rad = calculateRadToPoint(event.x, event.y)

                    controllerPointX = slideBarPointX + cos(rad) * slideBarRadius
                    controllerPointY = slideBarPointY + sin(-rad) * slideBarRadius

                    sliderValue = degreeToValue(rad.toDegree())

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

    private fun isHeightEnough(): Boolean {
        return height >= width / 2 + slideBarMargin
    }

    private fun calculateRadToPoint(x: Float, y: Float): Float {
        val arcTan = atan((x - slideBarPointX) / (y - slideBarPointY))
        return (pi / 2) + arcTan
    }

    private fun degreeToValue(degree: Float): Int {
        return ((180F - degree) / (180F / maxPercentValue)).toInt()
    }

    fun setValue(value: Int) {
        rad = (180 / maxPercentValue * (maxPercentValue - value)).toRadian().coerceIn(0F..pi)
        // cos(rad) = -cos(value * pi / maxPercentValue)
        controllerPointX = slideBarPointX + cos(rad) * slideBarRadius
        controllerPointY = slideBarPointY + sin(-rad) * slideBarRadius
        sliderValue = value
        invalidate()
    }

    fun setSliderStrokeWidth(value: Int) {
        slideBarStrokeWidth = Dp(value.toFloat()).toPx(context).value
        invalidate()
    }

    fun setMaxPercentValue(value: Int) {
        maxPercentValue = value.toFloat()
        invalidate()
    }

    fun setHighlightRange(startValue: Int, endValue: Int) {
        startDegree = (180 / maxPercentValue * startValue) + 180
        endDegree = (180 / maxPercentValue * endValue) + 180
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
}
