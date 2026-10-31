package com.example.entity;

import lombok.Data;
import java.util.Date;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError<T> {

    private String id;

    private Date errorTime;

    private String path;

    private T errorMessages;
}