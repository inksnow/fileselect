package com.inks.fileselect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class ResourceListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<ResourceListBean> resourceList  = new ArrayList<>();
    private int choosePosition = 0;

    private boolean showCheckBox = false;

    public ResourceListAdapter(Context context, ArrayList<ResourceListBean> resourceList) {
        this(context,resourceList,false);
    }

    public ResourceListAdapter(Context context, ArrayList<ResourceListBean> resourceList, boolean showCheckBox) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.resourceList=resourceList;
        this.showCheckBox = showCheckBox;
    }
    public void upData(ArrayList<ResourceListBean> resourceList){
        this.resourceList=resourceList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return resourceList.size();
    }

    @Override
    public Object getItem(int position) {
        return resourceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setChoosePosition(int position){
        this.choosePosition = position;
        notifyDataSetChanged();
    }

    public int getChoosePosition(){
        return choosePosition;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_resource, parent, false); //加载布局
            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.CheckBox);
            holder.path = convertView.findViewById(R.id.path);
            holder.time = convertView.findViewById(R.id.time);
            holder.image =  convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(resourceList.get(position).isSelect()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        File file = new File(resourceList.get(position).getPath());
        if(file.isDirectory()){
            holder.image.setImageResource(R.drawable.file);
            holder.checkBox.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.VISIBLE);
            if(pathGetType(resourceList.get(position).getPath()).equals("zip")){
                holder.image.setImageResource(R.drawable.zip);

            }else if(pathGetType(resourceList.get(position).getPath()).equalsIgnoreCase("jpg")||
                    pathGetType(resourceList.get(position).getPath()).equalsIgnoreCase("png")||
                    pathGetType(resourceList.get(position).getPath()).equalsIgnoreCase("gif")||
                    pathGetType(resourceList.get(position).getPath()).equalsIgnoreCase("jpeg")){
                //holder.image.setImageResource(R.drawable.image_file);
                Glide.with(context)
                        .load(resourceList.get(position).getPath())
                        .error(R.drawable.image)
                        .into(holder.image);
            } else if(pathGetType(resourceList.get(position).getPath()).equalsIgnoreCase("apk")){
                holder.image.setImageResource(R.drawable.apk);
            } else{
                holder.image.setImageResource(R.drawable.unknown);
            }
        }

        holder.path.setText(pathGetName(resourceList.get(position).getPath()));
        holder.time.setText(resourceList.get(position).getTime()+ "   "+resourceList.get(position).getFileNumber());

        if(!showCheckBox){
            holder.checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView path;
        TextView time;
        ImageView image;
        CheckBox checkBox;
    }

    private String pathGetName(String path){
        String[] s = path.split("/");
        return s[s.length - 1];
    }

    private String pathGetType(String path){
        String[] s = path.split("\\.");
        return s[s.length - 1];
    }


}
