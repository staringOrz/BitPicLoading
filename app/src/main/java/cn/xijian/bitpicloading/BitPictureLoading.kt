package cn.xijian.bitpicloading

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import java.io.InputStream

/**
 * @author dengxijian
 * @date 2020/6/24
 */
internal class BitPictureLoading : View,
    GestureDetector.OnGestureListener, View.OnTouchListener {
    private var mGestureDetector: GestureDetector = GestureDetector(context, this)
    private var mOption: BitmapFactory.Options = BitmapFactory.Options()
    private lateinit var mScroller: Scroller
    private var mRect: Rect = Rect()
    private lateinit var mBitmap: Bitmap
    private var mImageWidth = 0
    private var mImageHeight = 0
    private var mScale = 0f
    private lateinit var mDecoder: BitmapRegionDecoder

    init {
        setOnTouchListener(this)
        mScroller = Scroller(context)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun addImage(inputStream: InputStream) {
        mOption.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, mOption)
        mImageWidth = mOption.outWidth
        mImageHeight = mOption.outHeight
        mOption.inPreferredConfig = Bitmap.Config.RGB_565
        mOption.inJustDecodeBounds = false

        mDecoder = BitmapRegionDecoder.newInstance(inputStream, false)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRect.left = 0
        mRect.top = 0
        mRect.right = measuredWidth
        mScale = (measuredWidth / mImageWidth).toFloat()
        mRect.bottom = (measuredHeight / mScale).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //复用内存
        mOption.inBitmap = mDecoder.decodeRegion(mRect, mOption)
        mBitmap = mOption.inBitmap

        var matrix = Matrix()
        matrix.setScale(mScale, mScale)

        canvas.drawBitmap(mBitmap, matrix, null)

    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        if (!mScroller.isFinished) {
            mScroller.forceFinished(true)
        }
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        mScroller.fling(
            0,
            mRect.top,
            0,
            -velocityY.toInt(),
            0,
            0,
            0,
            (mImageHeight - (measuredHeight / mScale)).toInt()
        )
        return false
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.isFinished) {
            return
        }
        if (mScroller.computeScrollOffset()) {
            mRect.top = mScroller.currY
            mRect.bottom = (mRect.top + measuredHeight / mScale).toInt()
            invalidate()
        }
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        mRect.offset(0, distanceY.toInt())
        if (mRect.bottom >= mImageHeight) {
            mRect.bottom = mImageHeight
            mRect.top = (mImageHeight - (measuredHeight / mScale)).toInt()
        }
        if (mRect.top < 0) {
            mRect.top = 0
            mRect.bottom = (measuredHeight / mScale).toInt()
        }
        invalidate()
        return false

    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }
}