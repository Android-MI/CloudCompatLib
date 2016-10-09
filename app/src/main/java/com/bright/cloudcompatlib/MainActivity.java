package com.bright.cloudcompatlib;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bright.cloudcompatlib.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void resetLayout() {
        Button btnDialog = (Button) findViewById(R.id.btn_simple_dialog);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialog();
            }
        });

    }

    public void SimpleDialog() {
//        showSimpleDialog(this, "标题", "MD风格的对话框显示", "确定", "取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                callTelPhone("1231312312");
//            }
//        }, null, false);
        telDialog("call tel ", "13432233222");
    }
}
