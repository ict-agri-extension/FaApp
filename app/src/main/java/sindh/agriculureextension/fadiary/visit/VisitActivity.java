package sindh.agriculureextension.fadiary.visit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.database.DbHelper;
import sindh.agriculureextension.fadiary.database.Queries;
import sindh.agriculureextension.fadiary.form.VisitModel;

public class VisitActivity extends AppCompatActivity implements VisitAdapter.VisitSelection, GalleryAdapter.GallerySelection {

    private RecyclerView recyclerView;
    List<Map<String, String>> mapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        recyclerView = findViewById(R.id.visitRecycler);
        recyclerView.setHasFixedSize(true);
        setAlbum();
        findViewById(R.id.visitProfile).setOnClickListener(view -> this.finish());
    }

    private void setAlbum() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        DbHelper db = DbHelper.getInstance(this);

        mapList.clear();
        List<VisitModel> list = db.records();
        if (list.size() > 0) {
            Map<String, String> map = new HashMap<>();
            map.put("TITLE", getResources().getString(R.string.camera_title));
            VisitModel model = list.get(list.size() - 1);
            map.put("IMAGE", model.getImage());
            mapList.add(map);
        }
        if (db.locustRecordUploaded().size() > 0) {
            Map<String, String> map = new HashMap<>();
            Map<String, String> locust = db.locustRecordUploaded().get(0);
            map.put("TITLE", getResources().getString(R.string.locust_report));
            map.put("IMAGE", locust.get(Queries._IMAGE));
            mapList.add(map);
        }
        if (db.diaryRecordUploaded().size() > 0) {
            Map<String, String> map = new HashMap<>();
            Map<String, String> diary = db.diaryRecordUploaded().get(0);
            map.put("TITLE", getResources().getString(R.string.diary));
            map.put("IMAGE", diary.get(Queries.DIARY_IMAGE));
            mapList.add(map);
        }

        recyclerView.setAdapter(new GalleryAdapter(this, this, mapList));
    }

    private void getAllDiaryImages() {
        List<Map<String,String>> list = new ArrayList<>(DbHelper.getInstance(this).diaryRecordUploaded());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<VisitModel> modelList=new ArrayList<>();
        for(Map<String,String> map :list){
            VisitModel model=new VisitModel();
            model.setImage(map.get(Queries.DIARY_IMAGE));
            modelList.add(model);
        }
        recyclerView.setAdapter(new VisitAdapter(this, this, modelList,true));

    }

    private void getAllLocustImages() {
        List<Map<String,String>> list = new ArrayList<>(DbHelper.getInstance(this).locustRecordUploaded());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<VisitModel> modelList=new ArrayList<>();
        for(Map<String,String> map :list){
            VisitModel model=new VisitModel();
            model.setImage(map.get(Queries._IMAGE));
            model.setLAT(Double.parseDouble(Objects.requireNonNull(map.get(Queries._LAT))));
            model.setLANG(Double.parseDouble(Objects.requireNonNull(map.get(Queries._LANG))));
            model.setAddress(map.get(Queries._ADDRESS));
            modelList.add(model);
        }
        recyclerView.setAdapter(new VisitAdapter(this, this, modelList));

    }

    private void getAllVisitImages() {
        List<VisitModel> list = new ArrayList<>(DbHelper.getInstance(this).records());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VisitAdapter(this, this, list));
    }

    @Override
    public void onVisitSelect(int index) {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlbumSelect(int index) {
        //open selected album
        Map<String, String> map = mapList.get(index);
        String selected = map.get("TITLE");

        if (selected != null && selected.equalsIgnoreCase(getResources().getString(R.string.locust_report))) {
            getAllLocustImages();
            count = 0;
        } else if (selected != null && selected.equalsIgnoreCase(getResources().getString(R.string.diary))) {
            getAllDiaryImages();
            count = 0;
        } else if (selected != null && selected.equalsIgnoreCase(getResources().getString(R.string.camera_title))) {
            count = 0;
            getAllVisitImages();
        }
    }

    int count = 0;

    @Override
    public void onBackPressed() {
        if (count == 0) {
            setAlbum();
            count = 1;
        } else
            super.onBackPressed();
    }
}