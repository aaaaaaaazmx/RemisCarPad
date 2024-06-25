package com.drc.remiscar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.drc.remiscar.R;

public class ConfirmDialog {
    private final Context context;
    private final OnClickListener listener;

    private final Dialog dialog;
    private TextView tvTitle;
    private TextView tvContent;
    private Button btnCancel;
    private Button btnConfirm;

    public ConfirmDialog(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_confirm, null);

        initView(layout);

        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);
    }

    private void initView(View layout) {
        tvTitle = layout.findViewById(R.id.txt_title);
        tvContent = layout.findViewById(R.id.txt_content);

        btnCancel = layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnConfirm = layout.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onConfirm();
                }
                dialog.dismiss();
            }
        });
    }

    public void setTitle(String text) {
        tvTitle.setText(text);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setTitle(int resid) {
        tvTitle.setText(resid);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setContent(String text) {
        tvContent.setText(text);
    }

    public void setContent(int resid) {
        tvContent.setText(resid);
    }

    public void close() {
        dialog.dismiss();
    }

    public void show() {
        dialog.show();
    }

    public interface OnClickListener {
        void onConfirm();
    }

    public void setConfirmText(int resId) {
        btnConfirm.setText(resId);
    }

    public void setCancelText(int resId) {
        btnCancel.setText(resId);
    }
}
