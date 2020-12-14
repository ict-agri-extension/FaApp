package sindh.agriculureextension.fadiary.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.database.Queries;
import sindh.agriculureextension.fadiary.form.FarmerForm;

@SuppressWarnings("ALL")
public class CameraActivity extends AppCompatActivity {

    private ImageView imageView;
    private String currentPhotoPath;
    private static final int IMAGE_PICK = 899;
    private ProgressDialog progressDialog;
    LocationManager locationManager;
    private Location currentLocation;
    private static final int PERMISSION_CODE = 9913;

    @Override
    protected void onStart() {
        super.onStart();
        String[] per = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        for (String s : per) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, per, PERMISSION_CODE);
            }
        }
        isLocationEnabled();
        //get system location service instance1
        this.currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("لوڪيشن سينسر کي کوليو");
            alertDialog.setMessage("توهان جي جڳهه سينسر جي ترتيب جاري ناهي. مهرباني ڪري ان کي سيٽنگنگ مينيو اندر فعال ڪريو");
            alertDialog.setPositiveButton("مقام سينسر سيٽنگون", (dialog, which) -> {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            alertDialog.setNegativeButton("منسوخ ڪريو", (dialog, which) -> dialog.cancel());
            AlertDialog alert = alertDialog.create();
            alert.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.cameraImage);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        openCamera();

    }

    public void onAddImageClick(View view) {
        openCamera();
    }


    private String PATH;

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";

                File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        file     /* directory */
                );
                Uri photoURI = FileProvider.getUriForFile(this,
                        "sindh.agriculureextension.fadiary.fileprovider",
                        image.getAbsoluteFile());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                currentPhotoPath = image.getAbsolutePath();
                startActivityForResult(cameraIntent, IMAGE_PICK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(currentPhotoPath + " PATH");
        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK && currentPhotoPath != null) {
            System.out.println("Data is not null");
            imageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));

            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + SystemClock.currentThreadTimeMillis() + ".jpeg");
                FileOutputStream fos = new FileOutputStream(file);
                BitmapFactory.decodeFile(currentPhotoPath).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                PATH = file.getAbsolutePath();
                System.out.println("IMAGE WROTE");

                Intent intent = new Intent(this, FarmerForm.class);
                intent.putExtra(Queries._IMAGE, PATH);
                intent.putExtra(Queries._LAT, getIntent().getDoubleExtra(Queries._LAT,0));
                intent.putExtra(Queries._LANG, getIntent().getDoubleExtra(Queries._LANG,0));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            System.out.println("Data is null");
        }
    }

    public void onCameraClick(View view) {

        openCamera();
    }


    public void onCameraSave(View view) {
        if (PATH != null) {

            Intent intent = new Intent(this, FarmerForm.class);
            intent.putExtra(Queries._IMAGE, PATH);
            intent.putExtra(Queries._LAT, getIntent().getDoubleExtra(Queries._LAT,0));
            intent.putExtra(Queries._LANG, getIntent().getDoubleExtra(Queries._LANG,0));
            startActivity(intent);
        }


    }
}
