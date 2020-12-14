package sindh.agriculureextension.fadiary.user;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static UserSession session;
    private Context context;
    private SharedPreferences preferences;

    private UserSession(Context context) {
        preferences = context.getSharedPreferences(UserSession.class.getName(), Context.MODE_PRIVATE);
    }

    public static synchronized UserSession getInstance(Context context) {
        if (session == null)
            session = new UserSession(context);
        return session;
    }


    private int YEAR;
    private int FAID;
    private String Phone;
    private String Taluka;
    private int UcId;
    private String username;
    private int diary;
    private String Email;
    private String District;
    private int DisId;
    private int SerialNo;


    public String getEmail() {
        return Email=preferences.getString("EMAIL",null);
    }

    public void setEmail(String email) {
        Email = email;
        preferences.edit().putString("EMAIL",email).apply();
    }

    public int getYEAR() {
        return YEAR=preferences.getInt("YEAR",0);
    }

    public void setYEAR(int YEAR) {
        this.YEAR = YEAR;
        preferences.edit().putInt("YEAR",YEAR).apply();
    }

    public int getUcId() {
        return UcId=preferences.getInt("UCID",0);
    }

    public void setUcId(int ucId) {
        UcId = ucId;
        preferences.edit().putInt("UCID",ucId).apply();
    }

    public String getDistrict() {
        return District=preferences.getString("DISTRICT",null);
    }

    public void setDistrict(String district) {
        District = district;
        preferences.edit().putString("DISTRICT",district).apply();
    }

    public int getDisId() {
        return DisId=preferences.getInt("DISID",0);
    }

    public void setDisId(int disId) {
        DisId = disId;
        preferences.edit().putInt("DISID",disId).apply();
    }

    public int getSerialNo() {
        return SerialNo=preferences.getInt("SERIAL",0);
    }

    public String getTaluka() {
        return Taluka=preferences.getString("TALUKA",null);
    }

    public void setTaluka(String taluka) {
        Taluka = taluka;
        preferences.edit().putString("TALUKA",taluka).apply();
    }

    public void setSerialNo(int serialNo) {
        SerialNo = serialNo;
        preferences.edit().putInt("SERIAL",serialNo).apply();
    }

    public int getDiary() {
        return diary = preferences.getInt("DIARY", 0);
    }

    public void setDiary(int diary) {
        this.diary = diary;
        preferences.edit().putInt("DIARY", diary).apply();
    }

    public int getFAID() {
        return FAID = preferences.getInt("FAID", 0);
    }

    public void setFAID(int FAID) {
        this.FAID = FAID;
        preferences.edit().putInt("FAID", FAID).apply();
    }

    public String getPhone() {
        return Phone = preferences.getString("PHONE", null);
    }

    public void setPhone(String phone) {
        Phone = phone;
        preferences.edit().putString("PHONE", phone).apply();
    }

    public String getUsername() {
        return username = preferences.getString("USERNAME", null);
    }

    public void setUsername(String username) {
        this.username = username;
        preferences.edit().putString("USERNAME", username).apply();
    }

    public void remove() {
        setFAID(0);
        setPhone(null);
        setUsername(null);
        setDiary(0);
        setTaluka(null);
        setSerialNo(0);
        setDisId(0);
        setDistrict(null);
        setUcId(0);
        setYEAR(0);
        setEmail(null);
    }
}
