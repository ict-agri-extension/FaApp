package sindh.agriculureextension.fadiary.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import sindh.agriculureextension.fadiary.MainActivity;
import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.signin.SignIn;
import sindh.agriculureextension.fadiary.user.UserSession;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getActionBar() != null)
            getActionBar().hide();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (UserSession.getInstance(Splash.this).getFAID() != 0)
                startActivity(new Intent(Splash.this, MainActivity.class));
            else
                startActivity(new Intent(Splash.this, SignIn.class));

            Splash.this.finish();
        }, 3000);
    }
}
