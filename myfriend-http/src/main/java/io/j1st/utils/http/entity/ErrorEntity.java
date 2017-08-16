package io.j1st.utils.http.entity;

/**
 * Error Response
 */
@SuppressWarnings("unused")
public class ErrorEntity<T> {

    private String status = "fail";
    private int code;
    private String message;
    private T data;

    public ErrorEntity(int code, String lang) {
        this(code, lang, null, null);
    }

    public ErrorEntity(int code, String lang, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}