package sindh.agriculureextension.fadiary;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import sindh.agriculureextension.fadiary.signin.SignIn;

public class Welcome extends AppCompatActivity {


    private static final int PERMISSION_CODE = 9913;

    private void permissionChecker() {
        System.out.println("START");
        String[] per = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        for (String s : per) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, per, PERMISSION_CODE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionChecker();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Thread(() -> {
            File fileBrochure = new File(Environment.getExternalStorageDirectory() + "/" + "guide.pdf");
            if (!fileBrochure.exists()) {
                System.out.println("Guide not exists creating file...");
                CopyAssetsbrochure();
            }
        }).start();

    }

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
                System.out.println(e.getLocalizedMessage());
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


    private void makeACall() {
        // dial helpline
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            //ask permissions
            String[] arr = {Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(this, arr, 42365);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+923111646111"));
            startActivity(callIntent);
        }
    }



    public void welcomeOnGuideClick(View view) {
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

    public void welcomeOnCallClick(View view) {
        makeACall();
    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL() {
        PackageManager packageManager = this.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + "https://www.facebook.com/ictagriextensionsindh";
            } else { //older versions of fb app
                return "fb://page/" + "ictagriextensionsindh";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "https://www.facebook.com/ictagriextensionsindh/"; //normal web url
        }
    }

    public void welcomeOnFacebookClick(View view) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL();
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    public void welcomeOnYouTubeClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.youtube.com/channel/UCqTcNu8o9s2y7vjSiafo8kQ"));
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

    public void welcomeOnLoginClick(View view) {
        startActivity(new Intent(this, SignIn.class));
    }
}