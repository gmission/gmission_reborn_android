package hk.ust.gmission.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -7495897652017488896L;

    @Expose
    @SerializedName("id") protected int id;
    @Expose @SerializedName("name") protected String name;
    @Expose @SerializedName("res") protected int res;
    @Expose @SerializedName("expire") protected String expire;
    @Expose @SerializedName("token") protected String token;
    @Expose @SerializedName("email") protected String email;
    @Expose @SerializedName("credit") protected int credit;

    private int reuqestCnt = 0;
    private int answerCnt = 0;
//    private CheckIn indoorCheckIn;
//    private CheckIn globalCheckIn;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getReuqestCnt() {
        return reuqestCnt;
    }

    public void setReuqestCnt(int reuqestCnt) {
        this.reuqestCnt = reuqestCnt;
    }

    public int getAnswerCnt() {
        return answerCnt;
    }

    public void setAnswerCnt(int answerCnt) {
        this.answerCnt = answerCnt;
    }
}
