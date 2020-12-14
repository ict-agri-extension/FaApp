package sindh.agriculureextension.fadiary.visit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.form.VisitModel;
import sindh.agriculureextension.fadiary.network.AppHelper;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.VisitHolder> {

    private Context context;
    private VisitSelection selection;
    private List<VisitModel> list;
    private boolean isDiary;

    public VisitAdapter(Context context, VisitSelection selection, List<VisitModel> list) {
        this.context = context;
        this.selection = selection;
        this.list = list;
    }

    public VisitAdapter(Context context, VisitSelection selection, List<VisitModel> list, boolean isDiary) {
        this.context = context;
        this.selection = selection;
        this.list = list;
        this.isDiary = isDiary;
    }

    @NonNull
    @Override
    public VisitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VisitHolder(LayoutInflater.from(context).inflate(R.layout.visit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VisitHolder holder, int position) {

        VisitModel model = list.get(position);
        if (isDiary) {
            holder.view.setVisibility(View.GONE);
            if (model.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(model.getImage());
                holder.image.setImageBitmap(bitmap);
            }
            holder.download.setEnabled(true);
            holder.download.setOnClickListener(view -> {
                String msg = AppHelper.saveImage(AppHelper.getBitmapFromView(holder.cardView));
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                this.notifyDataSetChanged();
            });
        } else {
            System.out.println("ADDRESS "+model.getAddress());
            if (model.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(model.getImage());
                holder.image.setImageBitmap(bitmap);
            }
            holder.lang.setText(String.valueOf(model.getLANG()));
            holder.lat.setText(String.valueOf(model.getLAT()));

            if (model.getAddress() != null) {
                holder.address.setText(model.getAddress());
                holder.download.setEnabled(true);
                holder.download.setOnClickListener(view -> {
                    String msg = AppHelper.saveImage(AppHelper.getBitmapFromView(holder.cardView));
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    this.notifyDataSetChanged();
                });
            }
            System.out.println("TIME IS "+model.getTime());
            if(model.getTime()>0){
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(model.getTime());
                try{
                    holder.timestamp.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(calendar.getTime()));
                }catch (Exception x){
                    x.printStackTrace();
                }
            }
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VisitHolder extends RecyclerView.ViewHolder {

        private TextView address, lat, lang,timestamp;
        private ImageView image;
        private CardView cardView;
        private View view;
        private ExtendedFloatingActionButton download;

        public VisitHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.visitGprs);
            timestamp=view.findViewById(R.id.gpsTimeAndDate);
            address = view.findViewById(R.id.gprsAddress);
            lat = view.findViewById(R.id.gprsLAT);
            cardView = itemView.findViewById(R.id.visitCard);
            lang = view.findViewById(R.id.gprsLANG);
            download = itemView.findViewById(R.id.visitDownload);
            download.setEnabled(false);
            image = itemView.findViewById(R.id.visitImage);
        }
    }

    public interface VisitSelection {
        void onVisitSelect(int index);
    }
}
