package com.drc.remiscarmini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSON;


public class DetailActivity extends Activity {

    TextView labTaskNum = null;
    TextView labPatName = null;
    TextView labPatSex = null;
    TextView labPatAge = null;
    TextView labAlterTelNo = null;
    TextView labAddress = null;
    TextView labWaitAddress = null;
    TextView labCase = null;
    TextView labContact = null;
    TextView labAlterTime = null;
    TextView labyscd = null;
    TextView labzs = null;
    Button btnPos = null;
    Button btnSet = null;
    Button btnRefresh = null;
    Button btnCall1 = null;
    Button btnCall2 = null;
    private Dialog dialogSet;
    private Dialog dialogPad;
    private EditText txtPadNo;
    private Dialog dialogServerIP;
    private EditText txtServerIP;
    private Dialog dialogServerPort;
    private EditText txtServerPort;

    private Dialog dialog120Number;
    private EditText txt120Number;

    private Dialog dialog121Number;
    private EditText txt121Number;

    private TabHost tabhost;
    private TabWidget tabs;

    String CITY = "邵阳";
    double _Lng = 0;
    double _Lat = 0;
    String Lng = "";
    String Lat = "";
    public final static String _DefaultServerIP = "220.170.194.5";
    public final static String _DefaultServerPort = "27866";
    public final static String _DefaultPadNo = "18000008201";
    public final static String _gdGeo = "http://restapi.amap.com/v3/geocode/geo?key=9df43fa849aa738ef7e6b34f0295eeed&address=%s&city=%s";
    public final static String _BASE_URL = "http://%s:%s/RemisCarService.aspx?Token=Oif1ko0ZKC4IhoC3zplGRYQx57q8Kx9T&PadNo=%s";
    public final static String _GetCarList = "http://%s:%s/GetCarList.aspx?Token=Oif1ko0ZKC4IhoC3zplGRYQx57q8Kx9T";
    public final static String _ReviceOK_URL = "http://%s:%s/ReviceOK.aspx?token=Oif1ko0ZKC4IhoC3zplGRYQx57q8Kx9T&padNo=%s";
    public static String _Url = "";
    public String _ServerIP = "";
    public String _ServerPort = "";
    public String _PadNo = "";
    public String _CarLic = "";
    public String _120Number = "";
    public String _allCheckNumber = "";

    private LayoutInflater inflater;
    Handler handRefresh = null;

    CarAdapter carAdapter = null;
    ListView listCars = null;
    List<Car> cars = null;
    TextView txtCarLic = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Intent par = getIntent();
        // patNum = par.getStringExtra("PatNum");
        inflater = LayoutInflater.from(this);
        GetControl();
        InitControl();
        InitSettingDialog();
        GetServerIP();
        GetServerPort();
        getCarList();
        GetPadNo();
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
        handRefresh = new Handler();
        PostRefresh();
//		IntentFilter filter = new IntentFilter("finish");
//        registerReceiver(mFinishReceiver, filter);

        Intent intentService = new Intent(DetailActivity.this, NotificationService.class);
        startService(intentService);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());

            if (!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    /**
     * 鐩戝惉activity鐨勭粨鏉�
     */
    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //寮�鍚竴涓彂閫乶otification鐨剆ervice
            Intent intentService = new Intent(DetailActivity.this, NotificationService.class);
            startService(intentService);
            //涓�瀹氳娉ㄩ攢骞挎挱
            unregisterReceiver(mFinishReceiver);
        }
    };

    private void PostRefresh() {
        try {
            handRefresh.post(new Runnable() {

                @Override
                public void run() {

                    getTaskInfo();
                }
            });
        } catch (Exception e) {
        }
    }

    private void GetServerIP() {
        String serverIP = null;
        try {
            serverIP = readFile("ServerIP.txt");
        } catch (Exception ex) {
            dialogServerIP.show();
        }
        if (serverIP == null || serverIP.equals("")) {
            serverIP = _DefaultServerIP;
        }
        _ServerIP = serverIP;
        txtServerIP.setText(serverIP);

    }

    private void GetServerPort() {
        String serverPort = null;
        try {
            serverPort = readFile("ServerPort.txt");
        } catch (Exception ex) {
            dialogServerPort.show();
        }
        if (serverPort == null || serverPort.equals("")) {
            serverPort = _DefaultServerPort;
        }
        _ServerPort = serverPort;
        txtServerPort.setText(serverPort);

    }

    private void GetPadNo() {
        String phone = null;
        try {
            phone = readFile("padNo.txt");
        } catch (Exception ex) {
            dialogPad.show();
        }
        if (phone == null || phone.equals("")) {
            phone = _DefaultPadNo;
        }
        _PadNo = phone;
        txtPadNo.setText(phone);

    }

    private void writeServerIP() {
        _ServerIP = txtServerIP.getText().toString();
        writeFile("ServerIP.txt", _ServerIP);
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
    }

    private void writeServerPort() {
        _ServerPort = txtServerPort.getText().toString();
        writeFile("ServerPort.txt", _ServerPort);
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
    }

    private void writePadNo() {
//        _PadNo = txtPadNo.getText().toString();
        writeFile("padNo.txt", _PadNo);
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
    }

    private void InitSettingDialog() {
        txtServerIP = new EditText(this);
        dialogServerIP = new AlertDialog.Builder(this).setTitle("服务地址").setIcon(android.R.drawable.ic_dialog_info)
                .setView(txtServerIP).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeServerIP();
                    }
                }).setNegativeButton("取消", null).create();

        txtServerPort = new EditText(this);
        txtServerPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogServerPort = new AlertDialog.Builder(this).setTitle("服务端口").setIcon(android.R.drawable.ic_dialog_info)
                .setView(txtServerPort).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeServerPort();
                    }
                }).setNegativeButton("取消", null).create();

        txtPadNo = new EditText(this);
        txtPadNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        txtPadNo.setInputType(InputType.TYPE_CLASS_PHONE);
        LayoutInflater inflater = this.getLayoutInflater();
        View customView = inflater.inflate(R.layout.car_list, null, false);
        dialogPad = new AlertDialog.Builder(this).setView(customView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogPad.cancel();
            }
        }).create();
        listCars = (ListView) customView.findViewById(R.id.listCars);
        txtCarLic = (TextView) customView.findViewById(R.id.txtCarLic);
        listCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                _PadNo = cars.get(i).Key;
                _CarLic = cars.get(i).getValue();
                txtCarLic.setText(_CarLic);
                writePadNo();
                //dialogPad.cancel();
            }
        });

        dialogSet = new AlertDialog.Builder(this).setTitle("系统设置").setItems(
                new String[]{"服务地址", "服务端口", "救护车"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dialogServerIP.show();
                                GetServerIP();
                                break;
                            case 1:
                                dialogServerPort.show();
                                GetServerPort();
                                break;
                            case 2:
                                //dialogPad.show();

                                dialogPad.show();
                                GetPadNo();
                                break;
                        }
                    }
                }).create();
    }

    private void InitControl() {
        labTaskNum.setText("");
        labPatName.setText("");
        labPatSex.setText("");
        labPatAge.setText("");
        labAlterTelNo.setText("");
        labAddress.setText("");
        labWaitAddress.setText("");
        labCase.setText("");
        labContact.setText("");
        labAlterTime.setText("");
        labzs.setText("");
        labyscd.setText("");
        btnRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                PostRefresh();
            }

        });
        btnSet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialogSet.show();
            }

        });


    }

    public void startBaiduMap() {
        if (isAvilible(getBaseContext(), "com.baidu.BaiduMap")) {
            Intent i1 = new Intent();
// 驾车路线规划
            i1.setData(Uri.parse("baidumap://map/navi?" + (_Lat != 0 ? ("location=" + _Lat + "," + _Lng) : "query=" + labWaitAddress.getText().toString()) + "&coord_type=bd09ll&src=andr.baidu.openAPIdemo"));
            startActivity(i1);
        } else {
            alert("未安装百度地图APP");
        }
    }

    // 楂樺痉鍦板浘,璧风偣灏辨槸瀹氫綅鐐�

    public void startNaviGao() {
        if (isAvilible(getBaseContext(), "com.autonavi.minimap")) {
            try {
                String url = "";
                if (_Lng != 0 && _Lat != 0)
                    url = "androidamap://route?sourceApplication=softname&sname=我的位置&dlat=" + _Lat + "&dlon=" + _Lng + "&dname="
                            + labWaitAddress.getText().toString() + "&dev=0&m=0&t=0";
                else
                    url = "androidamap://route?sourceApplication=softname&sname=我的位置&dname="
                            + labWaitAddress.getText().toString() + "&dev=0&m=0&t=0";
                Intent intent = Intent.getIntent(url);
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            alert("未安装高德地图APP");
        }
    }

    private void openBaiduMap(double lon, double lat, String title, String describle) {
        try {
            StringBuilder loc = new StringBuilder();
            loc.append("intent://map/direction?origin=latlng:");
            loc.append(lat);
            loc.append(",");
            loc.append(lon);
            loc.append("|name:");
            loc.append("我的位置");
            loc.append("&destination=latlng:");
            loc.append(lat);
            loc.append(",");
            loc.append(lon);
            loc.append("|name:");
            loc.append(describle);
            loc.append("&mode=driving");
            loc.append("&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            Intent intent = Intent.getIntent(loc.toString());
            if (isAvilible(this, "com.baidu.BaiduMap")) {
                startActivity(intent); //启动调用
                Log.e("GasStation", "百度地图客户端已经安装");
            } else {
                Log.e("GasStation", "没有安装百度地图客户端");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 楠岃瘉鍚勭瀵艰埅鍦板浘鏄惁瀹夎
    public static boolean isAvilible(Context context, String packageName) {
        // 鑾峰彇packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 鑾峰彇鎵�鏈夊凡瀹夎绋嬪簭鐨勫寘淇℃伅
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 鐢ㄤ簬瀛樺偍鎵�鏈夊凡瀹夎绋嬪簭鐨勫寘鍚�
        List<String> packageNames = new ArrayList<String>();
        // 浠巔info涓皢鍖呭悕瀛楅�愪竴鍙栧嚭锛屽帇鍏Name list涓�
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 鍒ゆ柇packageNames涓槸鍚︽湁鐩爣绋嬪簭鐨勫寘鍚嶏紝鏈塗RUE锛屾病鏈塅ALSE
        return packageNames.contains(packageName);
    }

    private void GetControl() {
        labTaskNum = (TextView) findViewById(R.id.labTaskNum);
        labPatName = (TextView) findViewById(R.id.labPatName);
        labPatSex = (TextView) findViewById(R.id.labPatSex);
        labPatAge = (TextView) findViewById(R.id.labPatAge);
        labAlterTelNo = (TextView) findViewById(R.id.labAlterTelNo);
        labAddress = (TextView) findViewById(R.id.labAddress);
        labWaitAddress = (TextView) findViewById(R.id.labWaitAddress);
        labCase = (TextView) findViewById(R.id.labCase);
        labContact = (TextView) findViewById(R.id.labContact);
        labAlterTime = (TextView) findViewById(R.id.labAlterTime);
        labzs = (TextView) findViewById(R.id.labzs);
        labyscd = (TextView) findViewById(R.id.labyscd);
        btnPos = (Button) findViewById(R.id.btnPos);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnSet = (Button) findViewById(R.id.btnSet);
        btnCall1 = (Button) findViewById(R.id.btnCall1);
        btnCall2 = (Button) findViewById(R.id.btnCall2);
    }

    private class loadJSONStringTask extends AsyncTask<String, Integer, String> {
        int method = -1;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (params[1].equals("getTaskInfo")) {
                method = 0;
            } else if (params[1].equals("getAddressLocation")) {
                method = 1;
            } else if (params[1].equals("reviceOk")) {
                method = 2;
            } else if (params[1].equals("getCarList")) {
                method = 3;
            }

            return WebProxy.getString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            switch (method) {
                case 0:
                    getTaskInfoCallback(result);
                    break;
                case 1:
                    getAddressLocationCallback(result);
                    break;
                case 3:
                    getCarListCallback(result);
                    break;
            }
        }
    }

    private void getCarListCallback(String content) {
        try {
            if (content != null && !content.equals("null") && !content.equals("")) {
                cars = JSON.parseArray(content, Car.class);
                carAdapter = new CarAdapter(this, R.layout.car_item, cars);
                listCars.setAdapter(carAdapter);
                if (_PadNo != "") {
                    Optional<Car> car = cars.stream().filter(o -> o.Key.equals(_PadNo)).findFirst();
                    if (car.isPresent()) {
                        txtCarLic.setText(car.get().getValue());
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void getCarList() {
        try {
            String url = String.format(_GetCarList, _ServerIP, _ServerPort);
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "getCarList");
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }


    private void getTaskInfo() {
        try {
            String url = _Url;
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "getTaskInfo");
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    @SuppressWarnings("deprecation")
    private void getAddressLocation(String address, String city) {
        loadJSONStringTask task = new loadJSONStringTask();
        task.execute(String.format(_gdGeo, URLEncoder.encode(address), URLEncoder.encode(city)), "getAddressLocation");
    }

    private void getAddressLocationCallback(String location) {
        try {
            if (location != null && !location.equals("null")) {

                JSONObject json = new JSONObject(location);
                if (json.getString("status").equals("1")) {
                    JSONArray locations = json.getJSONArray("geocodes");
                    if (locations.length() > 0) {
                        String loc = locations.getJSONObject(0).getString("location");
                        String[] pos = loc.split(",");
                        if (pos.length > 0) {
                            Lng = pos[0];
                            Lat = pos[1];

                            btnPos.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    // TODO Auto-generated method stub
                                    //startNaviGao();
                                    startBaiduMap();
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    private void getTaskInfoCallback(String content) {
        try {
            if (content != null && !content.equals("null")) {

                JSONObject json = new JSONObject(content);
                if (json.length() > 0) {
                    loadFromJson(json.getJSONObject("0"));
                } else {
                    alert("无最新任务");
                }

            } else {
                // alert("未锟揭碉拷锟斤拷锟斤拷!");
                // Toast.makeText(getApplicationContext(), "未锟揭碉拷锟斤拷锟斤拷",
                // Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    private void loadFromJson(JSONObject json) {
        if (json != null) {
            try {
                _Lng = 0;
                _Lat = 0;
                labTaskNum.setText(GetNoNullString(json.getString("taskNum")));
                labPatName.setText(GetNoNullString(json.getString("PatName")));
                labPatSex.setText(GetNoNullString(json.getString("PatSex")));
                String age = GetNoNullString(json.getString("PatAge"));
                labPatAge.setText("0".equals(age) ? "" : age);
                final String AlterTelNo = GetNoNullString(json.getString("AlertTelephone"));
                labAlterTelNo.setText(AlterTelNo);
                if (!AlterTelNo.equals("")) {
                    btnCall1.setVisibility(View.VISIBLE);
                    btnCall1.setOnClickListener(new OnClickListener() {

                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AlterTelNo));
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);
                        }

                    });
                }
                String SceneAddress = GetNoNullString(json.getString("SceneAddress"));
                labAddress.setText(GetNoNullString(json.getString("Address")));
                final String lat = GetNoNullString(json.getString("Latitude"));
                final String lng = GetNoNullString(json.getString("Longitude"));
                if (!lat.isEmpty())
                    _Lat = Double.valueOf(lat);
                if (!lng.isEmpty()) {
                    _Lng = Double.valueOf(lng);
                    if (_Lng != 0)
                        labWaitAddress.setTextColor(Color.RED);
                }
                labWaitAddress.setText(SceneAddress);
                if (!SceneAddress.equals("")) {
                    btnPos.setVisibility(View.VISIBLE);
                    btnPos.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            //startNaviGao();
                            startBaiduMap();
                        }
                    });
                }
                // getAddressLocation(labWaitAddress.getText().toString(),
                // CITY);
                labCase.setText(GetNoNullString(json.getString("CaseType")));
                final String callNo = GetNoNullString(json.getString("ContactNumber"));
                labContact.setText(callNo);
                if (!callNo.equals("")) {
                    btnCall2.setVisibility(View.VISIBLE);
                    btnCall2.setOnClickListener(new OnClickListener() {

                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNo));
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);
                        }

                    });
                }
                labAlterTime.setText(GetNoNullString(json.getString("AlertTime")));
                labyscd.setText(GetNoNullString(json.getString("YSQX")));
                labzs.setText(GetNoNullString(json.getString("Complained")));

                // labPatNurse
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private String GetNoNullString(String value) {
        if (value == "null") {
            value = "";
        }
        return value;
    }

    public void alert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setMessage(msg);
        builder.setTitle("信息");
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private String readFile(final String fileName) {
        String content = "";
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(openFileInput(fileName), "UTF-8"));
            content = r.readLine();
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    private void writeFile(final String fileName, final String content) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(openFileOutput(fileName, this.MODE_PRIVATE), "UTF-8"));
            pw.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    private void showTips() {
        AlertDialog alertDialog = new AlertDialog.Builder(DetailActivity.this).setTitle("退出程序确认").setMessage("确定要退出程序吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DetailActivity.this.finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // 鍒涘缓瀵硅瘽妗�
        alertDialog.show(); // 鏄剧ず瀵硅瘽妗�
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.showTips();
            return false;
        }
        // TODO Auto-generated method stub
        if (KeyEvent.KEYCODE_HOME == keyCode && event.getRepeatCount() == 0) {
            this.showTips();
            // android.os.Process.killProcess(android.os.Process.myPid());
        }
        if (KeyEvent.KEYCODE_MENU == keyCode && event.getRepeatCount() == 0) {
            this.showTips();
            // android.os.Process.killProcess(android.os.Process.myPid());
        }

        return false;
    }

    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
        // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
