package com.drc.remiscar;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.P;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.MapView;
import com.drc.remiscar.util.ToastUtil;
import com.drc.remiscar.util.VersionUtil;
import com.drc.remiscar.widget.AvatarFloatView;
import com.drc.remiscar.widget.BaseFloatView;
import com.drc.remiscar.widget.FloatManager;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.FlacSeekTableSeekMap;
import com.google.android.exoplayer2.ui.PlayerView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends BaseActivity {

    private static final Logger log = LoggerFactory.getLogger(DetailActivity.class);
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
    Button btnMap = null;
    Button btnAed = null;
    Button btnCall1 = null;
    Button btnCall2 = null;
    Button btnGEO1 = null;
    Button btnGEO2 = null;
    Button btnOkRevice = null;
    Button btnTaskOver = null;
    private Dialog dialogSet;
    private Dialog dialogPad;
    private Dialog dialogLog;
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

    //_carOutTime
    private String txtCarOutDate;

    String CITY = "怀化";
    String Lng = "";
    String Lat = "";
    String _taskId = "";
    String _serviceName = "gskj-cloud120";
    public final static String _DefaultServerIP = "220.202.98.224";
    public final static String _DefaultServerPort = "8001";
    public final static String _DefaultPadNo = "786";
    public final static String _gdGeo = "http://restapi.amap.com/v3/geocode/geo?key=9df43fa849aa738ef7e6b34f0295eeed&address=%s&city=%s";
    public final static String _BASE_URL = "http://%s:%s/gskj-cloud120/api/RemisCarPad/Get/%s";
    public final static String _ReviceOK_URL = "http://%s:%s/gskj-cloud120/api/RemisCarPad/ReviceOK/%s";
    public final static String _GetTaskAndPat_URL = "http://%s:%s/gskj-cloud120/api/RemisCarPad/GetTaskInfo/%s";
    public final static String _GetHospital_URL = "http://%s:%s/gskj-cloud120/api/RemisCarPad/GetHospital";
    public final static String _GetEnum_URL = "http://%s:%s/gskj-cloud120/api/RemisCarPad/GetEnum/%s";
    public final static String _SaveTaskInfo = "http://%s:%s/gskj-cloud120/api/RemisCarPad/SaveTaskInfo";
    public final static String _GetNewVersion = "http://%s:%s/gskj-cloud120/api/RemisCarPad/GetNewVersion/%s/%s";
    public final static String _TaskOver = "http://%s:%s/gskj-cloud120/api/RemisCarPad/TaskOver";
    public final static String _Comeback = "http://%s:%s/gskj-cloud120/api/RemisCarPad/Comeback/%s";
    public final static String _ArrivedScene = "http://%s:%s/gskj-cloud120/api/RemisCarPad/ArrivedScene/%s";
    public final static String _OutCar = "http://%s:%s/gskj-cloud120/api/RemisCarPad/OutCar/%s";
    // 送大外院时间 /api/RemisCarPad/OutHosp/{id}
    public final static String _OutHosp = "http://%s:%s/gskj-cloud120/api/RemisCarPad/OutHosp/%s";
    public final static String _ENUM_EMPTY_REASON = "340";
    public final static String _ENUM_EMPTY_TOWHERE = "397";
    public static String _Url = "";
    public static String _ServerIP = "";
    public static String _ServerPort = "";
    public static String _PadNo = "";
    public String _120Number = "";
    public String _allCheckNumber = "";
    public String _alterNumber;
    public String _subConfirmTime;

    private double _Lng = 0;
    private double _Lat = 0;

    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat datetimeFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private LayoutInflater inflater;
    Handler handRefresh = null;
    String _version;
    String _DefaultVersion = "1.0.0";
    MultiAutoCompleteTextView txtLog;
    private final String _msgGEO = "您好：为了更快速的为您调派救护车前往您当前所在位置，请点击此链接https://www.zhdrs.com/#/?token=%s&mobile=%s确认位置信息，5分钟之内有效，祁阳市120指挥中心。";

    private final String userName = "geo120";
    private final String passWord = "Remis.120";
    private final String virtualHost = "/";
    private final String hostName = "47.94.152.74";
    private final int portNum = 5672;
    private final String queueName = "";
    private final String exchangeName = "demo";
    private final String rountingKey = "key";

    private final String _getGEOUrl = "https://www.zhdrs.com:50008/SendGEO?mobile=%s";
    GeoThread _geoThread = null;
    private static final int REQ_PERM_STORAGE = 10001;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10002;
    private AvatarFloatView mFloatView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        extracted();
    }

    private void initView() {
        FloatManager.hide();
        // 初始化悬浮按钮
        mFloatView = new AvatarFloatView(this);
        mFloatView.setDragDistance(0.3);
        mFloatView.setAdsorbType(BaseFloatView.ADSORB_HORIZONTAL);
        // mFloatView.setAdsorbType(BaseFloatView.ADSORB_VERTICAL);
        FloatManager.with(this).add(mFloatView)
                .setClick(new BaseFloatView.OnFloatClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:120"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private NotificationService notificationService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
            notificationService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private static final int REQUEST_PERMISSIONSS = 1111;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    private void permiss() {
        AMapLocationClient.updatePrivacyShow(getApplicationContext(),true,true);
        AMapLocationClient.updatePrivacyAgree(getApplicationContext(),true);
        // 检查定位权限
        // 检查以下这些权限
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONSS);
        } else {
            startActivity(new Intent(DetailActivity.this, MapActivity.class));
        }
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void extracted() {
        if (CheckPermissionUstils.checkReadPermission(this,
                new String[]{Manifest.permission.INTERNET, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.READ_PHONE_STATE, Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED}
                , CheckPermissionUstils.REQUEST_WRITE_PERMISSION)) {
            //ReadVersion();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }

            GetControl();
            InitControl();
            InitSettingDialog();
            GetServerIP();
            GetServerPort();

            inflater = LayoutInflater.from(this);

            GetPadNo();
            _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
            handRefresh = new Handler();

            // 增加通知权限的判断
            if (!areNotificationsEnabled(this)) showNotificationSettingsDialog();

            checkVersion();

            // 发送定时任务
            handRefresh.post(postRefreshRunnable);

            String url = DetailActivity._Url;
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "getTaskInfo");

            Intent intentService = new Intent(DetailActivity.this, NotificationService.class);
            startService(intentService);
            bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);

            if (Build.VERSION.SDK_INT >= M) {
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());

                if (!hasIgnored) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        }
    }

    private void showNotificationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("通知权限");
        builder.setMessage("您尚未开启通知权限，请开启通知权限，方便接收任务信息。");
        builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openNotificationSettings();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());

            intent.putExtra("app_uid", getApplicationInfo().uid);
        }
        startActivity(intent);
    }


    public boolean areNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkVersion() {
        if (!checkReadPermission()) {
            return;
        }
        new UpdateManager(this, UpdateManager.CHECK_USER).checkUpdate();
    }

    private boolean checkReadPermission() {
        if (ActivityCompat.checkSelfPermission(this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.note_permission_read, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERM_STORAGE);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERM_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, R.string.note_permission_read, Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/remis/remis.log";
                txtLog.setText(readFileLog(path));
                break;
            case CheckPermissionUstils.REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被授予
                    // Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    // 这里你可以继续执行需要权限的操作
                    extracted();
                } else {
                    // 权限被拒绝
                    Toast.makeText(this, "需要开启相对应的权限", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_PERMISSIONSS:
                startActivity(new Intent(DetailActivity.this, MapActivity.class));
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void ReadVersion() {
        String version = null;
        try {
            version = readFile("Version.txt");
        } catch (Exception ex) {
            //dialogServerIP.show();
        }
        if (version == null || version.equals("")) {
            version = _DefaultVersion;
        }
        _version = version;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void WriteVersion() {
        if (_version != null && !_version.isEmpty()) writeFile("Version.txt", _version);
    }


    private final BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Intent intentService = new Intent(DetailActivity.this, NotificationService.class);
            startService(intentService);
            bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeServerIP() {
        _ServerIP = txtServerIP.getText().toString();
        writeFile("ServerIP.txt", _ServerIP);
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeServerPort() {
        _ServerPort = txtServerPort.getText().toString();
        writeFile("ServerPort.txt", _ServerPort);
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writePadNo() {
        _PadNo = txtPadNo.getText().toString();
        writeFile("padNo.txt", _PadNo);
        _Url = String.format(_BASE_URL, _ServerIP, _ServerPort, _PadNo);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void InitSettingDialog() {
        txtServerIP = new EditText(this);
        dialogServerIP = new AlertDialog.Builder(this).setTitle("服务地址").setIcon(android.R.drawable.ic_dialog_info).setView(txtServerIP).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeServerIP();
            }
        }).setNegativeButton("取消", null).create();

        txtServerPort = new EditText(this);
        txtServerPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogServerPort = new AlertDialog.Builder(this).setTitle("服务端口").setIcon(android.R.drawable.ic_dialog_info).setView(txtServerPort).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeServerPort();
            }
        }).setNegativeButton("取消", null).create();

        txtPadNo = new EditText(this);
        txtPadNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        txtPadNo.setInputType(InputType.TYPE_CLASS_PHONE);
        dialogPad = new AlertDialog.Builder(this).setTitle("设备ID号").setIcon(android.R.drawable.ic_dialog_info).setView(txtPadNo).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writePadNo();
            }
        }).setNegativeButton("取消", null).create();

        txtLog = new MultiAutoCompleteTextView(this);
        txtLog.setLines(6);
        txtLog.setWidth(500);
        txtLog.isScrollbarFadingEnabled();
        txtLog.setEnabled(false);
        dialogLog = new AlertDialog.Builder(this).setTitle("查看日志").setView(txtLog).setPositiveButton("确定", null).create();
        dialogSet = new AlertDialog.Builder(this).setTitle("系统设置").setItems(new String[]{"服务地址", "服务端口", "设备ID号", "关于", "查看日志"}, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                        dialogPad.show();
                        GetPadNo();
                        break;
                    case 3:
                        alert("当前版本号：" + VersionUtil.getVersionName(DetailActivity.this));
                        break;
                    case 4:
                        readLog();
                        dialogLog.show();
                        break;
                }
            }
        }).create();
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void readLog() {
        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

    }

    private void InitControl() {
        ClearText();
        btnRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                PostRefresh();
            }

        });

        btnMap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                permiss();
            }
        });

        btnAed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 内嵌播放视频
                startActivity(new Intent(DetailActivity.this, NewsActivity.class));
            }
        });

        btnSet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialogSet.show();
            }

        });
        btnOkRevice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                reviceOk();
            }
        });
        btnTaskOver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!"".equals(labTaskNum.getText())) {
                    Intent intent = new Intent(DetailActivity.this, TaskActivity.class);
                    Bundle bundle = new Bundle();//通过Bundle实现数据的传递:
                    bundle.putString("taskNum", (String) labTaskNum.getText());
                    bundle.putString("taskId", _taskId);
                    bundle.putString("alterNumber", _alterNumber);
                    bundle.putString("subConfirmTime", _subConfirmTime);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void ClearText() {
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
    }

    public void startNaviGao() {
        if (isAvilible(getBaseContext(), "com.autonavi.minimap")) {
            try {
                String url = "";
                if (_Lng != 0 && _Lat != 0) {
                    url = "androidamap://route?sourceApplication=softname&sname=我的位置&dlat=" + _Lat + "&dlon=" + _Lng + "&dname=" + labWaitAddress.getText().toString() + "&dev=0&m=0&t=0";
                } else {
                    url = "androidamap://route?sourceApplication=softname&sname=我的位置&dname=" + labWaitAddress.getText().toString() + "&dev=0&m=0&t=0";
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); // 使用 Intent.ACTION_VIEW 来启动 Intent
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alert("未安装高德地图APP");
        }
    }

    /**
     * @param context
     * @param type      公交：bus
     *                  驾车：drive
     *                  步行：walk（仅适用移动端）
     * @param fromName
     * @param fromcoord fromcoord=39.873145,116.413306
     * @param toName
     * @param tocoord
     */
    public void toTenCent(Context context, String type, String fromName, String fromcoord, String toName, String tocoord) {
        if (isAvilible(context, "com.tencent.map")) {
            StringBuffer fromTo = new StringBuffer("qqmap://map/routeplan?type=");

            if (TextUtils.isEmpty(type)) type = "drive";
            fromTo.append(type);

            if (!TextUtils.isEmpty(fromName)) fromTo.append("&from=" + fromName);

            if (!TextUtils.isEmpty(fromcoord)) fromTo.append("&fromcoord=" + fromcoord);

            if (!TextUtils.isEmpty(toName)) fromTo.append("&to=" + toName);
            if (!TextUtils.isEmpty(tocoord)) fromTo.append("&tocoord=" + tocoord);
            fromTo.append("&policy=2");
            fromTo.append("&referer=ACVBZ-VZYKJ-ENDF7-KKR4I-Q75PO-3UBRA");

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            //将功能Scheme以URI的方式传入data
            intent.setData(Uri.parse(fromTo.toString()));
            context.startActivity(intent);
        } else {
            alert("未安装腾讯地图");
        }
    }

    public void go2Baidu() {
        try {
            Intent intent = Intent.getIntent("intent://map/direction?origin=我的位置&destination=" + labWaitAddress.getText().toString() + "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if (isAvilible(getBaseContext(), "com.baidu.BaiduMap")) {
                startActivity(intent);
                //SetNetwork();
//                setAirPlaneMode(this,true);
//                reNetwork();
            } else {
                alert("未安装百度地图APP");
                startNaviGao();
                //setUpGaodeAppByMine();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void reNetwork() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                setAirPlaneMode(DetailActivity.this, false);
            }
        }, 10000);
    }

    private void setAirPlaneMode(Context context, boolean enable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enable ? 1 : 0);
        } else {
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
        }
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enable);
        context.sendBroadcast(intent);
    }

    private void SetNetwork() {
        ContentResolver cr = getContentResolver();
        if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")) {
            //获取当前飞行模式状态,返回的是String值0,或1.0为关闭飞行,1为开启飞行
            //如果关闭飞行,则打开飞行
            Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            sendBroadcast(intent);
        } else {
            //否则关闭飞行
            Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "0");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            sendBroadcast(intent);
        }
    }


    public static boolean isAvilible(Context context, String packageName) {
        if (context == null || packageName == null || packageName.isEmpty()) {
            return false; // 无效输入检测
        }

        final PackageManager packageManager = context.getPackageManager();

        // 在 Android 11 (API 30) 或更高版本中使用更安全的方法
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                return true; // 找到了包名
            } catch (PackageManager.NameNotFoundException e) {
                return false; // 没有找到包名
            }
        } else {
            // 对于低于 Android 11 的版本
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

            if (packageInfos != null) {
                for (PackageInfo packageInfo : packageInfos) {
                    if (packageName.equals(packageInfo.packageName)) {
                        return true; // 找到了包名
                    }
                }
            }
            return false; // 没有找到包名
        }
    }


    private void GetControl() {
        labTaskNum = findViewById(R.id.labTaskNum);
        labPatName = findViewById(R.id.labPatName);
        labPatSex = findViewById(R.id.labPatSex);
        labPatAge = findViewById(R.id.labPatAge);
        labAlterTelNo = findViewById(R.id.labAlterTelNo);
        labAddress = findViewById(R.id.labAddress);
        labWaitAddress = findViewById(R.id.labWaitAddress);
        labCase = findViewById(R.id.labCase);
        labContact = findViewById(R.id.labContact);
        labAlterTime = findViewById(R.id.labAlterTime);
        labzs = findViewById(R.id.labzs);
        labyscd = findViewById(R.id.labyscd);
        btnPos = findViewById(R.id.btnPos);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnMap = findViewById(R.id.btnMap);
        btnAed = findViewById(R.id.btnAed);
        btnSet = findViewById(R.id.btnSet);
        btnCall1 = findViewById(R.id.btnCall1);
        btnCall2 = findViewById(R.id.btnCall2);
        btnGEO1 = findViewById(R.id.btnGEO1);
        btnGEO2 = findViewById(R.id.btnGEO2);
        btnOkRevice = findViewById(R.id.btnOkRevice);
        btnTaskOver = findViewById(R.id.btnTaskInfo);

    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private class loadJSONStringTask extends AsyncTask<String, Integer, String> {
        int method = -1;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (params[1].equals("getTaskInfo")) {
                method = 0;
            } else if (params[1].equals("getAddressLocation")) {
                method = 1;
            } else if (params[1].equals("reviceOk")) {
                method = 2;
            } else if (params[1].equals("checkVersion")) {
                method = 3;
            } else if (params[1].equals("geo120")) {
                method = 4;
            }

            String result = null;
            try {

                result = WebProxy.getString(params[0], WebProxy.WebRequestType.Get, "");


            } catch (IOException e) {
                // Use a Handler to post on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // alert("网络异常：" + e.getMessage());
                    ToastUtil.show("网络异常：" + e.getMessage());
                    writeFile("errorIO.log", e.toString());
                });
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                case 2:
                    reviceOkCallback(result);
                    break;
                case 3:
                    checkVersionCallback(result);
                    break;
                case 4:
                    getGEOCallback(result);
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkVersionCallback(String reslut) {
        if (reslut != null && !reslut.isEmpty()) {
            JSONObject res = (JSONObject) JSONObject.parse(reslut);
            if (res != null && res.getString("code") != null && res.getString("code").equals("10000")) {
                JSONObject data = res.getJSONObject("data");
                if (data != null && data.getString("appUrl") != null && !data.getString("appUrl").isEmpty()) {
                    String ver = res.getJSONObject("data").getString("appVersion");

                    alertDialogEx dialog = new alertDialogEx(this, "升级", "有最新版本请点击确定升级", true);
                    dialog.showDialog();
                    _version = ver;
                    String url = data.getString("appUrl");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    WriteVersion();
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);

                } else PostRefresh();
            } else PostRefresh();
        } else PostRefresh();
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void reviceOk() {
        try {
            String url = String.format(_ReviceOK_URL, _ServerIP, _ServerPort, _taskId);
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "reviceOk");
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    private void reviceOkCallback(String result) {
        if (result != null && !result.equals("null")) {
            JSONObject json = JSONObject.parseObject(result);
            if (json.getString("code").equals("10000")) {
                alert("确认成功");
                this.btnOkRevice.setEnabled(false);
                this.btnTaskOver.setEnabled(true);
                // 当没有收到正确的结果时，停止TTS播放
                if (isBound && notificationService != null) {
                    notificationService.stopTTS();
                }
            } else {
                // 当结果为空或为 "null" 时，停止TTS播放
                if (isBound && notificationService != null) {
                    notificationService.stopTTS();
                }
            }
        } else {
            // 当结果为空或为 "null" 时，停止TTS播放
            if (isBound && notificationService != null) {
                notificationService.stopTTS();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void getTaskInfo() {
        try {
            String url = _Url;
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "getTaskInfo");
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressWarnings("deprecation")
    private void getAddressLocation(String address, String city) {
        loadJSONStringTask task = new loadJSONStringTask();
        task.execute(String.format(_gdGeo, URLEncoder.encode(address), URLEncoder.encode(city)), "getAddressLocation");
    }

    private void getAddressLocationCallback(String location) {
        try {
            if (location != null && !location.equals("null")) {

                JSONObject json = JSONObject.parseObject(location);
                if (json.getString("status").equals("1")) {
                    JSONArray locations = json.getJSONArray("geocodes");
                    if (!locations.isEmpty()) {
                        String loc = locations.getJSONObject(0).getString("location");
                        String[] pos = loc.split(",");
                        if (pos.length > 0) {
                            Lng = pos[0];
                            Lat = pos[1];

                            btnPos.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    // TODO Auto-generated method stub
                                    startNaviGao();

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getTaskInfoCallback(String content) {
        try {
            if (content != null && !content.equals("null")) {
                JSONObject result = JSONObject.parseObject(content);
                if (result.getString("code").equals("10000")) {
                    JSONArray json = JSONArray.parseArray(result.getString("data"));
                    if (!json.isEmpty()) {
                        loadFromJson(json.getJSONObject(0));
                        //btnOkRevice.setEnabled(true);
                        //btnTaskOver.setEnabled(true);
                    } else {
                        // alert("无最新任务");
                        Toast.makeText(this, "无最新任务", Toast.LENGTH_SHORT).show();
                        ClearText();
                        btnOkRevice.setEnabled(false);
                        btnTaskOver.setEnabled(false);
                    }
                }

            } else {

            }
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    /**
     * 调起系统发短信功能
     *
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber, String message) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);

            _geoThread = new GeoThread();
            _geoThread.phone = phoneNumber;
            _geoThread.start();

            startActivity(intent);
//            basicConsume(phoneNumber);
        }
    }

    class GeoThread extends Thread {
        // 设置是否循环推送
        public boolean isRunning = true;
        public String phone = "";

        public void run() {
            while (isRunning) {
                try {
                    // 间隔时间
                    Thread.sleep(10000);
                    // 获取服务器消息
                    // String serverMessage = getServerMessage();
                    getGEO(phone);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void getGEO(String mobile) {
        try {
            String url = String.format(_getGEOUrl, mobile);
            loadJSONStringTask task = new loadJSONStringTask();
            task.execute(url, "geo120");
        } catch (Exception e) {
            // alert(WEB_ERROR);
        }
    }

    private void getGEOCallback(String result) {
        try {
            if (result != null && !result.equals("null")) {
                JSONObject object = JSONObject.parseObject(result);
                if (object.getJSONObject("data") != null) {
                    JSONObject location = object.getJSONObject("data");
                    Location loc = location.toJavaObject(Location.class);
                    DetailActivity.this._Lat = loc.lat;
                    DetailActivity.this._Lng = loc.lng;
                    alert(String.format("手机精确定位数据已确认：经度【%s】，纬度【%s】，地址：%s", loc.lng, loc.lat, loc.addr));
                    _geoThread.isRunning = false;
                }
            }
        } catch (Exception e) {

        }
    }

    //发送短信
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void sendSMSS(String phone, String content) {
        if (!content.isEmpty() && !phone.isEmpty()) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
            Toast.makeText(DetailActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
//            basicConsume(phone);
        } else {
            Toast.makeText(this, "手机号或内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFromJson(JSONObject json) {
        if (json != null) {
            try {
                // 获取carOutTime
                txtCarOutDate = json.getString("carOutTime");
                _alterNumber = json.getString("alterNumber");
                _taskId = json.getString("id");
                labTaskNum.setText(GetNoNullString(json.getString("taskNum")));
                labPatName.setText(GetNoNullString(json.getString("patName")));
                labPatSex.setText(GetNoNullString(json.getString("patSex")));
                String age = GetNoNullString(json.getString("patAge"));
                labPatAge.setText("0".equals(age) ? "" : age);
                final String AlterTelNo = GetNoNullString(json.getString("alertTelephone"));

                final String lat = GetNoNullString(json.getString("latitude"));
                final String lng = GetNoNullString(json.getString("longitude"));
                if (lat == null || lat.isEmpty()) _Lat = 0;
                else _Lat = Double.valueOf(lat);
                if (lng == null || lng.isEmpty()) _Lng = 0;
                else _Lng = Double.valueOf(lng);
                labAlterTelNo.setText(AlterTelNo);
                if (AlterTelNo != null && !AlterTelNo.equals("")) {
                    btnCall1.setVisibility(View.VISIBLE);
                    btnCall1.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AlterTelNo));
//                            if (PackageManager.PERMISSION_GRANTED == Activity.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE))
                            if (Build.VERSION.SDK_INT >= M) {
                                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    return;
                                }
                            }
                            startActivity(intent);
                        }

                    });
//                    btnGEO1.setVisibility(View.VISIBLE);
//                    btnGEO1.setOnClickListener(new OnClickListener() {
//                                                   @Override
//                                                   public void onClick(View view) {
//                                                       doSendSMSTo(AlterTelNo, String.format(_msgGEO, AlterTelNo.hashCode(), AlterTelNo));
//                                                   }
//                                               }
//                    );
                }
                String sceneAddress = GetNoNullString(json.getString("sceneAddress"));
                labAddress.setText(GetNoNullString(json.getString("address")));

                labWaitAddress.setText(sceneAddress);
                if (sceneAddress != null && !sceneAddress.equals("")) {
                    btnPos.setVisibility(View.VISIBLE);
                    btnPos.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            startNaviGao();
                            //go2Baidu();
                            //toTenCent(DetailActivity.this,"","我的位置","",labWaitAddress.getText().toString(),"");
                        }
                    });
                }
                // getAddressLocation(labWaitAddress.getText().toString(),
                // CITY);
                labCase.setText(GetNoNullString(json.getString("caseType")));
                final String callNo = GetNoNullString(json.getString("contactNumber"));
                labContact.setText(callNo);
                if (callNo != null && !callNo.equals("")) {
                    btnCall2.setVisibility(View.VISIBLE);
                    btnCall2.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNo));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    return;
                                }
                            }
                            startActivity(intent);
                        }

                    });

                }
                if (json.getString("alertTime") != null)
                    labAlterTime.setText(datetimeFormat1.format(datetimeFormat1.parse(GetNoNullString(json.getString("alertTime")))));
                labyscd.setText(GetNoNullString(json.getString("ysqx")));
                labzs.setText(GetNoNullString(json.getString("complained")));
                _subConfirmTime = GetNoNullString(json.getString("subConfirmTime"));
                boolean confirm = _subConfirmTime == null || _subConfirmTime.isEmpty();
                btnOkRevice.setEnabled(confirm);
                btnOkRevice.invalidate();
                btnTaskOver.setEnabled(!confirm);
                btnTaskOver.invalidate();
                // labPatNurse
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                writeFile("errorIO.log", e.toString());
            }
        }
    }

    private String GetNoNullString(String value) {
        if (value == null) {
            value = "";
        }
        return value;
    }


    private String readFileLog(final String fileName) {
        // Permission has already been granted
        // You can read the file
        String content = "";
        BufferedReader r = null;
        try {
            File log = new File(fileName);
            if (log.exists()) {
                InputStream fis = new FileInputStream(log);
                InputStreamReader isr = new InputStreamReader(fis);
                r = new BufferedReader(isr);
                //r = new BufferedReader(new InputStreamReader(openFileInput(fileName), "UTF-8"));
                content = r.readLine();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (r != null) r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String readFile(final String fileName) {
        String content = "";
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(openFileInput(fileName), StandardCharsets.UTF_8));
            content = r.readLine();
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (r != null) r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void writeFile(final String fileName, final String content) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(openFileOutput(fileName, MODE_PRIVATE), StandardCharsets.UTF_8));
            pw.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    private void showTips() {
        AlertDialog alertDialog = new AlertDialog.Builder(DetailActivity.this).setTitle("退出程序确认").setMessage("确定要退出程序吗?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DetailActivity.this.finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).create(); // 鍒涘缓瀵硅瘽妗�
        alertDialog.show();
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
        super.onAttachedToWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private Runnable postRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            // 执行你的刷新任务
            PostRefresh();

            // 重新安排Runnable自己再次执行，例如每10秒执行一次
            handRefresh.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当Activity销毁时移除所有回调，防止内存泄露
        handRefresh.removeCallbacks(postRefreshRunnable);
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
