package hk.ust.gmission.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by bigstone on 28/3/2016.
 */
public class DownloadUtils {
    public static Boolean downloadFileFromIO(String mGetFrom, String mWriteTo) throws Exception {
        File file = new File(mWriteTo);
        URL url = new URL(mGetFrom);
        long startTime = System.currentTimeMillis();
        Ln.d("DownloadAttachmentTask download beginning: " + mGetFrom);
        //Open a connection to that URL.
        URLConnection ucon = url.openConnection();

        //this timeout affects how long it takes for the app to realize there's a connection problem
        ucon.setReadTimeout(5000);
        ucon.setConnectTimeout(30000);
        //Define InputStreams to read from the URLConnection.
        // uses 3KB download buffer
        InputStream is = ucon.getInputStream();
        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buff = new byte[5 * 1024];
        //Read bytes (and store them) until there is nothing more to read(-1)
        int len;
        while ((len = inStream.read(buff)) != -1) {
            outStream.write(buff, 0, len);
        }
        //clean up
        outStream.flush();
        outStream.close();

        inStream.close();
        Ln.d("DownloadAttachmentTask download completed in "
                + ((System.currentTimeMillis() - startTime) / 1000)
                + " sec");
        return true;
    }
}
