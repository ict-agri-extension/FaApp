package sindh.agriculureextension.fadiary.visit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import sindh.agriculureextension.fadiary.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {

    private Context context;
    private GallerySelection selection;
    private List<Map<String,String>> list;

    public GalleryAdapter(Context context, GallerySelection selection, List<Map<String,String>> list) {
        this.context = context;
        this.selection = selection;
        this.list = list;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryHolder(LayoutInflater.from(context).inflate(R.layout.gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {

        Map<String,String> map = list.get(position);

        if (map.get("IMAGE") != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(map.get("IMAGE"));
            holder.image.setImageBitmap(bitmap);
        }

        holder.title.setText(String.valueOf(map.get("TITLE")));
        holder.itemView.setOnClickListener(view -> selection.onAlbumSelect(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class GalleryHolder extends RecyclerView.ViewHolder {


        private ImageView image;
        private TextView title;
        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.galleryImage);
            title=itemView.findViewById(R.id.galleryAlbumTitle);
        }
    }

    public interface GallerySelection {
        void onAlbumSelect(int index);
    }
}
