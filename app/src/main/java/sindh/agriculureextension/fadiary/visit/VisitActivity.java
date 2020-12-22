package sindh.agriculureextension.fadiary.visit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.database.DbHelper;
import sindh.agriculureextension.fadiary.database.Queries;
import sindh.agriculureextension.fadiary.form.VisitModel;

public class VisitActivity extends AppCompatActivity implements VisitAdapter.VisitSelection,
        TabLayout.OnTabSelectedListener {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        recyclerView = findViewById(R.id.visitRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.visitProfile).setOnClickListener(view -> this.finish());
        TabLayout tabLayout = findViewById(R.id.visitTabLayout);
        tabLayout.addOnTabSelectedListener(this);
        getAllVisitImages();
    }



    private void getAllDiaryImages() {
        List<Map<String,String>> list = new ArrayList<>(DbHelper.getInstance(this).diaryRecordUploaded());
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
        recyclerView.setAdapter(new VisitAdapter(this, this, list));

    }

    @Override
    public void onVisitSelect(int index) {

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:{
                //Visit images
                getAllVisitImages();
                break;
            }
            case 1:{
                //Diary
                getAllDiaryImages();
                break;
            }
            case 2:{
                //Locust
                getAllLocustImages();
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:{
                //Visit images
                getAllVisitImages();
                break;
            }
            case 1:{
                //Diary
                getAllDiaryImages();
                break;
            }
            case 2:{
                //Locust
                getAllLocustImages();
                break;
            }
            default:{
                break;
            }
        }
    }
}