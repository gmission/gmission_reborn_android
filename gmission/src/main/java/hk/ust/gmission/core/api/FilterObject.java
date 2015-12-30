package hk.ust.gmission.core.api;

/**
 * Created by rui on 14-5-5.
 */
public class FilterObject {
    private String name;
    private String op;
    private String val;

    public FilterObject(String name, String op, String val) {
        this.name = name;
        this.op = op;
        this.val = val;
    }
}
