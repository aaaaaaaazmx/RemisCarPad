package com.drc.remiscar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

public class CustomInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private final View infoWindow;
    private final Activity context;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
        LayoutInflater inflater = context.getLayoutInflater();
        infoWindow = inflater.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
        String title = marker.getTitle();
        TextView titleUi = view.findViewById(R.id.title);
        if (title != null) {
            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }

        String snippet = marker.getSnippet();
        TextView snippetUi = view.findViewById(R.id.snippet);
        if (snippet != null) {
            snippetUi.setText(snippet);
        } else {
            snippetUi.setText("");
        }

        ImageButton button = view.findViewById(R.id.start_amap_app);
        button.setOnClickListener(v -> {
            // 弹出提示框
            showConfirmationDialog(marker.getTitle(), marker.getPosition());
        });
    }

    private void showConfirmationDialog(String locationName, LatLng latLng) {
        /*new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("您是否导航到 " + locationName + " ")
                .setPositiveButton("确定", (dialog, which) -> navigateTo(latLng))
                .setNegativeButton("取消", null)
                .show();*/

        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("您是否要立即取用AED/急救包？")
                .setPositiveButton("确定", (dialog, which) -> Toast.makeText(context, "您已成功取用AED/急救包", Toast.LENGTH_SHORT).show())
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateTo(LatLng latLng) {
        String uri = "amapuri://route/plan/?dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dname=" + "目标位置" + "&dev=0&t=0";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.autonavi.minimap");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "请安装高德地图应用", Toast.LENGTH_LONG).show();
        }
    }
}

