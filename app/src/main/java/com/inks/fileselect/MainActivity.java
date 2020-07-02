package com.inks.fileselect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.inks.inkslibrary.Utils.ClickUtil;
import com.inks.inkslibrary.Utils.L;
import com.inks.inkslibrary.Utils.T;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ChooseFile chooseFile;
    private ChooseFileMultiple chooseFileMultiple;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseFile = new ChooseFile();
        chooseFile.setOnChooseFileBack(chooseFileBack);
        chooseFileMultiple = new ChooseFileMultiple();
        chooseFileMultiple.setOnChooseFileBack(chooseFileMultipleBack);


    }

    public void click(View view) {
        if (!ClickUtil.isFastDoubleClick((long) 100)) {
            int id = view.getId();
            if (id == R.id.check_one) {
                //单选
                chooseFile.popupChoose(MainActivity.this, view, getWindow(), getLayoutInflater(),true);
            } else if (id == R.id.check_multiple) {
                //多选

                chooseFileMultiple.popupChoose(MainActivity.this, view, getWindow(), getLayoutInflater(),true);
            }
        }
    }


    ChooseFile.onChooseFileBack chooseFileBack = new ChooseFile.onChooseFileBack() {
        @Override
        public void onChooseBack(String path, String type) {
            L.e("选择文件：" + path);
            T.showShort(MainActivity.this, "选择文件：" + path);

        }
    };

    ChooseFileMultiple.onChooseFileBack chooseFileMultipleBack = new ChooseFileMultiple.onChooseFileBack() {
        @Override
        public void onChooseBack(ArrayList<String> paths) {
            for (String path :paths) {
                L.e("选择文件：" + path);
            }

            T.showShort(MainActivity.this, "共选择"+paths.size()+"个文件" );
        }
    };


}
