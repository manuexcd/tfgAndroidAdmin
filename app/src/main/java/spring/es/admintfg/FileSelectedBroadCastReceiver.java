package spring.es.admintfg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileSelectedBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
         Uri filePath = intent.getParcelableExtra(com.aditya.filebrowser.Constants.BROADCAST_SELECTED_FILE);
    }
}