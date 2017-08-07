package xiaopeng666.top.entity;

/**
 * TulingRoot
 */

import java.util.List;

public class TulingRoot {
    private int code;

    private String text;

    private List<Tuling> list;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setList(List<Tuling> list) {
        this.list = list;
    }

    public List<Tuling> getList() {
        return this.list;
    }

}