package com.drc.remiscar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.ViewUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

public class TaskActivity extends BaseActivity {

    private TextView txtCarOutDate, txtOutHospitalDate;
    //private TextView txtCarOutTime;
    private TextView txtDestDate;
    //private TextView txtDestTime;
    private TextView txtArriveDate;
    //private TextView txtArriveTime;
    private Button btnSave;
    private Button btnCancel;
    private Button btnAdd;
    private Button btnDel;
    private Spinner listHospital;
    private Spinner listEmptyReason;
    private CheckBox chkEmpty;
    private ListView listPat;
    private String _taskNum;
    private String _alterNumber;
    private String _taskId;

    private EditText txtPatName;
    private EditText txtPatPhone;
    private EditText txtPatAge;
    private EditText txtPatAddress;
    private RadioGroup rdPatSex;
    private RadioButton rdPatSexMan;
    private RadioButton rdPatSexWoman;
    //    private EditText txtPatGoWhere;
    private EditText txtPatDoState;
    private Spinner cmbToWhere;
    private AlertDialog alertDialog;

    private Button btnOut;
    private Button btnOver;
    private Button btnScene, btnOutHospital;
    private Button btnHospital;

    LinearLayout trOutHospital;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ListPatAdapter apsAdapter = null;
    boolean _isEnd = false;
    String _subConfirmTime = "";
    String _carNumber = "";
    TaskInfo _taskInfo;

    String currentReceiverHospitalId;

    List<SysEnum> toWhereList =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _taskId = this.getIntent().getExtras().getString("taskId");
        _taskNum = this.getIntent().getExtras().getString("taskNum");
        _alterNumber = this.getIntent().getExtras().getString("alterNumber");
        setContentView(R.layout.activity_task);
        GetControl();
        InitControl();
        GetData();
    }

    private void GetData() {
        String url = String.format(DetailActivity._GetHospital_URL, DetailActivity._ServerIP, DetailActivity._ServerPort);
        try {
            loadJSONStringTask loadJSON = new loadJSONStringTask();
            loadJSON.execute(url, "getHospital");
        } catch (Exception e) {
            e.printStackTrace();
        }

        url = String.format(DetailActivity._GetEnum_URL, DetailActivity._ServerIP, DetailActivity._ServerPort, DetailActivity._ENUM_EMPTY_REASON);
        try {
            loadJSONStringTask loadJSON = new loadJSONStringTask();
            loadJSON.execute(url, "getEmptyReasonEnum");
        } catch (Exception e) {
            e.printStackTrace();
        }

        url = String.format(DetailActivity._GetEnum_URL, DetailActivity._ServerIP, DetailActivity._ServerPort, DetailActivity._ENUM_EMPTY_TOWHERE);
        try {
            loadJSONStringTask loadJSON = new loadJSONStringTask();
            loadJSON.execute(url, "getToWhereEnum");
        } catch (Exception e) {
            e.printStackTrace();
        }

        url = String.format(DetailActivity._GetTaskAndPat_URL, DetailActivity._ServerIP, DetailActivity._ServerPort, _taskId);
        try {
            loadJSONStringTask loadJSON = new loadJSONStringTask();
            loadJSON.execute(url, "getTaskInfo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class loadJSONStringTask extends AsyncTask<String, Integer, String> {
        int method = -1;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            WebProxy.WebRequestType type = WebProxy.WebRequestType.Get;
            String arg = "";
            switch (params[1]) {
                case "getTaskInfo":
                    method = 0;
                    break;
                case "getHospital":
                    method = 1;
                    break;

                case "getEmptyReasonEnum":
                    method = 2;
                    break;
                case "SaveTaskInfo":
                    method = 3;
                    type = WebProxy.WebRequestType.Post;
                    arg = JSONObject.toJSON(_taskInfo).toString();
                    break;
                case "OutCar":
                    method = 4;
                    type = WebProxy.WebRequestType.Post;
                    break;
                case "ArrivedScene":
                    method = 5;
                    type = WebProxy.WebRequestType.Post;
                    break;
                case "Comeback":
                    method = 6;
                    type = WebProxy.WebRequestType.Post;
                    break;
                case "TaskOver":
                    method = 7;
                    type = WebProxy.WebRequestType.Post;
                    arg = JSONObject.toJSON(_taskInfo).toString();
                    break;
                case "getToWhereEnum":
                    method = 8;
                    break;
                case "ArrivedHospital":
                    type = WebProxy.WebRequestType.Post;
                    method = 9;
                    break;
            }

            String result = null;
            try {
                result = WebProxy.getString(params[0], type, arg);
            } catch (IOException e) {
                writeFile("error.log", e.toString());
            }
            return result;
        }

        private void writeFile(final String fileName, final String content) {
            PrintWriter pw = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    pw = new PrintWriter(new OutputStreamWriter(openFileOutput(fileName, MODE_PRIVATE), StandardCharsets.UTF_8));
                }
                pw.println(content);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pw.close();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            switch (method) {
                case 0:
                    getTaskInfoCallback(result);
                    break;
                case 1:
                    getHospitalCallback(result);
                    break;
                case 2:
                    getEnumCallback(result);
                    break;
                case 3:
                    saveTaskInfoCallback(result);
                    break;
                case 4:
                    UpdateTime(txtCarOutDate, btnOut, result);
                    break;
                case 5:
                    UpdateTime(txtArriveDate, btnScene, result);
                    break;
                case 6:
                    UpdateTime(txtDestDate, btnHospital, result);
                    break;
                case 7:
                    taskOverCallback(result);
                    break;
                case 8:
                    getToWhereEnumCallback(result);
                    break;
                case 9:
                    // 解析 {"code":"10000","msg":"操作成功","data":null,"timestamp":1729587599375}
                    UpdateTime(txtOutHospitalDate, btnOutHospital, result);
                    break;
            }
        }
    }

    private void UpdateTime(TextView date, Button btn, String result) {
        if (result != null && !result.isEmpty()) {
            JSONObject data = JSONObject.parseObject(result);
            if (data != null && data.getString("code").equals("10000")) {
                try {
                    if (btn.getId() == btnOutHospital.getId()) {
                        date.setText(formatTimestamp(data.getString("data")));
                        btn.setEnabled(false);
                        return;
                    }
                    date.setText(datetimeFormat.format(datetimeFormat.parse(data.getString("data"))));
                    btn.setEnabled(false);
                    if (btn.getId() == btnOut.getId()) {
                        if (this.txtArriveDate.getText().toString().isEmpty()) {
                            btnScene.setEnabled(true);
                        }
                    } else if (btn.getId() == btnScene.getId()) {
                        if (this.txtDestDate.getText().toString().isEmpty()) {
                            btnHospital.setEnabled(true);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getEnumCallback(String result) {
        if (result != null && !result.equals("")) {
            List<SysEnum> emptyReasonList = JSONArray.parseArray(JSONObject.parseObject(result).getString("data"), SysEnum.class);
            SysEnum sysEnum = new SysEnum();
            sysEnum.setId("-1");
            sysEnum.setEnumName("");
            emptyReasonList.add(sysEnum);
            ArrayAdapter<SysEnum> adapter = new ArrayAdapter<SysEnum>(this, android.R.layout.simple_spinner_item, emptyReasonList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listEmptyReason.setAdapter(adapter);
            setSpinnerItemEnumSelectedByID(listEmptyReason, "-1");
        }
    }

    private void getToWhereEnumCallback(String result) {
        if (result != null && !result.equals("")) {
            toWhereList = JSONArray.parseArray(JSONObject.parseObject(result).getString("data"), SysEnum.class);
//            SysEnum sysEnum = new SysEnum();
//            sysEnum.setId("-1");
//            sysEnum.setEnumName("");
//            toWhereList.add(sysEnum);

            //setSpinnerItemEnumSelectedByID(cmbToWhere, "-1");
        }
    }

    private void getHospitalCallback(String result) {
        if (result != null && !result.equals("")) {

            List<Hospital> hospitalList = JSONArray.parseArray(JSONObject.parseObject(result).getString("data"), Hospital.class);
            Hospital hospital = new Hospital();
            hospital.setHospitalId("-1");
            hospital.setHospitalName("");
            hospitalList.add(hospital);
            ArrayAdapter<Hospital> adapter = new ArrayAdapter<Hospital>(this, android.R.layout.simple_spinner_item, hospitalList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listHospital.setAdapter(adapter);
            setSpinnerItemHospitalSelectedByID(listHospital, TextUtils.isEmpty(currentReceiverHospitalId) ? "-1" : currentReceiverHospitalId);
            listHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // 获取选中的项
                    if (!hospitalList.isEmpty()) {
                        String hospitalId = hospitalList.get(position).getHospitalId();
                        if (!TextUtils.isEmpty(hospitalId)) {
                            if (!hospitalId.equals(currentReceiverHospitalId)) {
                                trOutHospital.setVisibility(View.VISIBLE);
                            } else {
                                trOutHospital.setVisibility(View.GONE);
                            }
                        } else {
                            trOutHospital.setVisibility(View.GONE);
                        }
                    } else {
                        trOutHospital.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void getTaskInfoCallback(String result) {
        if (result != null && !result.equals("")) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(jsonObject!=null) {
                TaskInfo taskInfo = jsonObject.getJSONObject("data").toJavaObject(TaskInfo.class);
                _taskInfo = taskInfo;
                _carNumber = taskInfo.getCarNumber();
                // 当前接单人的自身医院
                // The current receiver's own hospital
                currentReceiverHospitalId = taskInfo.getHospitalId();
                setSpinnerItemHospitalSelectedByID(listHospital, TextUtils.isEmpty(currentReceiverHospitalId) ? "-1" : currentReceiverHospitalId);
                try {
                    if (taskInfo.getCarOutTime() != null && !taskInfo.getCarOutTime().isEmpty()) {
                        txtCarOutDate.setText(datetimeFormat.format(new Date(Long.valueOf(taskInfo.getCarOutTime()))));
                        //txtCarOutTime.setText(timeFormat.format(new Date(Long.valueOf(taskInfo.getCarOutTime()))));
                        this.btnOut.setEnabled(false);
                    } else {
                        this.btnOut.setEnabled(true);
                    }
                    if (taskInfo.getOutHospTime() !=null && !taskInfo.getOutHospTime().isEmpty()) {
                        trOutHospital.setVisibility(View.VISIBLE);
                        txtOutHospitalDate.setText(formatTimestamp(taskInfo.getOutHospTime()));
                        btnOutHospital.setEnabled(false);
                    }  else {
                        btnOutHospital.setEnabled(true);
                    }

                    if (taskInfo.getArriveSceneTime() != null && !taskInfo.getArriveSceneTime().isEmpty()) {
                        txtArriveDate.setText(datetimeFormat.format(new Date(Long.valueOf(taskInfo.getArriveSceneTime()))));
                        //txtArriveTime.setText(timeFormat.format(new Date(Long.valueOf(taskInfo.getArriveSceneTime()))));
                        btnScene.setEnabled(false);
                    } else if (!btnOut.isEnabled()) {
                        btnScene.setEnabled(true);
                    }
                    if (taskInfo.getCarBackTime() != null && !taskInfo.getCarBackTime().isEmpty()) {
                        txtDestDate.setText(datetimeFormat.format(new Date(Long.valueOf(taskInfo.getCarBackTime()))));
                        //txtDestTime.setText(timeFormat.format(new Date(Long.valueOf(taskInfo.getCarBackTime()))));
                        btnHospital.setEnabled(false);
                        _isEnd = true;
                    } else if (!btnScene.isEnabled()) {
                        btnHospital.setEnabled(true);
                    }
                    if (taskInfo.getDestHospitalId() != null)
                        setSpinnerItemHospitalSelectedByID(listHospital, taskInfo.getDestHospitalId());
                    if (taskInfo.getEmptyReason() != null) {
                        Boolean empty = setSpinnerItemEnumSelectedByID(listEmptyReason, taskInfo.getEmptyReason());
                        chkEmpty.setChecked(empty);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (taskInfo.getPatInfos() != null) {
                    SetGoWhere(taskInfo);
                    apsAdapter = new ListPatAdapter(this, R.layout.list_item, taskInfo.getPatInfos());
                    listPat.setAdapter(apsAdapter);
                }
            }
        }
    }

    private void SetGoWhere(TaskInfo taskInfo) {

        for (patInfo pat : taskInfo.getPatInfos()) {
            pat.setPatGoWhereInt(pat.getToWhere());

            pat.setToWhere( pat.getToWhereStr());
        }
    }

    public void taskOverCallback(String result) {
        JSONObject res = JSONObject.parseObject(result);
        if (res.getString("code").equals("10000")) {
            Intent intent = new Intent();
            Bundle bunle = new Bundle();
            bunle.putString("over", "ok");
            intent.putExtras(bunle);
            setResult(RESULT_OK, intent);  //设置返回结果
            alert("任务结束成功");
            finish(); //关闭子窗口，否则数据无法返回
        } else {
            alert("任务结束失败"+res.getString("msg"));
        }
    }

    public void saveTaskInfoCallback(String result) {
        JSONObject res = JSONObject.parseObject(result);
        if (res.getString("code").equals("10000")) {
            Intent intent = new Intent();
            Bundle bunle = new Bundle();
            bunle.putString("over", "ok");
            intent.putExtras(bunle);
            setResult(RESULT_OK, intent);  //设置返回结果
            alert("保存成功");
            finish(); //关闭子窗口，否则数据无法返回
        } else {
            alert("保存信息失败");
        }
    }

    public void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter spAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = spAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(spAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

    public void setSpinnerItemHospitalSelectedByID(Spinner spinner, String id) {
        SpinnerAdapter spAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        if (null == spAdapter) return;
        int k = spAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (id.equals(((Hospital) spAdapter.getItem(i)).getHospitalId())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

    public boolean setSpinnerItemEnumSelectedByID(Spinner spinner, String id) {
        SpinnerAdapter spAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = spAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (id.equals(((SysEnum) spAdapter.getItem(i)).getId())) {
                spinner.setSelection(i, true);// 默认选中项
                return true;
            }
        }
        return false;
    }

    private void showTimeDialog(final TextView editText) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = df.format(new Date());
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(TaskActivity.this);
        final View dialogView = LayoutInflater.from(TaskActivity.this)
                .inflate(R.layout.dialog_time, null);
        final TimePicker timePicker = dialogView.findViewById(R.id.tpDialogTime);

        timePicker.setIs24HourView(true);
        customizeDialog.setTitle("请选择时间");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectTime = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());
                        editText.setText(selectTime);
                    }
                });
        customizeDialog.show();
    }

    // 隐藏手机键盘
    private void hideIM(View edt) {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = edt.getWindowToken();

            if (windowToken != null) {
                im.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {

        }
    }

    private void showDateDialog(final TextView editText) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = df.format(new Date());
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(TaskActivity.this);
        final View dialogView = LayoutInflater.from(TaskActivity.this)
                .inflate(R.layout.dialog_date, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.tpDialogDate);

//        datePicker.setIs24HourView(true);
        customizeDialog.setTitle("请选择日期");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectTime = String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                        editText.setText(selectTime);
                    }
                });
        customizeDialog.show();
    }

    private void GetControl() {
        trOutHospital = findViewById(R.id.trOutHospital);
        btnOut = findViewById(R.id.btnOut);
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = String.format(DetailActivity._OutCar, DetailActivity._ServerIP, DetailActivity._ServerPort, _taskId);
                    loadJSONStringTask loadJSON = new loadJSONStringTask();
                    loadJSON.execute(url, "OutCar");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnOver = findViewById(R.id.btnOver);
        btnOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (chkEmpty.isChecked() && listEmptyReason.getSelectedItem().toString().isEmpty()) {
                        alert("请选择跑空原因");
                        return;
                    }
                    if (txtCarOutDate.getText().toString().isEmpty()) {
                        alert("请点击出车按钮填写出车时间");
                        return;
                    }
                    if (txtArriveDate.getText().toString().isEmpty()) {
                        alert("请点击到达现场按钮填写到达现场时间");
                        return;
                    }
                    if (txtDestDate.getText().toString().isEmpty()) {
                        alert("请点击送达医院按钮填写送达医院时间");
                        return;
                    }
//                    if (!chkEmpty.isChecked() && apsAdapter.getCount() == 0) {
//                        alert("请输入病人信息");
//                        return;
//                    }
                    String url = String.format(DetailActivity._TaskOver, DetailActivity._ServerIP, DetailActivity._ServerPort, _taskId);
                    SaveTaskInfo(url, "TaskOver");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnHospital = findViewById(R.id.btnHospital);
        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断出车时间
                if (TextUtils.isEmpty(txtCarOutDate.getText().toString())) {
                    // 未出车
                    alert("请点击出车按钮填写出车时间");
                    return;
                }
                // 判断到达现场时间
                if (TextUtils.isEmpty(txtArriveDate.getText().toString())) {
                    // 未到达现场
                    alert("请点击到达现场按钮填写到达现场时间");
                    return;
                }
                try {
                    String url = String.format(DetailActivity._Comeback, DetailActivity._ServerIP, DetailActivity._ServerPort, _taskId);
                    loadJSONStringTask loadJSON = new loadJSONStringTask();
                    loadJSON.execute(url, "Comeback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnScene = findViewById(R.id.btnScene);
        btnScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断是否已经出车
                if (TextUtils.isEmpty(txtCarOutDate.getText().toString())) {
                    // 未出车
                    alert("请点击出车按钮填写出车时间");
                    return;
                }
                try {
                    String url = String.format(DetailActivity._ArrivedScene, DetailActivity._ServerIP, DetailActivity._ServerPort, _taskId);
                    loadJSONStringTask loadJSON = new loadJSONStringTask();
                    loadJSON.execute(url, "ArrivedScene");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // 送达外院
        txtOutHospitalDate = findViewById(R.id.txtOutHospitalDate);
        btnOutHospital = findViewById(R.id.btnOutHospital);
        btnOutHospital.setOnClickListener(v -> {
            // 需要判断是否出车
            if (TextUtils.isEmpty(txtCarOutDate.getText().toString())) {
                alert("请点击出车按钮填写出车时间");
                return;
            }
            // 需要判断到达时间
            if (TextUtils.isEmpty(txtArriveDate.getText().toString())) {
                alert("请点击到达现场按钮填写到达现场时间");
                return;
            }
            // 然后请求接口，获取到达时间
            try {
                String url = String.format(DetailActivity._OutHosp, DetailActivity._ServerIP, DetailActivity._ServerPort, _taskId);
                loadJSONStringTask loadJSON = new loadJSONStringTask();
                loadJSON.execute(url, "ArrivedHospital");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        txtCarOutDate = findViewById(R.id.txtCarOutDate);
        txtCarOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDateDialog(txtCarOutDate);
            }
        });
        txtCarOutDate.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        txtCarOutDate.getPaint().setAntiAlias(true);//抗锯齿
//        txtCarOutTime = (TextView) findViewById(R.id.txtCarOutTime);
//        txtCarOutTime.setOnClickListener(new View.OnClickListener() {
//                                             @Override
//                                             public void onClick(View view) {
//                                                 //showTimeDialog(txtCarOutTime);
//                                             }
//                                         }
//        );
//        txtCarOutTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        txtCarOutTime.getPaint().setAntiAlias(true);//抗锯齿
        txtDestDate = findViewById(R.id.txtDestDate);
        txtDestDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIM(view);
                //showDateDialog(txtDestDate);
            }
        });
        txtDestDate.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        txtDestDate.getPaint().setAntiAlias(true);//抗锯齿
//        txtDestTime = (TextView) findViewById(R.id.txtDestTime);
//        txtDestTime.setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View view) {
//                                               //showTimeDialog(txtDestTime);
//                                           }
//                                       }
//        );
//        txtDestTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        txtDestTime.getPaint().setAntiAlias(true);//抗锯齿
        txtArriveDate = findViewById(R.id.txtArriveDate);
        txtArriveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDateDialog(txtArriveDate);
            }
        });
        txtArriveDate.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        txtArriveDate.getPaint().setAntiAlias(true);//抗锯齿
//        txtArriveTime = (TextView) findViewById(R.id.txtArriveTime);
//        txtArriveTime.setOnClickListener(new View.OnClickListener() {
//                                             @Override
//                                             public void onClick(View view) {
//                                                 //showTimeDialog(txtArriveTime);
//                                             }
//                                         }
//        );
//        txtArriveTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        txtArriveTime.getPaint().setAntiAlias(true);//抗锯齿
        listHospital = findViewById(R.id.listHospital);
        listEmptyReason = findViewById(R.id.listEmptyReason);
        chkEmpty = findViewById(R.id.chkEmpty);
        chkEmpty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSpinnerItemHospitalSelectedByID(listHospital, "-1");
                    if (apsAdapter != null) {
                        apsAdapter.clear();
                        apsAdapter.notifyDataSetChanged();
                    }
                } else {
                    setSpinnerItemEnumSelectedByID(listEmptyReason, "-1");
                }
                listEmptyReason.setEnabled(isChecked);
                btnAdd.setEnabled(!isChecked);
                btnDel.setEnabled(!isChecked);
                listHospital.setEnabled(!isChecked);
            }
        });
        listPat = findViewById(R.id.listPat);
        listPat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setBackgroundColor(Color.parseColor("#efefef"));
            }
        });
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEmpty.isChecked() && listEmptyReason.getSelectedItem().toString().isEmpty()) {
                    alert("请选择跑空原因");
                    return;
                }
//                if (!chkEmpty.isChecked() && apsAdapter.getCount() == 0) {
//                    alert("请输入病人信息");
//                    return;
//                }
                String url = String.format(DetailActivity._SaveTaskInfo, DetailActivity._ServerIP, DetailActivity._ServerPort);
                SaveTaskInfo(url, "SaveTaskInfo");
            }
        });
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bunle = new Bundle();
                bunle.putString("over", "cancel");
                intent.putExtras(bunle);
                setResult(RESULT_OK, intent);  //设置返回结果
                finish(); //关闭子窗口，否则数据无法返回
            }
        });
        btnAdd = findViewById(R.id.btnAdd);
        btnDel = findViewById(R.id.btnDel);

        txtPatName = new EditText(this);

        final View contentView = View.inflate(this, R.layout.add_pat, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增病人");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setView(contentView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    addPat();
                } catch (Exception ex) {

                }
            }
        });
        builder.setNegativeButton("取消", null);
        alertDialog = builder.create();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                txtPatName = contentView.findViewById(R.id.addPatName);
                txtPatAge = contentView.findViewById(R.id.addPatAge);
                txtPatAddress = contentView.findViewById(R.id.addPatAddress);
                txtPatPhone = contentView.findViewById(R.id.addPatPhone);
                cmbToWhere = contentView.findViewById(R.id.addCmbToWhere);
                txtPatDoState = contentView.findViewById(R.id.addPatDoState);
                rdPatSex = contentView.findViewById(R.id.addPatSex);
                rdPatSexMan = contentView.findViewById(R.id.addPatSexMan);
                rdPatSexWoman = contentView.findViewById(R.id.addPatSexWoman);
                InitAdd();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TaskActivity.this).setTitle("确定要删除病人信息吗？").setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ListPatAdapter listPatAdapter = (ListPatAdapter) listPat.getAdapter();
                                listPatAdapter.clear();
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });
    }

    @SneakyThrows
    private void SaveTaskInfo(String url, String method) {
        TaskInfo taskInfo = new TaskInfo();
        Date outTime = null;
        Date sceneTime = null;
        Date backTime = null;

        if (!txtCarOutDate.getText().toString().isEmpty()) {
            taskInfo.setCarOutTime(txtCarOutDate.getText().toString());
            try {
                outTime = datetimeFormat.parse(taskInfo.getCarOutTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (!txtArriveDate.getText().toString().isEmpty()) {
            taskInfo.setArriveSceneTime(txtArriveDate.getText().toString());
            try {
                sceneTime = datetimeFormat.parse(taskInfo.getArriveSceneTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (!txtDestDate.getText().toString().isEmpty()) {
            taskInfo.setCarBackTime(txtDestDate.getText().toString());
            try {
                backTime = datetimeFormat.parse(taskInfo.getCarBackTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (outTime != null && sceneTime != null && outTime.compareTo(sceneTime) >= 0) {
            alert("出车时间不能晚于到达现场时间");
            return;
        }

        if (sceneTime != null && backTime != null && sceneTime.compareTo(backTime) >= 0) {
            alert("到达现场时间不能晚于送达医院时间");
            return;
        }

        if (sceneTime == null && backTime != null && outTime != null && outTime.compareTo(backTime) >= 0) {
            alert("出车时间不能晚于送达医院时间");
            return;
        }
        taskInfo.setCarNumber(_carNumber);
        taskInfo.setCarLic(_taskInfo.getCarLic());
        taskInfo.setSceneAddress(_taskInfo.getSceneAddress());
        taskInfo.setId(Long.valueOf(_taskId));
        taskInfo.setDestHospitalId(((Hospital) listHospital.getSelectedItem()).hospitalId);
        taskInfo.setDestHospital(((Hospital) listHospital.getSelectedItem()).hospitalName);
        if (listEmptyReason.getSelectedItem() != null){
            taskInfo.setEmptyReason(((SysEnum) listEmptyReason.getSelectedItem()).Id);
        }
        taskInfo.setAlterNumber(_alterNumber);
        taskInfo.setTaskNumber(_taskNum);
        taskInfo.setEnd(_isEnd);
        int size = apsAdapter.getCount();
        for (int i = 0; i < size; i++)
            taskInfo.getPatInfos().add(apsAdapter.getItem(i));
        try {
            _taskInfo = taskInfo;
            loadJSONStringTask loadJSON = new loadJSONStringTask();
            loadJSON.execute(url, method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitAdd() {
        txtPatName.setText("");
        txtPatAge.setText("");
        txtPatAddress.setText("");
        txtPatPhone.setText("");
//        cmbToWhere.setSelection(-1);
        txtPatDoState.setText("");
        ArrayAdapter<SysEnum> adapter = new ArrayAdapter<SysEnum>(this, android.R.layout.simple_spinner_item, toWhereList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbToWhere.setAdapter(adapter);
    }

    private void addPat() {
//        ListPatAdapter apsAdapter = (ListPatAdapter)listPat.getAdapter();

        patInfo pat = new patInfo();
        pat.setPatName(txtPatName.getText().toString());
        pat.setPatAddress(txtPatAddress.getText().toString());
        pat.setPatAge(txtPatAge.getText().toString());
        pat.setPatContact(txtPatPhone.getText().toString());
        pat.setToWhere(((SysEnum) cmbToWhere.getSelectedItem()).getEnumName());
        pat.setPatGoWhereInt(((SysEnum) cmbToWhere.getSelectedItem()).getId());
        pat.setPatHeal((txtPatDoState.getText().toString()));
        pat.setPatSex(rdPatSexMan.isChecked() ? "男" : "女");
        apsAdapter.add(pat);
        apsAdapter.notifyDataSetChanged();

        listPat.invalidate();
    }

    private void InitControl() {
        try {
            btnOut.setEnabled(false);
            btnScene.setEnabled(false);
            btnHospital.setEnabled(false);
//            if(!_subConfirmTime.isEmpty()){
            txtCarOutDate.setText("");
//            txtCarOutTime.setText("");
            txtDestDate.setText("");
//            txtDestTime .setText("");
            txtArriveDate.setText("");
//            txtArriveTime.setText("");
            listEmptyReason.setEnabled(false);
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 方法：将时间戳转换为指定格式的日期字符串
    public  String formatTimestamp(String timestamp) {
        // 定义输入和输出时间格式
        DateTimeFormatter inputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 解析时间戳并格式化
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
            return dateTime.format(outputFormatter);
        }
        return "";
    }
}
