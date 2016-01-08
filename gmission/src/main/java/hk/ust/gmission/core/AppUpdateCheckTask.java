package hk.ust.gmission.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import static com.github.kevinsawicki.http.HttpRequest.post;

/**
 * Created by bigstone on 8/1/2016.
 */
public class AppUpdateCheckTask extends AsyncTask<Void, Void, String> {
    private Context context;

    public AppUpdateCheckTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String url = "http://lccpu4.cse.ust.hk/indoorLocalizationService/checkgmission.php";

            return post(url).body();


        } catch (Exception e) {
            return null;
        }

    }
    @Override
    protected void onPostExecute(final String content) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersionNumber = info.versionCode;
        Log.i("version code", ""+currentVersionNumber);
        if (content == null){
            return;
        }
        int latestVersionNumber = Integer.valueOf(content);

        if (latestVersionNumber > currentVersionNumber){
            new AlertDialog.Builder(context)
                    .setTitle("New Version Found!")
                    .setMessage("Do you want to download the latest version?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("http://lccpu4.cse.ust.hk/indoorLocalizationService/gmission.apk"));
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

}

