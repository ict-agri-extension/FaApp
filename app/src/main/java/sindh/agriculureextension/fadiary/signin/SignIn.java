package sindh.agriculureextension.fadiary.signin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sindh.agriculureextension.fadiary.MainActivity;
import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.network.AppHelper;
import sindh.agriculureextension.fadiary.network.ReqQueue;
import sindh.agriculureextension.fadiary.user.UserSession;

public class SignIn extends AppCompatActivity {

    private EditText phoneField;
    private String PHONE;
    private ProgressDialog progressDialog;
    private static final int PERMISSION_CODE = 9913;
    private String FCM;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViewById(R.id.signInForm).setAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
        phoneField = findViewById(R.id.signInPhone);
        progressDialog = new ProgressDialog(this);
        //progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait/مهرباني ڪري انتظار ڪريو");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    this.FCM=instanceIdResult.getToken();
                });

    }

    public void onSignInClick(View view) {

        String phone = phoneField.getText().toString();
        if (phone.length() == 11) {
            PHONE = phone;
            progressDialog.show();
            sendRequest(PHONE);
        } else {
            phoneField.setError("Invalid phone number!");
        }
    }

    private void sendRequest(String pn) {
        String url = AppHelper.BASE_URL;
        System.out.println(url);
        StringRequest request = new StringRequest(Request.Method.POST,
                url, response -> {
            System.out.println("RESPONSE " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (String.valueOf(jsonObject.get("response")).equals("200")) {
                    UserSession userSession = UserSession.getInstance(SignIn.this);
                    userSession.setUsername(String.valueOf(jsonObject.get("USERNAME")));
                    userSession.setPhone(String.valueOf(jsonObject.get("PHONE")));
                    userSession.setFAID(Integer.parseInt(String.valueOf(jsonObject.get("FAID"))));
                    userSession.setDiary(Integer.parseInt(String.valueOf(jsonObject.get("DIARY"))));
                    userSession.setSerialNo(Integer.parseInt(String.valueOf(jsonObject.get("SERIAL_NO"))));
                    userSession.setUcId(Integer.parseInt(String.valueOf(jsonObject.get("UCID"))));
                    userSession.setDistrict(String.valueOf(jsonObject.get("DISTRICT")));
                    userSession.setDisId(Integer.parseInt(String.valueOf(jsonObject.get("DISID"))));
                    userSession.setYEAR(Integer.parseInt(String.valueOf(jsonObject.get("YEAR"))));
                    userSession.setEmail(String.valueOf(jsonObject.get("EMAIL")));
                    userSession.setTaluka(String.valueOf(jsonObject.get("UCNAME")));

                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    progressDialog.cancel();
                    startActivity(intent);

                }else {
                    progressDialog.cancel();
                    Toast.makeText(SignIn.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                progressDialog.cancel();
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            progressDialog.cancel();
            error.printStackTrace();
            Toast.makeText(SignIn.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("ERROR " + error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("FCM_TOKEN",FCM);
                map.put("DEVICE",AppHelper.getDeviceName());
                map.put("DEVICE_ADDRESS",AppHelper.getDeviceInfo());
                map.put("PHONE", pn);
                map.put("REQUEST","FAS_TASK");
                map.put("TASK","LOGIN");

                return map;
            }

        };
        ReqQueue.getInstance(SignIn.this).addToRequestQueue(request);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                onStart();
                break;
            }
        }
    }

}
