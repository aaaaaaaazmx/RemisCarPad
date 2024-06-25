package com.drc.remiscar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lombok.NonNull;

public class PatAdapter extends ArrayAdapter {
    private int resourceId=-1;
    public PatAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        patInfo pat = (patInfo) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView patName = view.findViewById(R.id.listPatName);
        TextView patSex = view.findViewById(R.id.listPatSex);
        TextView patAge = view.findViewById(R.id.listPatAge);
        TextView patPhone = view.findViewById(R.id.listPatPhone);
        TextView patAddress = view.findViewById(R.id.listPatAddress);
        TextView patGoWhere = view.findViewById(R.id.listPatGoWhere);
        TextView patGoWhereInt = view.findViewById(R.id.listPatGoWhereInt);
        TextView patDoState = view.findViewById(R.id.listPatDoState);

        patName.setText(pat.getPatName());
        patSex.setText(pat.getPatSex());
        patAge.setText(pat.getPatAge());
        patAddress.setText(pat.getPatAddress());
        patPhone.setText(pat.getPatContact());
        patGoWhere.setText(pat.getToWhere());
        patGoWhereInt.setText(pat.getPatGoWhereInt());
        patDoState.setText(pat.getPatHeal());

        return view;
    }
}
