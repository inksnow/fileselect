package com.inks.fileselect;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChooseFileMultiple {

    private LayoutInflater inflater;
    private PopupWindow pWindow;
    private Context context;
    private View contentView = null;
    private Window window;
    private TextView rootPath,yesText;
    private ImageView back;
    private ListView listView;
    private Button exit;
    private LinearLayout noFile;
    private ArrayList<String> pathString = new ArrayList<>();

    private ResourceListAdapter resourceListAdapter;
    private ArrayList<ResourceListBean> resourceList = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    //选择完后回调是否关闭弹窗，如果不关闭，可以手动调用miss()关闭
    private Boolean chooseExit = true;

    public interface onChooseFileBack {
        public void onChooseBack(ArrayList<String> paths);
    }

    private  onChooseFileBack onChooseFileBack;

    public void setOnChooseFileBack(onChooseFileBack onChooseFileBack) {
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

    public void popupChoose(Context context, View v, Window window, LayoutInflater inflater, boolean chooseExit) {
        this.context = context;
        this.window = window;
        this.chooseExit = chooseExit;
        if (pWindow != null && pWindow.isShowing()) {
            miss();
        } else {
            contentView = inflater.inflate(R.layout.popup_choose_file_multiple, null);
            back = contentView.findViewById(R.id.back);
            rootPath = contentView.findViewById(R.id.rootPath);
            listView = contentView.findViewById(R.id.fileList);
            exit = contentView.findViewById(R.id.exit);
            yesText = contentView.findViewById(R.id.file_yes);
            noFile = contentView.findViewById(R.id.noFile);
            initData();
            back.setOnClickListener(click);
            exit.setOnClickListener(click);
            yesText.setOnClickListener(click);
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
        paths.clear();
        pathString.clear();
        String nowPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        pathString.add(nowPath);
        rootPath.setText(nowPath);
        resourceList.clear();
        resourceListAdapter = new ResourceListAdapter(context, resourceList,true);
        listView.setAdapter(resourceListAdapter);
        upListAdapter();
    }

    private void upListAdapter() {
        resourceList.clear();
        paths.clear();
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
                resourceListBean.setSelect(false);
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
        yesText.setText("确认("+paths.size()+")");
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
                } else if (id == R.id.file_yes) {
                    if (paths.size() > 0) {
                        miss();
                        if (onChooseFileBack != null) {
                            onChooseFileBack.onChooseBack(paths);
                        }
                    } else {
                        T.showShort(context, "还未选择文件");
                    }
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
                if(resourceList.get(position).isSelect()){
                    paths.remove(resourceList.get(position).getPath());
                    resourceList.get(position).setSelect(false);
                    resourceListAdapter.notifyDataSetChanged();
                }else{
                    paths.add(resourceList.get(position).getPath());
                    resourceList.get(position).setSelect(true);
                    resourceListAdapter.notifyDataSetChanged();
                }

            }
            yesText.setText("确认("+paths.size()+")");
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




}
