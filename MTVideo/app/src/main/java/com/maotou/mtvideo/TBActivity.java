package com.maotou.mtvideo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TBActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView recyclerView;
    private String[] arr = new String[]{
            "adidas官方旗舰店】￥LsPMYJGP3ri￥",
            "【dyson戴森官方旗舰店】￥jwcaYJGsxHQ￥",
            "【Estee Lauder雅诗兰黛官方旗舰店】￥3QqBYJuoc3A￥",
            "【GIORGIO ARMANI阿玛尼美妆官方旗舰店】￥G5QmYJuniCy￥",
            "【GREE格力官方旗舰店】￥GMuKYJGuobb￥",
            "【HomeFacialPro旗舰店】￥M9dPYJGwByN￥",
            "【HR赫莲娜官方旗舰店】￥YncWYJGDygD￥",
            "【KIEHL'S科颜氏官方旗舰店】￥EWFqYJGBRcv￥",
            "【Lancome兰蔻官方旗舰店】￥TRb9YJGzy94￥",
            "【olay官方旗舰店】￥WCG5YJuYBCK￥",
            "【OPPO官方旗舰店】￥tKxbYJuXZLU￥",
            "【SK-II官方旗舰店】￥PV5XYJu2lxh￥",
            "【vivo官方旗舰店】￥6vPSYJudrvL￥",
            "【YSL圣罗兰美妆官方旗舰店】￥koVYYJudDMD￥",
            "【ZARA官方旗舰店】￥x1JbYJu3gKr￥",
            "【华为官方旗舰店】￥dunVYJueXee￥",
            "【奥克斯旗舰店】￥uSWwYJuVVva￥",
            "【宝洁官方旗舰店】￥QGYUYJufVvF￥",
            "【小米官方旗舰店】￥yZyAYJuUdYm￥",
            "【李宁官方网店】￥R6PhYJu5b0v￥",
            "【欧莱雅官方旗舰店】￥mqsmYJu5L5f￥",
            "【波司登官方旗舰店】￥1VE9YrYFPmS￥",
            "【海尔官方旗舰店】￥ASx9YJuTf6w￥",
            "【百雀羚旗舰店】￥f5HaYJuTgvd￥",
            "【美特斯邦威官方网店】￥H564YJuhigt￥",
            "【美的官方旗舰店】￥mgVbYJuS6xa￥",
            "【自然堂旗舰店】￥iGBRYJu7TtX￥",
            "【苏泊尔官方旗舰店】￥uDMuYJui7uz￥",
            "【荣耀官方旗舰店】￥2WB1YJuOALq￥",
            "【蒙牛旗舰店】￥2qfdYJuNnzY￥",
    };
    private ClipboardManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tb_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new ArrayAdapter<String>(this, R.layout.item_text, arr));
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cm == null) {
                    cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                }
                ClipData clip = ClipData.newPlainText("text", arr[position]);
                cm.setPrimaryClip(clip);
                if (isAppInstalled(TBActivity.this, "com.taobao.taobao")) {
                    Intent settintIntent = getPackageManager().
                            getLaunchIntentForPackage("com.taobao.taobao");
                    startActivity(settintIntent);
                } else {
                    Toast.makeText(TBActivity.this, "未安装淘宝", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

}
