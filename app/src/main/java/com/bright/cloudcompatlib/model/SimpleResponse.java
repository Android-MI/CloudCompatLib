package com.bright.cloudcompatlib.model;

import java.io.Serializable;

public class SimpleResponse implements Serializable {

    public int code;
    public String msg;

    public LzyResponse toLzyResponse() {
        LzyResponse lzyResponse = new LzyResponse();
        lzyResponse.code = code;
        lzyResponse.msg = msg;
        return lzyResponse;
    }
}