package com.drc.remiscarmini;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	 static final String ACTION = "android.intent.action.BOOT_COMPLETED"; 
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg1.getAction().equals(ACTION))
		{
			Intent remisCarRun = new Intent(arg0,DetailActivity.class);
			remisCarRun.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(remisCarRun);
		}

	}

}
