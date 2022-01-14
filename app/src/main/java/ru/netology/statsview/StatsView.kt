package ru.netology.statsview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.Util.AndroidUtils
import kotlin.random.Random


class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyle: Int = 0
): View(context, attributeSet, defStyleAttr,defStyle) {
    private var lineWidth = AndroidUtils.dp(context,5F).toFloat()
    private var textSize = AndroidUtils.dp(context, 30F).toFloat()
    private var colors = listOf<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView){
            lineWidth = getDimension(R.styleable.StatsView_lineSize, lineWidth)
            textSize = getDimension(R.styleable.StatsView_testSize, textSize)
            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateColor()),
                getColor(R.styleable.StatsView_color2, generateColor()),
                getColor(R.styleable.StatsView_color3, generateColor()),
                getColor(R.styleable.StatsView_color4, generateColor()),
                getColor(R.styleable.StatsView_color5, generateColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
    set(value) {
        var parts = value.takeLast(4)
        field = if (parts.size < 4  && parts.sum() < 100F){
            parts.map { it / 100F }
        } else{
            parts.map { it / parts.sum() }
        }

        invalidate()
    }
    private var radius = 0F
    private var oval = RectF()
    private var center = PointF()


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = lineWidth
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = this@StatsView.textSize
    }

    fun generateColor(): Int = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = w.toFloat().coerceAtMost(h.toFloat()) / 2F - lineWidth
        center = PointF(w / 2F, h/ 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {

        if(data.isEmpty()){
            return
        }

        var startAngle = - 90F
        paint.color = colors.get(4)
        canvas.drawCircle(center.x ,
            center.y , radius, paint)
        data.forEachIndexed{ index, datum ->
            val angle = datum * 360
            paint.color = colors.getOrElse(index){generateColor()}
            canvas.drawArc(oval, startAngle,angle,false,paint)
            startAngle += angle
        }
        canvas.drawText(
            "%.2f%%".format(data.sum() * 100F),
            center.x ,
            center.y + (textPaint.textSize / 4),
            textPaint
        )
        paint.color = colors.get(0)
        canvas.drawPoint(center.x ,
            center.y - radius, paint)
    }
}