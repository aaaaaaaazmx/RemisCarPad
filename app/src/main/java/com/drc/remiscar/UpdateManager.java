package com.drc.remiscar;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.drc.remiscar.dialog.ConfirmDialog;
import com.drc.remiscar.dialog.LoadingDialog;
import com.drc.remiscar.net.FileRequest;
import com.drc.remiscar.net.RetrofitRequest;
import com.drc.remiscar.util.Constant;
import com.drc.remiscar.util.StorageUtil;
import com.drc.remiscar.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateManager {
    private static final int DOWNLOAD_ING = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private static final int DOWNLOAD_ERROR = -1;

    public static final boolean CHECK_AUTO = true;
    public static final boolean CHECK_USER = false;


    private final Context context;

    ProgressBar proDownload;
    TextView tvPercent;
    TextView tvKbNow;
    TextView tvKbAll;

    private Dialog downloadDialog;

    private String savePath;
    private int progress;
    private int totalByte;
    private int downByte;

    private final boolean isAutoCheck;
    private boolean cancelUpdate = false;
    private int newVersionCode;
    private String newFileName;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_ING:
                    showProgress();
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
                case DOWNLOAD_ERROR:
                    onDownloadError();
                    break;
            }
        }
    };

    /**
     * @param context
     * @param isAutoCheck
     */
    public UpdateManager(Context context, boolean isAutoCheck) {
        this.context = context;
        this.isAutoCheck = isAutoCheck;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkUpdate() {
        if (!isAutoCheck) {
            LoadingDialog.show(context, R.string.layout_version_checking);
        }
        //getNewVersion();

        String url = String.format(DetailActivity._GetNewVersion, DetailActivity._ServerIP, DetailActivity._ServerPort, 0, VersionUtil.getVersionCode(this.context));
        try {
            //getNewVersionExt(url);
            getNewVersion(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getNewVersionExt(String url) throws IOException {
        loadJSONStringTask getNew = new loadJSONStringTask();
        getNew.execute(url,"");
//       String result =  WebProxy.getString(url, WebProxy.WebRequestType.Get, "");
//       checkVersionCallback(result);
    }

    /**
     * 检测最新版本
     */
    private void getNewVersion(String url) {
        RetrofitRequest.sendGetRequest(url, new RetrofitRequest.ResultHandler(context) {
            @Override
            public void onBeforeResult() {
                LoadingDialog.close();
            }

            @Override
            public void onResult(String response) {
                if (response == null || response.trim().length() == 0) {
                    //Toast.makeText(context, R.string.layout_version_no_new, Toast.LENGTH_SHORT).show();
                    LoadingDialog.close();
                    return;
                }
                try {
                    com.alibaba.fastjson.JSONObject res = null;
                    try {
                        res = (com.alibaba.fastjson.JSONObject)com.alibaba.fastjson.JSONObject.parse(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (res != null && res.getString("code") != null && res.getString("code").equals("10000")) {
                        com.alibaba.fastjson.JSONObject data = res.getJSONObject("data");
                        if (data != null && data.getString("appUrl") != null && !data.getString("appUrl").isEmpty()) {
                            String url = data.getString("appUrl");
                            newFileName = data.getString("appFileName");
                            LoadingDialog.close();
                            showUpdateDialog(url,newFileName);
                        }else
                            LoadingDialog.close();
                    }else
                        LoadingDialog.close();
                } catch (Exception e) {
//                    Toast.makeText(context, R.string.layout_version_no_new, Toast.LENGTH_SHORT).show();
                    LoadingDialog.close();
                }
            }

            @Override
            public void onAfterFailure() {
                LoadingDialog.close();
//                if (!isAutoCheck) {
//                    Toast.makeText(context, R.string.layout_version_error, Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private class loadJSONStringTask extends AsyncTask<String, Integer, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                result = WebProxy.getString(strings[0], WebProxy.WebRequestType.Get, "");
            } catch (IOException e) {
                Toast.makeText(context, "网络异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            checkVersionCallback(result);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkVersionCallback(String result) {
        if (result != null && !result.isEmpty()) {
            com.alibaba.fastjson.JSONObject res = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result);
            if (res != null && res.getString("code") != null && res.getString("code").equals("10000")) {
                com.alibaba.fastjson.JSONObject data = res.getJSONObject("data");
                if (data != null && data.getString("appUrl") != null && !data.getString("appUrl").isEmpty()) {
                    newFileName = data.getString("appFileName");
                    String url = data.getString("appUrl");
                    showUpdateDialog(url,newFileName);
                } else
                    LoadingDialog.close();
            } else
                LoadingDialog.close();
        } else
            LoadingDialog.close();
    }

    /**
     * 显示更新提醒对话框
     *
     * @param url 新apk文件名称
     */
    private void showUpdateDialog(final String url,String fileName) {
        ConfirmDialog dialog = new ConfirmDialog(UpdateManager.this.context, new ConfirmDialog.OnClickListener() {
            @Override
            public void onConfirm() {
                showDownloadDialog(url,fileName);
            }
        });
        dialog.setTitle(R.string.note_confirm_title);
        dialog.setContent(R.string.layout_version_new);
        dialog.setConfirmText(R.string.layout_yes);
        dialog.setCancelText(R.string.layout_no);
        dialog.show();
    }

    /**
     * 显示apk下载进度
     *
     * @param url 新apk文件名称
     */
    private void showDownloadDialog(String url,String fileName) {
        Builder builder = new Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        proDownload = view.findViewById(R.id.pro_download);
        tvPercent = view.findViewById(R.id.txt_percent);
        tvKbNow = view.findViewById(R.id.txt_kb_now);
        tvKbAll = view.findViewById(R.id.txt_kb_all);

        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadDialog != null) {
                    downloadDialog.dismiss();
                }
                cancelUpdate = true;
            }
        });

        downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        downloadDialog.getWindow().setContentView(view);

        downloadApk(url,fileName);
    }

    /**
     * 后台下载apk文件
     *
     * @param url 新apk文件名称
     */
    private void downloadApk(String url,String fileName) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .callbackExecutor(executorService)
                .build();


        FileRequest fileRequest = retrofit.create(FileRequest.class);
        Call<ResponseBody> call = fileRequest.download(fileName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (writeResponseBodyToDisk(response.body())) {
                        downloadDialog.dismiss();
                    } else {
                        mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                    }
                } else {
                    mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
            }
        });
    }

    /**
     * 将apk写入手机存储空间
     *
     * @param body
     * @return
     */
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        savePath = StorageUtil.getDownloadPath(context);
        File apkFile = new File(savePath, newFileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096];
            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(apkFile);

            BigDecimal bd1024 = new BigDecimal(1024);
            totalByte = new BigDecimal(fileSize).divide(bd1024, BigDecimal.ROUND_HALF_UP).setScale(0).intValue();

            while (!cancelUpdate) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                progress = (int) (((float) (fileSizeDownloaded * 100.0 / fileSize)));
                downByte = new BigDecimal(fileSizeDownloaded).divide(bd1024, BigDecimal.ROUND_HALF_UP).setScale(0).intValue();
                mHandler.sendEmptyMessage(DOWNLOAD_ING);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在界面显示实时进度
     */
    private void showProgress() {
        proDownload.setProgress(progress);
        tvPercent.setText(progress + "%");
        tvKbAll.setText(totalByte + "Kb");
        tvKbNow.setText(downByte + "Kb");
    }

    private void onDownloadError() {
        Toast.makeText(context, R.string.layout_download_error, Toast.LENGTH_SHORT).show();
        downloadDialog.dismiss();
    }

    /**
     * 安装最新下载的apk文件
     */
    private void installApk() {
        File apkFile = new File(savePath, newFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}