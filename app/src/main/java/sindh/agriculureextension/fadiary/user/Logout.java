package sindh.agriculureextension.fadiary.user;

import android.content.Context;

import sindh.agriculureextension.fadiary.database.DbHelper;

public class Logout {
    private static Logout logout;
    private Context context;

    private Logout(Context context) {
        this.context = context;
    }

    public static synchronized Logout getInstance(Context context) {
        if (logout == null)
            logout = new Logout(context);
        return logout;
    }

    public void logout() {
        UserSession.getInstance(context).remove();
        DbHelper.getInstance(context).deleteAll();

    }
}
