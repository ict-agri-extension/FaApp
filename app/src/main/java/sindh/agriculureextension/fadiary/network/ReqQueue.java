package sindh.agriculureextension.fadiary.network;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class ReqQueue {
    @SuppressLint("StaticFieldLeak")
    private static ReqQueue instance;
    private RequestQueue requestQueue;
    private Context ctx;

    private ReqQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ReqQueue getInstance(Context context) {
        if (instance == null) {
            instance = new ReqQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext(),new HurlStack());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req).setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
