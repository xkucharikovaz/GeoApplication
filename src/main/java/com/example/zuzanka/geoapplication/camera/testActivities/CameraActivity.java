package com.example.zuzanka.geoapplication.camera.testActivities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zuzanka.geoapplication.MainActivity;
import com.example.zuzanka.geoapplication.R;
import com.example.zuzanka.geoapplication.camera.CameraPreview;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends MainActivity implements SensorEventListener {

    // Layout Views
    private Button buttonUse;
    private Button buttonRetake;
    private Button buttonCapture;
    private TextView degreeTextView;
    private ImageView imageViewCamera;
    private FrameLayout frameLayoutCapture;

    // Camera
    private Camera mCamera;
    private int deviceHeight;
    private ExifInterface exif;
    private CameraPreview mPreview;

    // File
    private String dir;
    private File sdRoot;
    private String fileName;
    private GoogleApiClient client;
    Bitmap bmp;

    // Orientation
    private int orientation;
    private int degrees = -1;
    private static float degree;
    private double imageLatitude;
    private double imageLongitude;
    private float imageOrientationDegree;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Getting all the needed elements from the layout
        buttonUse = (Button) findViewById(R.id.buttonUse);
        buttonRetake = (Button) findViewById(R.id.buttonRetake);
        buttonCapture = (Button) findViewById(R.id.buttonCapture);
        degreeTextView = (TextView) findViewById(R.id.degreeTextView);
        imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);
        frameLayoutCapture = (FrameLayout) findViewById(R.id.frameLayoutCapture);

        // Setting all button click listeners
        buttonUse.setOnClickListener(cameraUseListener);
        buttonRetake.setOnClickListener(cameraRetakeListener);
        buttonCapture.setOnClickListener(cameraCaptureListener);

        // Getting the motion sensor service.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Setting all the path for the image
        sdRoot = Environment.getExternalStorageDirectory();
        dir = "/DCIM/Camera/";

        // Selecting the resolution of the Android device to create a proportional preview
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        deviceHeight = display.getHeight();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void createCamera() {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Setting the right parameters in the camera
        Camera.Parameters params = mCamera.getParameters();
        //params.setPictureSize(1600, 1200);
        //params.setPictureFormat(PixelFormat.JPEG);
        //params.setJpegQuality(85);
        params.setAutoWhiteBalanceLock(true);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setJpegQuality(100);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        // Resizing the LinearLayout so we can make a proportional preview. This
        // approach is not 100% perfect because on devices with a really small
        // screen the the image will still be distorted - there is place for
        // improvment.
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        preview.setLayoutParams(layoutParams);

        // Adding the camera preview after the FrameLayout and before the button
        // as a separated element.
        preview.addView(mPreview, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Test if there is a camera on the device and if the SD card is
        // mounted.
        if (!checkCameraHardware(this)) {
            System.out.println("no camera hardware");
        } else if (!checkSDCard()) {
            System.out.println("no SD card");
        }

        // Creating the camera
        createCamera();

        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // release the camera immediately on pause event
        releaseCamera();

        // removing the inserted view - so when we come back to the app we
        // won't have the views on top of each other.
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeViewAt(0);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private boolean checkSDCard() {
        boolean state = false;

        String sd = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sd)) {
            state = true;
        }

        return state;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            // attempt to get a Camera instance
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }

        // returns null if camera is unavailable
        return c;
    }

    Camera.AutoFocusCallback autoFocus=new Camera.AutoFocusCallback() {
        Camera.ShutterCallback shutterCallback =new Camera.ShutterCallback() {

            @Override
            public void onShutter() {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);

            }
        };
        Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, final Camera camera) {
                //dialog = ProgressDialog.show(CustomCameraActivity.this, "", "Saving Photo");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ex) {}
                        onPictureTake(data, camera);
                    }
                }.start();
            }
        };

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.takePicture(shutterCallback,null, null, photoCallback);
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            // Name the file of taken image
            fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";

            // Creating the directory where to save the image. Sadly in older
            // version of Android we can not get the Media catalog name
            File mkDir = new File(sdRoot, dir);
            mkDir.mkdirs();

            // Main file where to save the data that we recive from the camera
            File pictureFile = new File(sdRoot, dir + fileName);

            try {
                FileOutputStream purge = new FileOutputStream(pictureFile);
                purge.write(data);
                purge.close();
            } catch (FileNotFoundException e) {
                Log.d("DG_DEBUG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
            }

            // Adding Exif data for the orientation. For some strange reason the
            // ExifInterface class takes a string instead of a file.
            try {
                exif = new ExifInterface("/sdcard/" + dir + fileName);
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + orientation);
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, dec2DMS(imageLatitude));
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, dec2DMS(imageLongitude));
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, getLatitudeRef(imageLatitude));
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, getLongitudeRef(imageLongitude));
                exif.setAttribute("ImageDegrees",Float.toString(degree));
                // exif imageDescription
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save all information about capturing photo into file, path:/downloads/GPSdata.txt
            /*if (isExternalStorageWritable()){
                writeToSDFile(imageLatitude, imageLongitude, imageOrientationDegree, externalSDFile, new String(dir + fileName));
            }*/

            // Replacing the button capture to use/retake buttons after a photo was taken
            buttonUse.setVisibility(View.VISIBLE);
            buttonRetake.setVisibility(View.VISIBLE);
            frameLayoutCapture.setVisibility(View.GONE);

        }
    };

    /**
     * Calculating the degrees needed to rotate the image imposed on the button
     * so it is always facing the user in the right direction
     *
     * @param toDegrees
     * @return
     */
    private RotateAnimation getRotateAnimation(float toDegrees) {
        float compensation = 0;

        if (Math.abs(degrees - toDegrees) > 180) {
            compensation = 360;
        }

        // When the device is being held on the left side (default position for
        // a camera) we need to add, not subtract from the toDegrees.
        if (toDegrees == 0) {
            compensation = -compensation;
        }

        // Creating the animation and the RELATIVE_TO_SELF means that he image
        // will rotate on it center instead of a corner.
        RotateAnimation animation = new RotateAnimation(degrees, toDegrees - compensation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // Adding the time needed to rotate the image
        animation.setDuration(250);

        // Set the animation to stop after reaching the desired position. With
        // out this it would return to the original state.
        animation.setFillAfter(true);

        return animation;
    }

    /**
     * STUFF THAT WE DON'T NEED BUT MUST BE HEAR FOR THE COMPILER TO BE HAPPY.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Camera Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.zuzanka.geoapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Camera Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.zuzanka.geoapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

// ---------------------------------------------------------------------------------------- SENSOR FUNCTIONS ----------------------------------------------------------------------------------------

    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                RotateAnimation animation = null;
                if (event.values[0] < 4 && event.values[0] > -4) {
                    if (event.values[1] > 0 && orientation != ExifInterface.ORIENTATION_ROTATE_90) {
                        // UP
                        orientation = ExifInterface.ORIENTATION_ROTATE_90;
                        animation = getRotateAnimation(270);
                        degrees = 270;
                    } else if (event.values[1] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_270) {
                        // UP SIDE DOWN
                        orientation = ExifInterface.ORIENTATION_ROTATE_270;
                        animation = getRotateAnimation(90);
                        degrees = 90;
                    }
                } else if (event.values[1] < 4 && event.values[1] > -4) {
                    if (event.values[0] > 0 && orientation != ExifInterface.ORIENTATION_NORMAL) {
                        // LEFT
                        orientation = ExifInterface.ORIENTATION_NORMAL;
                        animation = getRotateAnimation(0);
                        degrees = 0;
                    } else if (event.values[0] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_180) {
                        // RIGHT
                        orientation = ExifInterface.ORIENTATION_ROTATE_180;
                        animation = getRotateAnimation(180);
                        degrees = 180;
                    }
                }
                if (animation != null) {
                    imageViewCamera.startAnimation(animation);
                }
            }

            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
                degree = Math.round(event.values[0]);
                degreeTextView.setText("Degrees from North: " + Float.toString(degree));
            }

        }
    }

// ---------------------------------------------------------------------------------------- LISTENERS FUNCTIONS ----------------------------------------------------------------------------------------

    View.OnClickListener cameraCaptureListener = new View.OnClickListener() {
        public void onClick(View v) {

            // Store view orientation degree when capturing photo
            imageOrientationDegree = degree;

            // Store GPS parameters when capturing photo
            imageLatitude = gpsTracker.getLatitude();
            imageLongitude = gpsTracker.getLongitude();

            // Call function for capturing picture
            mCamera.takePicture(null, null, mPicture);
        }
    };

    View.OnClickListener cameraRetakeListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Deleting the image from the SD card/
            File discardedPhoto = new File(sdRoot, dir + fileName);
            discardedPhoto.delete();

            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                private long timestamp = 0;

                public synchronized void onPreviewFrame(byte[] data, Camera camera) {
                    Log.v("CameraTest", "Time Gap = " + (System.currentTimeMillis() - timestamp));
                    timestamp = System.currentTimeMillis();
                    try {
                        camera.addCallbackBuffer(data);
                    } catch (Exception e) {
                        Log.e("CameraTest", "addCallbackBuffer error");
                        return;
                    }
                    return;
                }
            });


            /*public final void onPreviewFrame(final byte[] data, final Camera camera) {
                this.data = data;
                now = Calendar.getInstance().getTimeInMillis();

                if (now - lastRecognitionTime > TIME_BETWEEN_RECOGNITION_STARTS_IN_MILLIS) {
                    lastRecognitionTime = now;
                    startRecognition(); // here you can start your async task
                }
            }*/


            // Restart the camera preview.
            mCamera.startPreview();

            // Reorganize the buttons on the screen
            frameLayoutCapture.setVisibility(LinearLayout.VISIBLE);
            buttonRetake.setVisibility(LinearLayout.GONE);
            buttonUse.setVisibility(LinearLayout.GONE);
        }
    };

    View.OnClickListener cameraUseListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Everything is saved so we can quit the app.
            finish();

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse
                    ("file://" + dir + fileName)));

            /*
            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            sendBroadcast(mediaScanIntent);*/
        }
    };

    public void onPictureTake(byte[] data, Camera camera) {

        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        //dialog.dismiss();
    }

}
