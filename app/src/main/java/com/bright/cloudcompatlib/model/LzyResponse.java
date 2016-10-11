package com.bright.cloudcompatlib.model;

import java.io.Serializable;

public class LzyResponse<T> implements Serializable {

    public int code;
    public String msg;
    public T data;
}