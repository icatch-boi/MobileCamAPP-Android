package com.icatch.mobilecam.data.CustomException;


public class NullPointerException extends Exception{
    private String exceptionType = "NullPointerException!";
    public NullPointerException() {
        super();
    }

    public NullPointerException(String tag,String describleInfo,String detailInfo) {
        super(describleInfo);
    }
}
