package com.example.zuzanka.geoapplication.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.example.zuzanka.geoapplication.MainActivity;
import com.example.zuzanka.geoapplication.R;
import com.example.zuzanka.geoapplication.helpers.GPSTracker;
import com.example.zuzanka.geoapplication.objects.Image;
import com.example.zuzanka.geoapplication.objects.OpenCVWorker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class Camera2Activity extends MainActivity implements SurfaceHolder.Callback {

    public static Image currentImage = new Image();
    public static GPSTracker gps;

    Camera2BasicFragment camera2BasicFragment = null;
    public static final int DRAW_RESULT_BITMAP = 10;
    private OpenCVWorker mWorker;
    private Handler mUiHandler;
    private double mFpsResult;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        try {
            //camera2BasicFragment = Camera2BasicFragment.newInstance(); new
            //camera2BasicFragment.setArguments(getIntent().getExtras()); old
        } catch (VerifyError error){

        }

        mSavedInstanceState = savedInstanceState;

        this.gps = gpsTracker;

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, new Camera2Activity.OpenCVLoaderCallback(this));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // Initializing OpenCV is done asynchronously. We do this after our SurfaceView is ready.
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, new Camera2Activity.OpenCVLoaderCallback(this));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //mSurfaceSize = new Rect(0, 0, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void initCameraView() {
        camera2BasicFragment = Camera2BasicFragment.newInstance();

        if (null == mSavedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }
    }

    /**
     * This class will receive a callback once the OpenCV library is loaded.
     */
    private static final class OpenCVLoaderCallback extends BaseLoaderCallback {
        private Context mContext;

        public OpenCVLoaderCallback(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    ((Camera2Activity) mContext).initCameraView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

    }
}
