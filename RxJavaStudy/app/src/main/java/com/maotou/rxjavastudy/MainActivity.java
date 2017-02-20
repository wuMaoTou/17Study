package com.maotou.rxjavastudy;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maotou.rxjavastudy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    public String[] menu = new String[]{"RxJavaSample","CreateEventStream","Operator"};
    public static final int RXJAVASAMPLE = 0;
    public static final int CREATEEVENTSTREAM = 1;
    public static final int OPERATOR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.listview.setAdapter(new MyAdapter());
        mainBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case RXJAVASAMPLE:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,RxJavaSample.class));
                        break;
                    case CREATEEVENTSTREAM:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,CreateEventStreamList.class));
                        break;
                    case OPERATOR:
                        MainActivity.this.startActivity(new Intent(MainActivity.this,OperatorActivity.class));
                        break;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

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
