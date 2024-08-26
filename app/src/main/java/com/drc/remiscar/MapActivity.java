package com.drc.remiscar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

public class MapActivity extends Activity implements PoiSearch.OnPoiSearchListener {
    MapView mMapView;
    AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker locationMarker;
    private LatLng currentLocation; // 当前定位坐标
    private String currentCity = ""; // 当前城市
    private String apiKey = "你的API_KEY"; // 替换为你的高德地图API Key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        mMapView = findViewById(R.id.mp_view);
        mMapView.onCreate(savedInstanceState); // 创建地图

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this)); // 设置自定义InfoWindow适配器
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.543099, 114.057868), 13));
        }

        checkPermissions(); // 检查并请求权限
        initLocation(); // 初始化定位
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        } else {
            initLocation();
        }
    }

    private void initLocation() {
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();

            // 设置定位模式为高精度模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true); // 设置是否只定位一次,默认为false

            // 设置定位监听
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation amapLocation) {
                    if (amapLocation != null) {
                        if (amapLocation.getErrorCode() == 0) {
                            // 定位成功回调信息，设置相关消息
                            double latitude = amapLocation.getLatitude();
                            double longitude = amapLocation.getLongitude();
                            currentLocation = new LatLng(latitude, longitude); // 保存当前定位信息
                            currentCity = amapLocation.getCity(); // 获取当前城市

                            // 移动地图到定位点
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                            // 添加定位标记
                            if (locationMarker != null) {
                                locationMarker.remove();
                            }
                            locationMarker = aMap.addMarker(new MarkerOptions().position(currentLocation).title("当前位置"));

                            // 开始POI搜索
                            fetchPOIs();
                        } else {
                            // 显示错误信息
                            Log.e("AmapError", "location Error, ErrCode:"
                                    + amapLocation.getErrorCode() + ", errInfo:"
                                    + amapLocation.getErrorInfo());
                        }
                    }
                }
            });

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 启动定位
            mLocationClient.startLocation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchPOIs() {
        String[] keywordsArray = {"医院", "卫健委", "急救中心", "120指挥中心", "AED"};
        for (String keyword : keywordsArray) {
            PoiSearch.Query query = new PoiSearch.Query(keyword, "", currentCity);
            query.setPageSize(50); // 设置每页最多返回多少条poiitem
            query.setPageNum(1); // 设置查第一页

            PoiSearch poiSearch = null;
            try {
                poiSearch = new PoiSearch(this, query);
            } catch (AMapException e) {
                throw new RuntimeException(e);
            }
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn(); // 异步搜索
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) { // 搜索poi的结果
                List<PoiItem> poiItems = result.getPois(); // 取得第一页的poiitem数据，页数从数字0开始
                if (poiItems != null && poiItems.size() > 0) {
                    for (PoiItem poiItem : poiItems) {
                        LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                        LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                        String name = poiItem.getTitle();
                        String address = poiItem.getSnippet();
                        aMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .snippet(address));
                    }
                } else {
                    Toast.makeText(this, "无结果", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "无结果", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "搜索失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // 此方法在单个POI搜索时使用，暂不处理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy(); // 销毁地图
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume(); // 重新绘制加载地图
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause(); // 暂停地图的绘制
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState); // 保存地图当前的状态
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                // 权限被拒绝，提示用户
                Log.e("Permission", "Location permission was denied.");
            }
        }
    }
}
