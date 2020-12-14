package sindh.agriculureextension.fadiary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import sindh.agriculureextension.fadiary.camera.CameraActivity;
import sindh.agriculureextension.fadiary.database.DbHelper;
import sindh.agriculureextension.fadiary.network.AppHelper;
import sindh.agriculureextension.fadiary.network.InternetWatcher;
import sindh.agriculureextension.fadiary.network.LocationData;
import sindh.agriculureextension.fadiary.network.ReqQueue;
import sindh.agriculureextension.fadiary.user.Profile;
import sindh.agriculureextension.fadiary.user.UserSession;
import sindh.agriculureextension.fadiary.visit.VisitActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 9913;
    private static final int LOCATION_REFRESH_DISTANCE = 2; // 2 meters
    private static final int LOCATION_REFRESH_TIME = 2000; // 2 seconds
    String currentPhotoPath;
    private static final int IMAGE_PICK = 25;
    private static final int LOCUST_CODE = 153;
    private Handler handler;
    LocationManager locationManager;
    private Location currentLocation;

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("START");
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


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        handler=new Handler();
        //register receiver
        InternetWatcher watcher = new InternetWatcher();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(watcher, filter);
        //copy pdf file from assets
        new Thread(() -> {
            File fileBrochure = new File(Environment.getExternalStorageDirectory() + "/" + "guide.pdf");
            if (!fileBrochure.exists()) {
                System.out.println("Guide not exists creating file...");
                CopyAssetsbrochure();
            }
        }).start();

        findViewById(R.id.mainCameraCard)
                .setOnClickListener(view ->
                        startActivity(new Intent(this, CameraActivity.class)));

        findViewById(R.id.mainGrid).setAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));

        findViewById(R.id.mainProfile).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, Profile.class
                )));


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, locationListenerGPS);

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
            handler.post(()->{
                AlertDialog alert = alertDialog.create();
                alert.show();
            });
        }

    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            currentLocation = location;
            LocationData locationData = new LocationData(getBaseContext());
            locationData.setLang(currentLocation.getLongitude());
            locationData.setLat(currentLocation.getLatitude());

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            System.out.println(msg);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            if(provider==null)
                return;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("لوڪيشن سينسر کي کوليو");
            alertDialog.setMessage("توهان جي جڳهه سينسر جي ترتيب جاري ناهي. مهرباني ڪري ان کي سيٽنگنگ مينيو اندر فعال ڪريو");
            alertDialog.setPositiveButton("مقام سينسر سيٽنگون", (dialog, which) -> {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            alertDialog.setNegativeButton("منسوخ ڪريو", (dialog, which) -> dialog.cancel());
            handler.post(()->{
                AlertDialog alert = alertDialog.create();
                alert.show();
            });

        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 42365) {
            //call  permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted make a call
                makeACall();
            }

        }
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                onStart();
                break;
            }
        }
    }

    public void onFeedbackClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this
                , android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        View v = LayoutInflater.from(this).inflate(R.layout.feedback, null);
        builder.setView(v);
        AlertDialog dialog = builder.create();

        v.findViewById(R.id.feedbackProfile).setOnClickListener(view1 -> dialog.cancel());
        dialog.show();
        v.findViewById(R.id.feedbackSubmit).setOnClickListener(view1 -> {
            EditText editText = v.findViewById(R.id.feedbackField);
            if (editText.getText().toString().length() > 0) {
                String feedback = editText.getText().toString();
                editText.setText("");
                dialog.cancel();
                StringRequest request = new StringRequest(Request.Method.POST, AppHelper.BASE_URL,
                        response -> {
                            if (response.contains("response") && response.contains("200")) {
                                Toast.makeText(this, "Feedback submitted!/جواب راءِ ڪاميابي سان جمع ڪئي وئي!",
                                        Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            } else {
                                editText.setText(feedback);
                                Toast.makeText(this, "Check internet!", Toast.LENGTH_SHORT).show();
                            }

                        }, error -> {
                    Toast.makeText(this, error.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    editText.setText(feedback);
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        map.put("FAID", String.valueOf(UserSession.getInstance(getApplicationContext()).getFAID()));
                        map.put("FEEDBACK", feedback);
                        map.put("REQUEST", "FEEDBACK_TASK");
                        return map;
                    }
                };
                ReqQueue.getInstance(getApplicationContext()).addToRequestQueue(request);
            } else {
                editText.setError("Can't submit empty data!");
            }
        });
    }


    public void onProfileClick(View view) {
        startActivity(new Intent(MainActivity.this, Profile.class));
    }

    public void onSAGPClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sagp.gos.pk/"));
        startActivity(browserIntent);
    }

    public void onMainDiaryClick(View view) {
        //capture diary image only....
        openCamera("DIARY");
    }


    private void openCamera(String task) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

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

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (task.equals("DIARY")) {
                //open camera for diary
                startActivityForResult(cameraIntent, IMAGE_PICK);

            } else if (task.equals("LOCUST")) {
                //open cam for locust
                startActivityForResult(cameraIntent, LOCUST_CODE);
            }

        }

    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(currentPhotoPath + " PATH");
        if (resultCode == RESULT_OK && currentPhotoPath != null) {
            System.out.println("Data is not null");
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + SystemClock.currentThreadTimeMillis() + ".jpeg");
                FileOutputStream fos = new FileOutputStream(file);
                BitmapFactory.decodeFile(currentPhotoPath).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                if (requestCode == IMAGE_PICK) {
                    DbHelper.getInstance(this).addDiaryRecord(file.getAbsolutePath());
                    Toast.makeText(this, "Diary image has been saved!", Toast.LENGTH_SHORT).show();
                    recreate();
                } else if (requestCode == LOCUST_CODE && currentLocation != null) {
                    DbHelper.getInstance(this).addLocustRecord(file.getAbsolutePath(), currentLocation.getLatitude()
                            , currentLocation.getLongitude());
                    recreate();
                    Toast.makeText(this, "Locust image has been saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Move device so that we can get location..",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            System.out.println("Data is null");
        }
    }

    public void onLocustReportClick(View view) {
        openCamera("LOCUST");
    }

    public void onHelpLineClick(View view) {
        // dial helpline
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            //ask permissions
            String[] arr = {Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(this, arr, 42365);
        } else {
            makeACall();
        }
    }

    private void makeACall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+923111646111"));
        startActivity(callIntent);
    }

    public void onFieldGuideClick(View view) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "guide.pdf");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(file.getAbsolutePath()), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "NO Pdf Viewer app found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaiSatabiClick(View view) {
        String url = "https://www.facebook.com/ictagriextensionsindh/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }


    //method to write the PDFs file to sd card
    private void CopyAssetsbrochure() {
        AssetManager assetManager = getAssets();

        try {
            String fStr = Objects.requireNonNull(assetManager.list(""))[0];
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(fStr);
                out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + fStr);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;

            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void onPictureCaptures(View view) {
        startActivity(new Intent(this, VisitActivity.class));
    }

}
