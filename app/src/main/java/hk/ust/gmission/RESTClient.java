package hk.ust.gmission;

/**
 * Created by rui on 14-4-29.
 */
public class RESTClient {


    private final static String URL_BASE = "http://lccpu3.cse.ust.hk/gmission-dev";
    private final static String URL_REST = "/rest";
    private final static String URL_IMAGE = "/image";
    private final static String URL_VIDEO = "/video";
    private final static String URL_AUDIO = "/audio";
    public  final static String URL_IMAGE_ORI = URL_BASE + URL_IMAGE + "/original";
    public  final static String URL_VIDEO_ORI = URL_BASE + URL_VIDEO + "/original";
    public  final static String URL_VIDEO_THUMB = URL_BASE + URL_VIDEO + "/thumb";
    public  final static String URL_AUDIO_ORI = URL_BASE + URL_AUDIO + "/original";

    public  final static String URL_AUTH = URL_BASE + "/user/auth";
    public  final static String URL_REG = URL_BASE + "/user/register";



}
