package hk.ust.gmission.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hk.ust.gmission.R;

/**
 * Created by bigstone on 23/3/2016.
 */
public class PlyUtil {

    public static File getPlyFile(Context context, String fileName) {
        String state = Environment.getExternalStorageState();
        File path = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.ply_folder));
        } else {
            path = new File(Environment.getDataDirectory(), context.getString(R.string.ply_folder));
        }


        if (!path.exists()) {
            boolean result = path.mkdirs();

            if (result == false){
                return null;
            }

        }

        return new File(path, fileName);
    }


}
