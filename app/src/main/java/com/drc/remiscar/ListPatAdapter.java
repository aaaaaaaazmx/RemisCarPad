package com.drc.remiscar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListPatAdapter extends ArrayAdapter<patInfo> {
    private final int resourceId;
    public ListPatAdapter(Context context, int resource, List<patInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        patInfo pat=getItem(position); //获取当前项的Fruit实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewPat viewPat;
        if (convertView==null){

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewPat=new ViewPat();
            viewPat.txtPatAge= view.findViewById(R.id.listPatAge);
            viewPat.txtPatAddress= view.findViewById(R.id.listPatAddress);
            viewPat.txtPatName= view.findViewById(R.id.listPatName);
            viewPat.txtPatDoState= view.findViewById(R.id.listPatDoState);
            viewPat.txtPatGoWhere= view.findViewById(R.id.listPatGoWhere);
            viewPat.txtPatPhone= view.findViewById(R.id.listPatPhone);
            viewPat.txtPatSex = view.findViewById(R.id.listPatSex);
            viewPat.txtPatGoWhereInt = view.findViewById(R.id.listPatGoWhereInt);
            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewPat);
        } else{
            view=convertView;
            viewPat=(ViewPat) view.getTag();
        }

        // 获取控件实例，并调用set...方法使其显示出来
        viewPat.txtPatAge.setText(pat.getPatAge());
        viewPat.txtPatPhone.setText(pat.getPatContact());
        viewPat.txtPatGoWhere.setText(pat.getToWhere());
        viewPat.txtPatDoState.setText(pat.getPatHeal());
        viewPat.txtPatAddress.setText(pat.getPatAddress());
        viewPat.txtPatSex.setText(pat.getPatSex());
        viewPat.txtPatName.setText(pat.getPatName());
        viewPat.txtPatGoWhereInt.setText(pat.getPatGoWhereInt());
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewPat{
        private TextView txtPatName;
        private TextView txtPatPhone;

        private TextView txtPatAge;
        private TextView txtPatAddress;
        private TextView txtPatSex;
        private TextView txtPatGoWhere;
        private TextView txtPatGoWhereInt;
        private TextView txtPatDoState;
    }
}
