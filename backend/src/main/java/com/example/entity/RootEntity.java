package com.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RootEntity<T> {

    private String result;

    private int statusCode;

    private T data;

    private T errors;


    public static <T> RootEntity<T> success(T data, HttpStatus httpStatus){
        RootEntity<T> rootEntity = new RootEntity<>();
        rootEntity.setData(data);
        rootEntity.setErrors(null);
        rootEntity.setResult("Successfull");
        rootEntity.setStatusCode(httpStatus.value());
        return rootEntity;
    }

    public static <T> RootEntity<T> error(T errorMessage, HttpStatus httpStatus){
        RootEntity<T> rootEntity = new RootEntity<>();
        rootEntity.setData(null);
        rootEntity.setErrors(errorMessage);
        rootEntity.setResult("Error");
        rootEntity.setStatusCode(httpStatus.value());
        return rootEntity;
    }



}

