package e.kyco.cst369project;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Markers {
    private double latit, longitu;
    private Bitmap imageForMark;
    private String title;
    String bitImageString;

    public Markers(){

    }

    public Markers(double lat, double longitude, String imageSet, String titleFor){
        latit = lat;
        longitu = longitude;
        bitImageString = imageSet;
        title = titleFor;

    }


    public double getLatit() {
        return latit;
    }

    public void setLatit(double latit) {
        this.latit = latit;
    }

    public double getLongitu() {
        return longitu;
    }

    public void setLongitu(double longitu) {
        this.longitu = longitu;
    }

    public String getBitImageString() {
        return bitImageString;
    }

    public void setBitImageString(String imageForMark) {
        this.bitImageString = imageForMark;
    }

    public String getTitle() { return title;}

    public void setTitle(String title){this.title = title;}

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
