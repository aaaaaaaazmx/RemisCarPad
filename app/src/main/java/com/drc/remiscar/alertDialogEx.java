package com.drc.remiscar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class alertDialogEx extends Dialog
{
    int dialogResult;
    Handler mHandler ;
    TextView txtMess;
    Button btnEnter;
    public alertDialogEx(Activity context, String title, String mailName, boolean retry)
    {
        super(context);
        setOwnerActivity(context);


        onCreate();

        txtMess.setText(mailName);
        setTitle(title);
    }
    public int getDialogResult()
    {
        return dialogResult;
    }
    public void setDialogResult(int dialogResult)
    {
        this.dialogResult = dialogResult;
    }
    /** Called when the activity is first created. */

    public void onCreate() {
        setContentView(R.layout.dialog);
        btnEnter = findViewById(R.id.btnDialogEnter);
        txtMess = findViewById(R.id.txtDialogMess);
        btnEnter.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View paramView)
            {
                endDialog(1);
            }
        });

    }

    public void endDialog(int result)
    {
        dismiss();
        setDialogResult(result);
        Message m = mHandler.obtainMessage();
        mHandler.sendMessage(m);
    }

    public int showDialog()
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                // process incoming messages here
                //super.handleMessage(msg);
                throw new RuntimeException();
            }
        };
        super.show();
        try {
            Looper.getMainLooper().loop();
        }
        catch(RuntimeException e2)
        {
        }
        return dialogResult;
    }

}
