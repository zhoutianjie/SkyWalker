package com.kedacom.truetouch.ok.widgt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


/**
 * 圆形图片
 * Created by zhoutianjie on 2018/12/11.
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;
    private Shader mBitmapShader;
    private Matrix mMatrix;
    private Bitmap mBitmap;
    private Canvas canvas;


    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMatrix = new Matrix();
        canvas = new Canvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if(null == drawable){
            return;
        }

        Bitmap bitmap = DrawableToBitmap(drawable);
        float scale = getWidth()/Math.min(bitmap.getWidth(),bitmap.getHeight());
        mMatrix.setScale(scale,scale);
        if(null == mBitmapShader){
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mBitmapShader);

        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,mPaint);

    }

    private Bitmap DrawableToBitmap(Drawable drawable) {
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        if(null == mBitmap || mBitmap.isRecycled()){
            mBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        }
        canvas.setBitmap(mBitmap);
        drawable.setBounds(0,0,w,h);
        drawable.draw(canvas);
        return mBitmap;
    }
}
