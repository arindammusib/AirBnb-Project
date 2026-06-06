package com.AirBnd.AirBnB_backend.exceptions;

public class UnAuthorisedException extends RuntimeException{
    public UnAuthorisedException(String message){
        super(message);
    }
}
