package com.maotou.dagger2study;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.maotou.dagger2study.Lazy.LazyActivity;
import com.maotou.dagger2study.provider.ProviderActivity;
import com.maotou.dagger2study.inject.InjectActivity;
import com.maotou.dagger2study.module.ModuleActivity;
import com.maotou.dagger2study.qulifier.QualifierActivity;
import com.maotou.dagger2study.scope.ScopeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.listview)
    ListView listView;
    String[] list = new String[]{"@Inject","@Module","@Qualifier","@Scope","Lazy<T>","Provider<T>"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.item_listview,list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        gotonext(InjectActivity.class);
                        break;
                    case 1:
                        gotonext(ModuleActivity.class);
                        break;
                    case 2:
                        gotonext(QualifierActivity.class);
                        break;
                    case 3:
                        gotonext(ScopeActivity.class);
                        break;
                    case 4:
                        gotonext(LazyActivity.class);
                        break;
                    case 5:
                        gotonext(ProviderActivity.class);
                        break;
                }
            }
        });
    }

    private void gotonext(Class clazz){
        this.startActivity(new Intent(this,clazz));
    }

}
