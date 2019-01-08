package e.kyco.cst369project;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class PhotoView extends MainActivity {

    TextView locationView;
    TextView descriptView;
    ImageView showImage;
    double latit, longitu;
    String descript;
    String together;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editphoto);
        showImage = (ImageView) findViewById(R.id.displayImageIV);
        locationView = (TextView) findViewById(R.id.locationTV);
        descriptView = (TextView) findViewById(R.id.imgDescriptTV);

        final Bundle extras = getIntent().getExtras();

        final Bitmap setFromImage = (Bitmap) extras.get("image");


        //setFromImage = extras.get("image");
        latit = extras.getDouble("lat");
        longitu = extras.getDouble("long");
        descript = extras.getString("description");

        together = longitu + ", " + latit;

        try {
            locationView.setText(together);
            descriptView.setText(descript);


            if (extras != null) {
                Bitmap image = (Bitmap) extras.get("image");
                if (image != null) {
                    showImage.setImageBitmap(image);


                }
            }
        }
          catch (NullPointerException e){

        }




    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
