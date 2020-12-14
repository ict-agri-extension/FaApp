package sindh.agriculureextension.fadiary.form;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import sindh.agriculureextension.fadiary.MainActivity;
import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.database.Queries;
import sindh.agriculureextension.fadiary.network.LocationData;

public class FarmerForm extends AppCompatActivity {


    private double lang, lat;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_form);
        findViewById(R.id.formProfile).setOnClickListener(view -> this.finish());
        TextView textView = findViewById(R.id.farmerCoordinates);
        LocationData location = new LocationData(this);
        lat = location.getLat();
        lang = location.getLang();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Handler handler=new Handler();

        if (lat == 0) {
            LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (loc != null) {
                Location locLastKnownLocation = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                new Thread(() -> {
                    for (int i = 0; i < 15; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (locLastKnownLocation != null) {
                            lat = locLastKnownLocation.getLatitude();
                            lang = locLastKnownLocation.getLongitude();
                            handler.post(()->{
                                progressDialog.cancel();
                                 String txt = "LAT: " + lat
                                        + " , LANG: " + lang;
                                textView.setText(txt);
                            });

                        }
                    }
                    handler.post(()->{
                        progressDialog.cancel();
                        if (lang == 0 || lat == 0) {
                            Toast.makeText(this, "Try again!", Toast.LENGTH_LONG).show();
                            this.finish();
                        }
                        String txt = "LAT: " + lat
                                + " , LANG: " + lang;
                        textView.setText(txt);
                    });

                }).start();

            }
        }

    }

    public void onFarmerFormSubmit(View view) {
        EditText farmer = findViewById(R.id.farmerName);
        EditText question = findViewById(R.id.farmerQuesstion);
        EditText answer = findViewById(R.id.farmerSuggestion);
        EditText phone = findViewById(R.id.farmerPhone);
       /* if (farmer.length() > 3 && question.length() > 3 && answer.length() > 3
                && phone.getText().toString().length() == 11) {
            VisitModel model = new VisitModel();
            model.setSuggestion(answer.getText().toString());
            model.setQuestion(question.getText().toString());
            model.setFarmer(farmer.getText().toString());
            model.setStatus(0);
            model.setImage(getIntent().getStringExtra(Queries._IMAGE));
            model.setLANG(lang);
            model.setLAT(lat);
            model.setFarmerPhone(phone.getText().toString());
            //send data to local database
            new Thread(new AddVisitData(getApplicationContext(), model)).start();
            //redirect to main activity....
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finishAffinity();
        } else {
            Toast.makeText(this, "Invalid data!", Toast.LENGTH_SHORT).show();
        }*/

        VisitModel model = new VisitModel();
        model.setSuggestion(answer.getText().toString());
        model.setQuestion(question.getText().toString());
        model.setFarmer(farmer.getText().toString());
        model.setStatus(0);
        model.setImage(getIntent().getStringExtra(Queries._IMAGE));
        model.setLANG(lang);
        model.setLAT(lat);
        model.setFarmerPhone(phone.getText().toString());
        model.setTime(Calendar.getInstance().getTimeInMillis());
        //send data to local database
        new Thread(new AddVisitData(getApplicationContext(), model)).start();
        //redirect to main activity....
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finishAffinity();
    }
}