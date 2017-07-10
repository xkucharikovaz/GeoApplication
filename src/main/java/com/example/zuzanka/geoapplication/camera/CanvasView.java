package com.example.zuzanka.geoapplication.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.core.Point;

/**
 * Created by zuzanka on 9. 4. 2017.
 */

public class CanvasView extends View {

    Paint paint;
    Point start = new Point();
    Point end = new Point();


    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawRect(30, 50, 200, 350, paint);
        canvas.drawRect(Math.round(start.x), Math.round(start.y), Math.round(end.x), Math.round(end.y), paint);
    }

    public void resetRentangle(){
        start = new Point();
        end = new Point();
    }

}
