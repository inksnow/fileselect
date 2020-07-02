package com.inks.fileselect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.inks.inkslibrary.Utils.ClickUtil;
import com.inks.inkslibrary.Utils.L;
import com.inks.inkslibrary.Utils.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChooseFile {

    private LayoutInflater inflater;
    private PopupWindow pWindow;
    private Context context;
    private View contentView = null;
    private Window window;
    private TextView rootPath;
    private ImageView back;
    private ListView listView;
    private Button exit;
    private LinearLayout noFile;
    private ArrayList<String> pathString = new ArrayList<>();
    //选择完后回调是否关闭弹窗，如果不关闭，可以手动调用miss()关闭
    private Boolean chooseExit = true;

    private ResourceListAdapter resourceListAdapter;
    ArrayList<ResourceListBean> resourceList = new ArrayList<>();

    public interface onChooseFileBack {
        public void onChooseBack(String path, String type);
    }

    private  onChooseFileBack onChooseFileBack;

    public void setOnChooseFileBack(ChooseFile.onChooseFileBack onChooseFileBack) {
        this.onChooseFileBack = onChooseFileBack;
    }

    public void miss() {
        if (pWindow != null && pWindow.isShowing()) {
            pWindow.dismiss();
        }
    }

    public boolean isShowing() {
        if (pWindow != null && pWindow.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public void popupChoose(Context context, View v, Window window, LayoutInflater inflater,boolean chooseExit) {
        this.context = context;
        this.window = window;
        this.chooseExit = chooseExit;
        if (pWindow != null && pWindow.isShowing()) {
            miss();
        } else {
            contentView = inflater.inflate(R.layout.popup_choose_file, null);
            back = contentView.findViewById(R.id.back);
            rootPath = contentView.findViewById(R.id.rootPath);
            listView = contentView.findViewById(R.id.fileList);
            exit = contentView.findViewById(R.id.exit);
            noFile = contentView.findViewById(R.id.noFile);
            initData();
            back.setOnClickListener(click);
            exit.setOnClickListener(click);
            listView.setOnItemClickListener(listClick);
            pWindow = new PopupWindow(contentView, -1, -1);
            pWindow.setAnimationStyle(R.style.pop_animation);
            pWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            pWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            pWindow.setClippingEnabled(true);
            backgroundAlpha(0.5f);
            pWindow.showAtLocation(v, Gravity.TOP, 0, 0);
            pWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0f);
                }
            });
        }


    }

    @SuppressLint("SimpleDateFormat")
    private void initData() {
        pathString.clear();
        String nowPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        pathString.add(nowPath);
        rootPath.setText(nowPath);
        resourceList.clear();
        resourceListAdapter = new ResourceListAdapter(context, resourceList);
        listView.setAdapter(resourceListAdapter);
        upListAdapter();
    }

    private void upListAdapter() {
        resourceList.clear();

        int l = pathString.size();
        if (l < 1) {
            return;
        }
        rootPath.setText(pathString.get(l - 1));
        File directory = new File(pathString.get(l - 1));
        File[] files = directory.listFiles();
        if (files != null) {

            if(files.length<1){
                noFile.setVisibility(View.VISIBLE);
            }else{
                noFile.setVisibility(View.GONE);
            }
            ResourceListBean resourceListBean;
            for (File file2 : files) {
                resourceListBean = new ResourceListBean();
                resourceListBean.setPath(file2.getPath());
                Date time = new Date(file2.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                resourceListBean.setTime(formatter.format(time));

                if (file2.isDirectory()) {
                    File[] fileNumber = file2.listFiles();
                    if (fileNumber != null) {
                        resourceListBean.setFileNumber(fileNumber.length + "项");
                    } else {
                        resourceListBean.setFileNumber(0 + "项");
                    }
                } else {
                    resourceListBean.setFileNumber(pathGetType(file2.getPath()) + "文件");
                }

                resourceList.add(resourceListBean);
            }
        }
        resourceListAdapter.notifyDataSetChanged();
    }

   private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!ClickUtil.isFastDoubleClick((long) 100)) {
                int id = v.getId();
                if (id == R.id.back) {//上一目录
                    if (pathString.size() > 1) {
                        pathString.remove(pathString.size() - 1);
                    }
                    upListAdapter();
                } else if (id == R.id.exit) {
                    miss();
                }
            }
        }
    };



    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String clickPath = resourceList.get(position).getPath();
            File file = new File(clickPath);
            if (file.isDirectory()) {
                pathString.add(clickPath);
                rootPath.setText(clickPath);
                upListAdapter();
            } else {
                if(onChooseFileBack!=null){
                    if(chooseExit){
                        miss();
                    }
                    onChooseFileBack.onChooseBack(clickPath,pathGetType(resourceList.get(position).getPath()));
                }
            }


        }
    };


    private String pathGetType(String path) {
        String[] s = path.split("\\.");
        return s[s.length - 1];
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        window.setAttributes(lp);
    }


    public static Bitmap getLoacalBitmap(String url) {
        L.e("getLoacalBitmap:::" + url);
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
