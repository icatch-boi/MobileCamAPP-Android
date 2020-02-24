package com.icatch.mobilecam.data.CustomException;


public class DataFormatException extends Exception{
    public DataFormatException() {
        super();
    }

    public DataFormatException(String tag,String message) {
        super(message);
    }
}
