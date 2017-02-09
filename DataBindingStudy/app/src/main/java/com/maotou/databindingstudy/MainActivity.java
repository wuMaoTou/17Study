package com.maotou.databindingstudy;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maotou.databindingstudy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    public String[] menu = new String[]{"BindingViewId","BindinigData","BindingModule","BindingEvent","BingingStaticCast",
            "BindingModuleObserver","BindingMap","BindingInclude"};
    public static final int BINDING_VIEW_ID = 0;
    public static final int BINDING_DATA = 1;
    public static final int BINDING_MODULE = 2;
    public static final int BINDING_EVENT = 3;
    public static final int BINDING_STATIC_CAST = 4;
    public static final int BINDING_MODULE_OBSERVER = 5;
    public static final int BINDING_MAP = 6;
    public static final int BINDING_INCLUDE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.listview.setAdapter(new MyAdapter());
        mainBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case BINDING_VIEW_ID:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingViewIdActivity.class));
                        break;
                    case BINDING_DATA:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingDataActivity.class));
                        break;
                    case BINDING_MODULE:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingModuleActivity.class));
                        break;
                    case BINDING_EVENT:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingEventActivity.class));
                        break;
                    case BINDING_STATIC_CAST:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingStaticCastActivity.class));
                        break;
                    case BINDING_MODULE_OBSERVER:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingModuleObserverActivity.class));
                        break;
                    case BINDING_MAP:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingMapActivity.class));
                        break;
                    case BINDING_INCLUDE:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,BindingIncludeActivity.class));
                        break;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return menu.length;
        }

        @Override
        public Object getItem(int position) {
            return menu[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = new TextView(MainActivity.this);
            textView.setText(menu[position]);
            int padding = 30;
            textView.setPadding(padding,padding,padding,padding);

            return textView;
        }
    }
}
