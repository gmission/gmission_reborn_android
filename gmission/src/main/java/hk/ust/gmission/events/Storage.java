package hk.ust.gmission.events;

import android.content.Context;
import android.util.Log;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import hk.ust.gmission.BootstrapApplication;
import hk.ust.gmission.models.dao.User;

/**
 * Created by rui on 14-2-10.
 */
public class Storage implements Serializable {
    public static User user;


    private static final String user_fileName = "cache_user.dat";



    public static User getUser(){
        if(user == null) loadUser();
        return user;
    }

    public static void writeUser(){
        Context context = BootstrapApplication.getInstance().getApplicationContext();
        if(context!=null){
            Log.i("BACKUP", "DOING BACKUP!");
            FileOutputStream stream = null;
            try{
                if(user!=null){
                    Log.i("BACKUP","DOING user!");
                    stream = context.openFileOutput(user_fileName, Context.MODE_PRIVATE);
                    ObjectOutputStream dout = new ObjectOutputStream(stream);
                    dout.writeObject(user);
                    dout.flush();
                    stream.getFD().sync();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void loadUser(){
        Context context = BootstrapApplication.getInstance().getApplicationContext();
        if(context!=null){
            Log.i("BACKUP","LOADING BACKUP!");
            FileInputStream stream = null;
            try{
                Log.i("BACKUP","DOING tasks!");
                stream = context.openFileInput(user_fileName);
                ObjectInputStream input = new ObjectInputStream(stream);
                user = (User)input.readObject();
                stream.getFD().sync();

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
