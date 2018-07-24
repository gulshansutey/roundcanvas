package gulshansutey.oldmirrorimageview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by Gulshan on 7/13/2018 at 16
 */
public class RoundCanvas extends android.support.v7.widget.AppCompatImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_STROKE_WIDTH = 1;
    private static final int DEFAULT_SOLID_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final boolean DEFAULT_SOLID_OVERLAY = false;
    /**
     * Rect @{@link RectF} holds four float coordinates for a rectangle.
     * The rectangle is represented by the coordinates of its 4 edges (left, top, right bottom).
     */
    private final RectF strokeRectF = new RectF();
    private final RectF drawableRectF = new RectF();
    /**
     * Paint @{@link Paint} class holds the style and color information about how to draw geometries, text and bitmaps.
     */
    private final Paint aBitmapPaint = new Paint();
    private final Paint aRoundCanvasPain = new Paint();
    private final Paint aStrokePain = new Paint();

    private final Matrix mShaderMatrix = new Matrix();
    private boolean ready = false;
    private boolean setup;
    private boolean strokeOverlay;
    private Bitmap aBitmap;

    /**
     * BitmapShader @{@link BitmapShader} used to draw a bitmap as a texture. The bitmap can be repeated or
     * mirrored by setting the tiling mode.
     */

    private BitmapShader bitmapShader;
    private ColorStateList tintStateList;
    private int strokeColor = DEFAULT_STROKE_COLOR;

    private float strokeWidth = DEFAULT_STROKE_WIDTH;

    private int solidColor = DEFAULT_SOLID_BACKGROUND_COLOR;
    private int aBitmapWidth, aBitmapHeight;
    private float strokeRadius;
    private float drawableRadius;
    private boolean removeRoundStyle=false;
    private ColorFilter colorFilter;

    public RoundCanvas(Context context) {
        super(context);
        System.out.println("context = " );
        init();
    }

    public RoundCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        System.out.println("context =2 " );
    }

    public RoundCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        System.out.println("context =3  " );
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCanvas, defStyleAttr, 0);
        /*get values from xml attrs*/
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.RoundCanvas_its_stroke_width, DEFAULT_STROKE_WIDTH);
        strokeColor = typedArray.getColor(R.styleable.RoundCanvas_its_stroke_color, DEFAULT_STROKE_COLOR);
        strokeOverlay = typedArray.getBoolean(R.styleable.RoundCanvas_its_stroke_overlay, DEFAULT_SOLID_OVERLAY);
        solidColor = typedArray.getColor(R.styleable.RoundCanvas_its_solid_color, DEFAULT_SOLID_BACKGROUND_COLOR);
        tintStateList = typedArray.getColorStateList(R.styleable.RoundCanvas_its_tint_color_state_drawable);
        typedArray.recycle();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (removeRoundStyle) {
            super.onDraw(canvas);
            return;
        }

        if (aBitmap == null) {
            return;
        }

        if (solidColor != Color.TRANSPARENT) {
            canvas.drawCircle(drawableRectF.centerX(), drawableRectF.centerY(), drawableRadius, aRoundCanvasPain);
        }
        canvas.drawCircle(drawableRectF.centerX(), drawableRectF.centerY(), drawableRadius, aBitmapPaint);
        if (strokeWidth > 0) {
            canvas.drawCircle(strokeRectF.centerX(), strokeRectF.centerY(), strokeRadius, aStrokePain);
        }
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        ready = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new MyViewOutLineProvider());
        }

        if (setup) {
            setupAttrs();
            setup = false;
        }
    }

    private void setupAttrs() {

        if (!ready) {
            setup = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (aBitmap == null) {
            /* redraw */
            invalidate();
            return;
        }

        /*  aBitmap : The bitmap to use inside the shader
     tileX : CLAMP The tiling mode for x to draw the bitmap in.
     tileY : CLAMP The tiling mode for y to draw the bitmap in.*/
        bitmapShader = new BitmapShader(aBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        aBitmapPaint.setAntiAlias(true);
        aBitmapPaint.setShader(bitmapShader);

        /*Setup Stroke*/

        aStrokePain.setStyle(Paint.Style.STROKE);
        aStrokePain.setAntiAlias(true);
        aStrokePain.setColor(strokeColor);
        aStrokePain.setStrokeWidth(strokeWidth);

        /*Setup Solid*/
        aRoundCanvasPain.setStyle(Paint.Style.FILL);
        aRoundCanvasPain.setAntiAlias(true);
        aRoundCanvasPain.setColor(solidColor);

        /*Setup Size*/

        aBitmapWidth = aBitmap.getWidth();
        aBitmapHeight = aBitmap.getHeight();

        strokeRectF.set(setCornerBounds());
        strokeRadius = Math.min((strokeRectF.height() - strokeWidth) / 2.0f, (strokeRectF.width() - strokeWidth) / 2.0f);
        drawableRectF.set(setCornerBounds());
        if (!strokeOverlay && strokeWidth > 0) {

            drawableRectF.inset(strokeWidth - 1f, strokeWidth - 1f);
        }
        drawableRadius = Math.min(drawableRectF.height() / 2f, drawableRectF.width() / 2f);
        colorFilter();
        updateShaderMatrix();
        invalidate();
    }




    private RectF setCornerBounds() {

            int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

            int sideLength = Math.min(availableWidth, availableHeight);

            float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
            float top = getPaddingTop() + (availableHeight - sideLength) / 2f;
            return new RectF(left, top, left + sideLength, top + sideLength);


    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (aBitmapWidth * drawableRectF.height() > drawableRectF.width() * aBitmapHeight) {
            scale = drawableRectF.height() / (float) aBitmapHeight;
            dx = (drawableRectF.width() - aBitmapWidth * scale) * 0.5f;
        } else {
            scale = drawableRectF.width() / (float) aBitmapWidth;
            dy = (drawableRectF.height() - aBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + drawableRectF.left, (int) (dy + 0.5f) + drawableRectF.top);

        bitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private void colorFilter() {
        if (aBitmapPaint != null) {
            aBitmapPaint.setColorFilter(colorFilter);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tintStateList != null && tintStateList.isStateful())

            updateTintColor();
    }

    private void updateTintColor() {
        int color = tintStateList.getColorForState(getDrawableState(), Color.WHITE);
        setColorFilter(color);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setupAttrs();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setupAttrs();
    }

    /**
     * @return Image View stroke color
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        if (this.strokeColor == strokeColor) {
            return;
        }
        this.strokeColor = strokeColor;
        aStrokePain.setColor(this.strokeColor);
        invalidate();
    }

    public ColorStateList getTintStateList() {
        return tintStateList;
    }

    public void setTintStateList(ColorStateList tintStateList) {
        if (this.tintStateList == tintStateList) {
            return;
        }
        this.tintStateList = tintStateList;

    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(SCALE_TYPE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupAttrs();
    }

    @Override
    public int getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(int solidColor) {
        if (this.solidColor == solidColor) {
            return;
        }

        this.solidColor = solidColor;
        aRoundCanvasPain.setColor(this.solidColor);
        invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {

        if (this.strokeWidth == strokeWidth) return;

        this.strokeWidth = strokeWidth;
        setupAttrs();
    }

    public boolean isStrokeOverlay() {
        return strokeOverlay;
    }

    public void setStrokeOverlay(boolean strokeOverlay) {
        if (this.strokeOverlay == strokeOverlay) return;

        this.strokeOverlay = strokeOverlay;
        setupAttrs();
    }

    public boolean isRemoveRoundStyle() {
        return removeRoundStyle;
    }

    public void setRemoveRoundStyle(boolean removeRoundStyle) {
        if (this.removeRoundStyle == removeRoundStyle) return;

        this.removeRoundStyle = removeRoundStyle;
        initializeBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    @Override
    public ColorFilter getColorFilter() {
        return colorFilter;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == colorFilter) {
            return;
        }

        colorFilter = cf;
        colorFilter();
        invalidate();
    }

    private void applyColorFilter() {
        if (aBitmapPaint != null) {
            aBitmapPaint.setColorFilter(colorFilter);
        }
    }

    private void initializeBitmap() {
        if (removeRoundStyle) {
            aBitmap = null;
        } else {
            aBitmap = getBitmapFromDrawable(getDrawable());
        }
        setupAttrs();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) return null;

        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable) drawable).getBitmap();

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable)
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);

            else
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MyViewOutLineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect bounds = new Rect();
            strokeRectF.roundOut(bounds);
            outline.setRoundRect(bounds, bounds.width() / 2.0f);
        }
    }


}
