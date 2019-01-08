package e.kyco.cst369project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

public class PhotoActivity extends MainActivity {
    ImageView imgTaken;
    TextView locationText;
    EditText descript;
    double latit, longitu;
    private static final String TAG = "PhotoActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editphoto);
        Button btnPublish = (Button) findViewById(R.id.publishBtn);

        imgTaken = (ImageView) findViewById(R.id.editImageIV);
        locationText = (TextView) findViewById(R.id.locationET);
        descript = (EditText) findViewById(R.id.imgDescriptET);

        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();

        final Bitmap setFromImage = (Bitmap) extras.get("image");


        latit = extras.getDouble("lat");
        longitu = extras.getDouble("long");

        locationText.setText(latit + ", " + longitu);


        if (extras != null) {
            Bitmap image = setFromImage;
            if (image != null) {
                imgTaken.setImageBitmap(image);

            }
        }

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "publishBtn: sendingPicBackForMarker");
                Intent publishIntent = new Intent(PhotoActivity.this, MainActivity.class);
                publishIntent.putExtra("lat", latit);
                publishIntent.putExtra("long", longitu);
                publishIntent.putExtra("description", descript.getText().toString());
                startActivity(publishIntent);
            }
        });


    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
        byte[] b = ByteStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {

        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;

    }
}



