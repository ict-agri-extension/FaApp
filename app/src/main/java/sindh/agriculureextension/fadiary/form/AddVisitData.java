package sindh.agriculureextension.fadiary.form;

import android.content.Context;

import sindh.agriculureextension.fadiary.database.DbHelper;

public class AddVisitData implements  Runnable {

    private Context context;
    private VisitModel model;

    public AddVisitData(Context context, VisitModel model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public void run() {
        boolean bol=DbHelper.getInstance(context).addRecord(model);
        System.out.println(AddVisitData.class.getName()+" Record added "+bol);
    }
}
