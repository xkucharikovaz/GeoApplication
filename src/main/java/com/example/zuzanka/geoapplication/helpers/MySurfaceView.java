package com.example.zuzanka.geoapplication.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

/**
 * Created by zuzanka on 8. 4. 2016.
 */
public class MySurfaceView extends SurfaceView implements Callback,
        Camera.PreviewCallback {

    private SurfaceHolder mHolder;

    private Camera mCamera;
    private byte[] mBuffer;
    private boolean isPreviewRunning = false;
    private byte [] rgbbuffer = new byte[256 * 256];
    private int [] rgbints = new int[256 * 256];

    protected final Paint rectanglePaint = new Paint();

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rectanglePaint.setARGB(100, 200, 0, 0);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setStrokeWidth(2);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect((int) Math.random() * 100,
                (int) Math.random() * 100, 200, 200, rectanglePaint);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat imgSource = Imgcodecs.imread("C://DataSetPV//img8.jpg");
        Imgproc.Canny(imgSource, imgSource, 300, 600, 5, true);

        Log.w(this.getClass().getName(), "On Draw Called");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    void openCamera() {
        // Called from parent activity after setting content view to CameraView
        mCamera = Camera.open();
        mCamera.setPreviewCallbackWithBuffer(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this) {
            this.setWillNotDraw(false); // This allows us to make our own draw
            // calls to this canvas

            mCamera = Camera.open();


            //try { mCamera.setPreviewDisplay(holder); } catch (IOException e)
            //  { Log.e("Camera", "mCamera.setPreviewDisplay(holder);"); }

            final Camera.Parameters params = mCamera.getParameters();
            final List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            final int screenWidth = ((View) getParent()).getWidth();
            int minDiff = Integer.MAX_VALUE;
            Camera.Size bestSize = null;

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // Find the camera preview width that best matches the
                // width of the surface.
                for (Camera.Size size : sizes) {
                    final int diff = Math.abs(size.width - screenWidth);
                    if (diff < minDiff) {
                        minDiff = diff;
                        bestSize = size;
                    }
                }
            } else {
                // Find the camera preview HEIGHT that best matches the
                // width of the surface, since the camera preview is rotated.
                mCamera.setDisplayOrientation(90);
                for (Camera.Size size : sizes) {
                    final int diff = Math.abs(size.height - screenWidth);
                    if (Math.abs(size.height - screenWidth) < minDiff) {
                        minDiff = diff;
                        bestSize = size;
                    }
                }
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();

            final int previewWidth = bestSize.width;
            final int previewHeight = bestSize.height;

            layoutParams.height = previewHeight;
            layoutParams.width = previewWidth;
            setLayoutParams(layoutParams);

            params.setPreviewFormat(ImageFormat.NV21);
            mCamera.setParameters(params);

            int size = previewWidth * previewHeight *
                    ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
            mBuffer = new byte[size];
            mCamera.addCallbackBuffer(mBuffer);

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            try {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    isPreviewRunning = false;
                    mCamera.release();
                }
            } catch (Exception e) {
                Log.e("Camera", e.getMessage());
            }
        }
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d("Camera", "Got a camera frame");

        Canvas c = null;

        if(mHolder == null){
            return;
        }

        try {
            synchronized (mHolder) {
                c = mHolder.lockCanvas(null);

                // Do your drawing here
                // So this data value you're getting back is formatted in YUV format and you can't do much
                // with it until you convert it to rgb
                int bwCounter=0;
                int yuvsCounter=0;
                for (int y=0;y<160;y++) {
                    System.arraycopy(data, yuvsCounter, rgbbuffer, bwCounter, 240);
                    yuvsCounter=yuvsCounter+240;
                    bwCounter=bwCounter+256;
                }

                for(int i = 0; i < rgbints.length; i++){
                    rgbints[i] = (int)rgbbuffer[i];
                }

                //decodeYUV(rgbbuffer, data, 100, 100);
                c.drawBitmap(rgbints, 0, 256, 0, 0, 256, 256, false, new Paint());

                Log.d("SOMETHING", "Got Bitmap");

            }
        } finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (c != null) {
                mHolder.unlockCanvasAndPost(c);
            }
        }
    }
}
