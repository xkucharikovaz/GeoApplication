package com.example.zuzanka.geoapplication.objects;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zuzanka on 16. 10. 2016.
 */

public class Image {

    public float orientationDegree[] = new float[3];
    public float magnetometerDegree[] = new float[3];
    public float magnetometerRawData[] = new float[3];
    public String billboardType;
    public String orientation;
    public Place realPlace;
    public LatLng latLng;
    public String imageFilePath;
    public String imageFileName;
    public int cvtColorAttribute = Imgproc.COLOR_BGR2GRAY;

    public Image(){}

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                System.getProperty("line.separator")
                        + System.getProperty("line.separator")
                        + "Image Name: " + imageFileName);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "Time: " + getCurrentTime());
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "File Path: " + imageFilePath);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "Orientation Degree: x = " + orientationDegree[0]
                        + ", y = " + orientationDegree[1]
                        + ", z = " + orientationDegree[2]);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "Magnetometer Degree: x = " + magnetometerDegree[0]
                        + ", y = " + magnetometerDegree[1]
                        + ", z = " + magnetometerDegree[2]);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "Magnetometer Raw Data: x = " + magnetometerRawData[0]
                        + ", y = " + magnetometerRawData[1]
                        + ", z = " + magnetometerRawData[2]);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "Billboard Type: " + billboardType);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"CvtColorAttribute\":\"" + String.valueOf(cvtColorAttribute) + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "Orientation: " + orientation);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "GPS: latitude = " + latLng.latitude
                        + ", longitude = " + latLng.longitude);
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "RealPlaceGPS: address = " + realPlace.getAddress()
                        + ", latitude = " + realPlace.getLatLng().latitude
                        + ", longitude = " + realPlace.getLatLng().longitude);
        return  stringBuilder.toString();
    }
    public String toJSONString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                System.getProperty("line.separator")
                        + System.getProperty("line.separator")
                        + "{\"ImageName\":\"" + imageFileName + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"Time\":\"" + getCurrentTime() + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"FilePath\":\"" + imageFilePath + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"OrientationDegree\":{"
                        + System.getProperty("line.separator")
                        + "\"x\":\"" + orientationDegree[0] + "\","
                        + System.getProperty("line.separator")
                        + "\"y\":\"" + orientationDegree[1] + "\","
                        + System.getProperty("line.separator")
                        + "\"z\":\"" + orientationDegree[2] + "\"},");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"MagnetometerDegree\":{"
                        + System.getProperty("line.separator")
                        + "\"x\":\"" + magnetometerDegree[0] + "\","
                        + System.getProperty("line.separator")
                        + "\"y\":\"" + magnetometerDegree[1] + "\","
                        + System.getProperty("line.separator")
                        + "\"z\":\"" + magnetometerDegree[2] + "\"},");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"MagnetometerRawData\":{"
                        + System.getProperty("line.separator")
                        + "\"x\":\"" + magnetometerRawData[0] + "\","
                        + System.getProperty("line.separator")
                        + "\"y\":\"" + magnetometerRawData[1] + "\","
                        + System.getProperty("line.separator")
                        + "\"z\":\"" + magnetometerRawData[2] + "\"},");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"BillboardType\":\"" + billboardType + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"CvtColorAttribute\":\"" + String.valueOf(cvtColorAttribute) + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"Orientation\":\"" + orientation + "\",");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"GPS\":{"
                        + System.getProperty("line.separator")
                        + "\"latitude\":\"" + latLng.latitude + "\","
                        + System.getProperty("line.separator")
                        + "\"longitude\":\"" + latLng.longitude + "\"},");
        stringBuilder.append(
                System.getProperty("line.separator")
                        + "\"RealPlaceGPS\":{"
                        + System.getProperty("line.separator")
                        + "\"name\":\"" + realPlace.getName() + "\","
                        + System.getProperty("line.separator")
                        + "\"address\":\"" + realPlace.getAddress() + "\","
                        + System.getProperty("line.separator")
                        + "\"latitude\":\"" + realPlace.getLatLng().latitude + "\","
                        + System.getProperty("line.separator")
                        + "\"longitude\":\"" + realPlace.getLatLng().longitude + "\"}}");
        return  stringBuilder.toString();
    }

    private String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
        Date now = new Date();
        String strDate = simpleDateFormat.format(now);
        return strDate.toString();
    }

    public void swapCvtColorSttribute(){
        if (cvtColorAttribute == Imgproc.COLOR_BGR2GRAY){
            cvtColorAttribute = Imgproc.COLOR_RGB2GRAY;
        } else if (cvtColorAttribute == Imgproc.COLOR_RGB2GRAY){
            cvtColorAttribute = Imgproc.COLOR_BGR2GRAY;
        }
    }
}
