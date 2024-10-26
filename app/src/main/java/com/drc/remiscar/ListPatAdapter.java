package com.drc.remiscar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListPatAdapter extends ArrayAdapter<patInfo> {
    private final int resourceId;
    private final Context context;

    public ListPatAdapter(Context context, int resource, List<patInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        patInfo pat = getItem(position); //获取当前项的Fruit实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewPat viewPat;
        if (convertView == null) {

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewPat = new ViewPat();
            viewPat.txtPatAge = view.findViewById(R.id.listPatAge);
            viewPat.txtPatAddress = view.findViewById(R.id.listPatAddress);
            viewPat.txtPatName = view.findViewById(R.id.listPatName);
            viewPat.txtPatDoState = view.findViewById(R.id.listPatDoState);
            viewPat.txtPatGoWhere = view.findViewById(R.id.listPatGoWhere);
            viewPat.txtPatPhone = view.findViewById(R.id.listPatPhone);
            viewPat.txtPatSex = view.findViewById(R.id.listPatSex);
            viewPat.txtPatGoWhereInt = view.findViewById(R.id.listPatGoWhereInt);
            viewPat.listItem = view.findViewById(R.id.listItem);
            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewPat);
        } else {
            view = convertView;
            viewPat = (ViewPat) view.getTag();
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
        viewPat.listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("是否删除此条病人信息");
                builder.setTitle("信息");
                builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        remove(pat);
                    }
                });
                builder.create().show();
                return true;
            }
        });
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewPat {
        private TextView txtPatName;
        private TextView txtPatPhone;

        private TextView txtPatAge;
        private TextView txtPatAddress;
        private TextView txtPatSex;
        private TextView txtPatGoWhere;
        private TextView txtPatGoWhereInt;
        private TextView txtPatDoState;
        private LinearLayout listItem;
    }
}
