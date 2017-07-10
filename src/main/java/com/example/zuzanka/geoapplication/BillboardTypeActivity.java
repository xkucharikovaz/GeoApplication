package com.example.zuzanka.geoapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.zuzanka.geoapplication.adapters.ImageAdapter;
import com.example.zuzanka.geoapplication.camera.Camera2Activity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;

public class BillboardTypeActivity extends MainActivity {

    private View selectedItem = null;
    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard_type);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(onGridViewClickListener);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                Camera2Activity.currentImage.realPlace = place;

                if (isExternalStorageWritable()){
                    writeToSDFile(externalSDFile, Camera2Activity.currentImage.toJSONString());
                }

                ExifInterface exif;
                try {
                    exif = new ExifInterface(Camera2Activity.currentImage.imageFilePath);
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "" + dec2DMS(Camera2Activity.currentImage.latLng.latitude));
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "" + dec2DMS(Camera2Activity.currentImage.latLng.longitude));
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "" + getLatitudeRef(Camera2Activity.currentImage.latLng.latitude));
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "" + getLongitudeRef(Camera2Activity.currentImage.latLng.longitude));
                    exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "" + Camera2Activity.currentImage.billboardType);
                    // exif imageDescription
                    exif.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, Camera2Activity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //---------------------------------- BUTTON FUNCTIONS ------------------------------------------

    public void onTypeSelectedButtonClick(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        if (selectedItem != null) {
            String type = getResources().getString((Integer) selectedItem.getTag());
            Camera2Activity.currentImage.billboardType = type;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } else {
            showMissingSelectionDialog();
        }
    }

    //--------------------------------- LISTENER FUNCTIONS -----------------------------------------

    AdapterView.OnItemClickListener onGridViewClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {

            if (selectedItem != null){
                selectedItem.findViewById(R.id.selectedLayout)
                        .setBackgroundColor(Color.TRANSPARENT);
            }

            selectedItem = v;
            selectedItem.findViewById(R.id.selectedLayout)
                    .setBackgroundColor(getResources().getColor(R.color.selectedItemColor));
        }
    };

    //---------------------------------- DIALOG FUNCTIONS ------------------------------------------

    private void showMissingSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Missing selection")
                .setMessage("Please select billboard type.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}
