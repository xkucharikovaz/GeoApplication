package com.example.zuzanka.geoapplication.camera.testActivities;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.zuzanka.geoapplication.R;

import java.io.IOException;

public class OpenCVCamera2Activity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cvcamera2);

        SurfaceHolder cameraHolder = ((SurfaceView)findViewById(R.id.cameraView)).getHolder();
        cameraHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if(camera != null) {
            camera.release();
            camera = null;
        }
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setPreviewCallback(new Camera.PreviewCallback() {

            public void onPreviewFrame(byte[] data, Camera camera) {
                System.out.println("Frame received!"+data.length);
                Camera.Size size = camera.getParameters().getPreviewSize();
				/*
				 * Directly constructing a bitmap from the data would be possible if the preview format
				 * had been set to RGB (params.setPreviewFormat() ) but some devices only support YUV.
				 * So we have to stick with it and convert the format
				 */
                int[] rgbData = convertYUV420_NV21toRGB8888(data, size.width, size.height);
                Bitmap bitmap = Bitmap.createBitmap(rgbData, size.width, size.height, Bitmap.Config.ARGB_8888);
				/*
				 * TODO: now process the bitmap
				 */
            }
        });
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera != null)
        {
            camera.release();
            camera = null;
        }
    }

    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @author Derzu, {@link https://stackoverflow.com/a/12702836}
     * @param data		Image data in YUV420 NV21 format.
     * @param width		The image's width
     * @param height	The image's height
     * @return RGB8888 pixels int array
     */
    public static int[] convertYUV420_NV21toRGB8888(byte [] data, int width, int height) {
        int size = width*height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = data[i  ]&0xff;
            y2 = data[i+1]&0xff;
            y3 = data[width+i  ]&0xff;
            y4 = data[width+i+1]&0xff;

            u = data[offset+k  ]&0xff;
            v = data[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            pixels[i  ] = convertYUVtoRGB(y1, u, v);
            pixels[i+1] = convertYUVtoRGB(y2, u, v);
            pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
            pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i+=width;
        }

        return pixels;
    }
    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)1.402f*v;
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)1.772f*u;
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }
}
