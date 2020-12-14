package sindh.agriculureextension.fadiary.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import sindh.agriculureextension.fadiary.Uploader;

public class InternetWatcher extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();

        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        if(connected){
            //upload remaining tasks
            System.out.println("INTERNET IS AVAILABLE");
            uploadRemainingTasks(context);
        }else
            System.out.println("INTERNET IS OFF!");
    }

    private void uploadRemainingTasks(Context context) {
        Intent intent=new Intent(context, Uploader.class);
        Uploader.enqueue(context,intent);
    }
}
