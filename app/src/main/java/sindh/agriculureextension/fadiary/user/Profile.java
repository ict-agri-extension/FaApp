package sindh.agriculureextension.fadiary.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.network.AppHelper;
import sindh.agriculureextension.fadiary.network.ReqQueue;
import sindh.agriculureextension.fadiary.signin.SignIn;

public class Profile extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViewById(R.id.profileBack).setOnClickListener(view -> this.finish());
        ListView listView = findViewById(R.id.profileInfo);
        List<String> list = new ArrayList<>();
        UserSession session = UserSession.getInstance(this);
        list.add("ID: " + session.getFAID());
        list.add("Name: " + session.getUsername());
        list.add("Phone: " + session.getPhone());
        list.add("Email: " + session.getEmail());
        list.add("Diary Number: " + session.getSerialNo());
        list.add("District: " + session.getDistrict());
        list.add("Taluka: " + session.getTaluka());
        list.add("Year: " + session.getYEAR());

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));

    }

    public void onLogoutClick(View view) {
        Context context = this;
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, AppHelper.BASE_URL,
                response -> {
                    System.out.println("RESPONSE OF LOGOUT REQUEST: "+response);
                    if (response.contains("200")) {
                        progressDialog.cancel();
                        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                        Logout.getInstance(this).logout();
                        Intent intent = new Intent(this, SignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        this.finishAffinity();
                    } else {
                        progressDialog.cancel();
                        Toast.makeText(context, "Failed to logout! Check internet!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.cancel();
                    Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("REQUEST","FAS_TASK");
                map.put("TASK","LOGOUT");
                map.put("FAID",String.valueOf(UserSession.getInstance(context).getFAID()));
                return map;
            }
        };
        ReqQueue.getInstance(context).addToRequestQueue(request);

    }
}