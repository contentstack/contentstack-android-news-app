package com.raweng.contentstackapplication.thirdpartyutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ProgressBarCircularIndeterminate extends View {

    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    int backgroundColor = Color.parseColor("#e57b1e");
    public ProgressBarCircularIndeterminate(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }
    protected void setAttributes(AttributeSet attrs){
        setMinimumHeight(Utils.dpToPx(32, getResources()));
        setMinimumWidth(Utils.dpToPx(32, getResources()));


        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,"background",-1);
        if(bacgroundColor != -1){
            setBackgroundColor(getResources().getColor(bacgroundColor));
        }else{

            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
            else
                setBackgroundColor(Color.parseColor("#e57b1e"));
        }
        setMinimumHeight(Utils.dpToPx(3, getResources()));
    }

    protected int makePressColor(){
        int r = (this.backgroundColor >> 16) & 0xFF;
        int g = (this.backgroundColor >> 8) & 0xFF;
        int b = (this.backgroundColor >> 0) & 0xFF;

        return Color.argb(128, r, g, b);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(firstAnimationOver == false)
            drawFirstAnimation(canvas);
        if(cont > 0)
            drawSecondAnimation(canvas);
        invalidate();
    }
    float radius1 = 0;
    float radius2 = 0;
    int cont = 0;
    boolean firstAnimationOver = false;

    /**
     * Draw first animation of view
     * @param canvas
     */
    private void drawFirstAnimation(Canvas canvas){
        if(radius1 < getWidth()/2){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            radius1 = (radius1 >= getWidth()/2)? (float)getWidth()/2 : radius1+1;
            canvas.drawCircle(getWidth()/2, getHeight()/2, radius1, paint);
        }else{
            Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            temp.drawCircle(getWidth()/2, getHeight()/2, getHeight()/2, paint);
            Paint transparentPaint = new Paint();
            transparentPaint.setAntiAlias(true);
            transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            if(cont >= 50){
                radius2 = (radius2 >= getWidth()/2)? (float)getWidth()/2 : radius2+1;
            }else{
                radius2 = (radius2 >= getWidth()/2-Utils.dpToPx(4, getResources()))? (float)getWidth()/2-Utils.dpToPx(4, getResources()) : radius2+1;
            }
            temp.drawCircle(getWidth()/2, getHeight()/2, radius2, transparentPaint);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
            if(radius2 >= getWidth()/2-Utils.dpToPx(4, getResources()))
                cont++;
            if(radius2 >= getWidth()/2)
                firstAnimationOver = true;
        }
    }
    int arcD = 1;
    int arcO = 0;
    float rotateAngle = 0;
    int limite = 0;
    /**
     * Draw second animation of view
     * @param canvas
     */
    private void drawSecondAnimation(Canvas canvas){
        if(arcO == limite)
            arcD+=6;
        if(arcD >= 290 || arcO > limite){
            arcO+=6;
            arcD-=6;
        }
        if(arcO > limite + 290){
            limite = arcO;
            arcO = limite;
            arcD = 1;
        }
        rotateAngle += 4;
        canvas.rotate(rotateAngle,getWidth()/2, getHeight()/2);
        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        temp.drawArc(new RectF(0, 0, getWidth(), getHeight()), arcO, arcD, true, paint);
        Paint transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        temp.drawCircle(getWidth()/2, getHeight()/2, (getWidth()/2)-Utils.dpToPx(4, getResources()), transparentPaint);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
    }
    public void setBackgroundColor(int color){
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if(isEnabled())
        this.backgroundColor = color;
    }
}