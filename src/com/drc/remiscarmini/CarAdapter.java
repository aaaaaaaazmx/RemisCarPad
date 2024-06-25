package com.drc.remiscarmini;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CarAdapter extends ArrayAdapter<Car> {
    private Context context;
    private int resourceId;
    private List<Car> items = null;

    public CarAdapter(@NonNull Context context, int resource, @NonNull List<Car> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.context = context;
        this.items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Car item = getItem(position); //获取当前项的Fruit实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewCar viewCar;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewCar = new ViewCar();
            viewCar.carNumber = (TextView)view.findViewById(R.id.carNumber);
            viewCar.carLic = (TextView)view.findViewById(R.id.carLic);
            view.setTag(viewCar);
        }
        else
        {
            view = convertView;
            viewCar = (ViewCar)view.getTag();
        }
        viewCar.carNumber.setText(item.Key);
        viewCar.carLic.setText(item.Value);
        return view;
    }

    class ViewCar {
        TextView carNumber;
        TextView carLic;
    }
}
