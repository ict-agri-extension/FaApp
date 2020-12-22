package sindh.agriculureextension.fadiary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sindh.agriculureextension.fadiary.database.DbHelper;
import sindh.agriculureextension.fadiary.database.Queries;
import sindh.agriculureextension.fadiary.form.VisitModel;
import sindh.agriculureextension.fadiary.network.AppHelper;
import sindh.agriculureextension.fadiary.network.ReqQueue;
import sindh.agriculureextension.fadiary.user.UserSession;

public class Uploader extends JobIntentService {

    private static final int JOB_ID = 789;
    private static final int NOTIFICATION_ID = 79;

    public static void enqueue(Context context, Intent intent) {
        enqueueWork(context, Uploader.class, JOB_ID, intent);
    }

    private NotificationCompat.Builder notification;

    private NotificationManager notificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "Image Upload Channel";
    public static final String CHANNEL_NAME = "Upload Image";
    private static final String TAG = "ImageService";

    private List<VisitModel> list;
    private List<Map<String, String>> diaryImages;
    private List<Map<String, String>> locustImages;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //get list pending tasks to be uploaded

        list = DbHelper.getInstance(getApplicationContext())
                .recordsToBeUploaded();

        //if there is available task then proceed
        if (list.size() > 0) {

            //check app version for notification channel handling because of api level 28+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }
            notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(false)
                    .setContentTitle("Uploading Visits Data")
                    //indeterminate true because size and tasks aren't defined in numbers
                    .setProgress(0, 0, true)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .setSmallIcon(R.mipmap.ic_launcher_foreground);

            //notification service
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(NOTIFICATION_ID, notification.build());
            //convert image into base46 and reduce size...
            Log.d(TAG, "onHandleWork: LIST TASK SIZE:" + list.size());
            VisitModel model = list.get(0);
            list.remove(0);
            proceed(model);
        }

        //get diary images
        diaryImages = DbHelper.getInstance(getApplicationContext()).diaryRecordsToBeUploaded();

        if (diaryImages != null && diaryImages.size() > 0) {
            System.out.println("DIARY IMAGES: " + diaryImages.toString());
            //check app version for notification channel handling because of api level 28+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }
            notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(false)
                    .setContentTitle("Uploading Diary Image")
                    //indeterminate true because size and tasks aren't defined in numbers
                    .setProgress(0, 0, true)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .setSmallIcon(R.mipmap.ic_launcher_foreground);

            //notification service
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(NOTIFICATION_ID, notification.build());
            //convert image into base46 and reduce size...
            Log.d(TAG, "onHandleWork: LIST TASK SIZE:" + diaryImages.size());
            proceedWithDiary(diaryImages.get(0));
        }


        //get locust images
        locustImages = DbHelper.getInstance(getApplicationContext()).locustRecordsToBeUploaded();
        System.out.println("LOCUST LIST: " + locustImages.toString());
        if (locustImages != null && locustImages.size() > 0) {
            System.out.println("LOCUST IMAGES: " + locustImages.toString());
            //check app version for notification channel handling because of api level 28+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }
            notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(false)
                    .setContentTitle("Uploading Locust Image")
                    //indeterminate true because size and tasks aren't defined in numbers
                    .setProgress(0, 0, true)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .setSmallIcon(R.mipmap.ic_launcher_foreground);

            //notification service
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(NOTIFICATION_ID, notification.build());
            //convert image into base46 and reduce size...
            Log.d(TAG, "onHandleWork: LIST TASK SIZE:" + locustImages.size());
            Map<String, String> map = locustImages.get(0);
            double lat = Double.parseDouble(String.valueOf(map.get("_LAT")));
            double lang = Double.parseDouble(String.valueOf(map.get("_LANG")));
            Address address = AppHelper.getLocationData(lat, lang, getApplicationContext());
            if (address != null) {
                map.put("ADDRESS", address.getAddressLine(0));
                map.put("LOCATION_NAME", address.getFeatureName());
                map.put("LAT", String.valueOf(lat));
                map.put("LANG", String.valueOf(lang));

                proceedWithLocust(locustImages.get(0));
            } else {
                Log.d(TAG, "onHandleWork: Address not found for locust lat lang!");
            }

        }

    }


    private void proceedWithLocust(Map<String, String> map) {
        System.out.println("LOCUST DATA " + map);
        if (map.get(Queries._IMAGE) != null) {
            String base64 = AppHelper.getBase64(String.valueOf(map.get(Queries._IMAGE)));
            UserSession session = UserSession.getInstance(getApplicationContext());
            map.put("FAID", String.valueOf(session.getFAID()));
            map.remove("IMAGE");
            map.put("REQUEST", "LOCUST_TASK");
            System.out.println("UPLOADING LOCUST IMAGE..." + map);
            map.put("IMAGE", base64);

            notification.setProgress(0, 0, true);
            notificationManager.notify(NOTIFICATION_ID, notification.build());

            String url = AppHelper.BASE_URL;
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                System.out.println("RESPONSE OF LOCUST IS: " + response);
                if (response.contains("response") && response.contains("200")) {
                    //records added
                    notification.setContentText("Locust Data Uploaded");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    DbHelper.getInstance(getApplicationContext())
                            .updateLocustStatus(Integer.parseInt(String.valueOf(map.get(Queries._ID))), map.get("ADDRESS"));
                    locustImages.remove(0);
                    if (locustImages.size() > 0) {
                        map.clear();
                        proceedWithDiary(locustImages.get(0));
                    }
                } else if (response.contains("response") && response.contains("100")) {
                    notification.setContentText("Image uploaded!");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    DbHelper.getInstance(getApplicationContext())
                            .updateLocustStatus(Integer.parseInt(String.valueOf(map.get(Queries._ID))), map.get("ADDRESS"));
                    locustImages.remove(0);
                    if (locustImages.size() > 0) {
                        map.clear();
                        proceedWithDiary(locustImages.get(0));
                    }
                } else {
                    notification.setContentText("Image uploaded!");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                }
            }, error -> {
                error.printStackTrace();
                notification.setContentText("Something went wrong!");
                notification.setProgress(100, 100, false);
                notificationManager.notify(NOTIFICATION_ID, notification.build());

            }) {
                @Override
                protected Map<String, String> getParams() {
                    return map;
                }

            };

            request.setRetryPolicy(new DefaultRetryPolicy(0, 0
                    , 0));
            ReqQueue.getInstance(getApplicationContext()).addToRequestQueue(request);

        }
    }


    private void proceedWithDiary(Map<String, String> map) {
        System.out.println("MAP DATA " + map);
        if (map.get(Queries.DIARY_IMAGE) != null) {
            String base64 = AppHelper.getBase64(String.valueOf(map.get(Queries.DIARY_IMAGE)));
            UserSession session = UserSession.getInstance(getApplicationContext());
            map.put("FAID", String.valueOf(session.getFAID()));
            map.put("DIARY", String.valueOf(session.getDiary()));
            map.remove("IMAGE");
            map.put("IMAGE", base64);
            System.out.println("UPLOADING DIARY IMAGE...");
            notification.setProgress(0, 0, true);
            notificationManager.notify(NOTIFICATION_ID, notification.build());

            String url = AppHelper.BASE_URL;
            map.put("REQUEST", "DIARY_TASK");
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                System.out.println("RESPONSE IS: " + response);
                if (response.contains("response") && response.contains("200")) {
                    //records added

                    notification.setContentText("Diary Data Uploaded");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    DbHelper.getInstance(getApplicationContext())
                            .updateDiaryStatus(Integer.parseInt(String.valueOf(map.get(Queries._ID))));
                    diaryImages.remove(0);
                    if (diaryImages.size() > 0) {
                        map.clear();
                        proceedWithDiary(diaryImages.get(0));
                    }
                }else if(response.contains("response") && response.contains("100")){
                    notification.setContentText("Diary Data Uploaded");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    DbHelper.getInstance(getApplicationContext())
                            .updateDiaryStatus(Integer.parseInt(String.valueOf(map.get(Queries._ID))));
                    diaryImages.remove(0);
                    if (diaryImages.size() > 0) {
                        map.clear();
                        proceedWithDiary(diaryImages.get(0));
                    }
                } else {
                    notification.setContentText("Server internal error!");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                }
            }, error -> {
                error.printStackTrace();
                notification.setContentText("Something went wrong!");
                notification.setProgress(100, 100, false);
                notificationManager.notify(NOTIFICATION_ID, notification.build());

            }) {
                @Override
                protected Map<String, String> getParams() {
                    return map;
                }

            };

            request.setRetryPolicy(new DefaultRetryPolicy(0, 0
                    , 0));
            ReqQueue.getInstance(getApplicationContext()).addToRequestQueue(request);

        }
    }


    private void proceed(VisitModel model) {
        if (model.getAddress() == null) {
            Address address = AppHelper.getLocationData(model.getLAT(), model.getLANG(),
                    getApplicationContext());
            if (address != null) {
                model.setAddress(address.getAddressLine(0));
                model.setLocationName(address.getFeatureName());
                DbHelper.getInstance(getApplicationContext()).updateData(model);
                String base64 = AppHelper.getBase64(model.getImage());
                model.setImage(base64);
                uploadImage(model);
            } else {
                Log.d(TAG, "onHandleWork: No address found with that coordinates!");
                onDestroy();
            }

        } else {
            String base64 = AppHelper.getBase64(model.getImage());
            model.setImage(base64);
            uploadImage(model);
        }
    }

    private void uploadImage(VisitModel model) {
        UserSession session = UserSession.getInstance(getApplicationContext());
        if (model.getImage() != null && model.getAddress() != null && session != null && model.getImage() != null) {
            Map<String, String> map = new HashMap<>();
            System.out.println("VISIT TIME " + model.getTime());
            map.put("FAID", String.valueOf(session.getFAID()));
            map.put("DIARY_NUMBER", String.valueOf(session.getDiary()));
            map.put("LAT", String.valueOf(model.getLAT()));
            map.put("LANG", String.valueOf(model.getLANG()));
            map.put("REQUEST", "VISIT");
            map.put("ADDRESS", String.valueOf(model.getAddress()));
            map.put("LOCATION_NAME", String.valueOf(model.getLocationName()));
            map.put("QUESTION", model.getQuestion());
            map.put("SUGGESTION", model.getSuggestion());
            map.put("ABADGAR_NAME", model.getFarmer());
            map.put("ABADGAR_PHONE", model.getFarmerPhone());
            map.put("IMAGE", model.getImage());
            map.put("_TIME", String.valueOf(model.getTime()));
            map.put("ID", String.valueOf(model.getID()));
            System.out.println("VISIT MAP: " + map.toString());
            notification.setProgress(0, 0, true);
            notificationManager.notify(NOTIFICATION_ID, notification.build());
            String url = AppHelper.BASE_URL;

            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                System.out.println("RESPONSE OF VISIT IS: " + response);
                if (response.contains("response") && response.contains("200")) {
                    //records added
                    notification.setContentText("Visit Data Uploaded");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    DbHelper.getInstance(getApplicationContext()).updateStatus(model.getID());
                    if (list.size() > 0) {
                        VisitModel next = list.get(0);
                        list.remove(next);
                        proceed(next);
                    }
                } else if (response.contains("response") && response.contains("100")) {
                    notification.setContentText("Visit Data Uploaded");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    DbHelper.getInstance(getApplicationContext()).updateStatus(model.getID());
                } else {
                    notification.setContentText("Server internal error! Try again later");
                    notification.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                }
            }, error -> {
                error.printStackTrace();
                notification.setContentText("Something went wrong! Try again later.");
                notification.setProgress(100, 100, false);
                notificationManager.notify(NOTIFICATION_ID, notification.build());

            }) {
                @Override
                protected Map<String, String> getParams() {
                    return map;
                }

            };

            request.setRetryPolicy(new DefaultRetryPolicy(0, 0
                    , 0));
            ReqQueue.getInstance(getApplicationContext()).addToRequestQueue(request);
        } else {
            notification.setContentText("Upload failed! check internet");
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        //Notification channel should only be created for devices running Android 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID
                    , CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

}
