package com.platform.messsage.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class MsgDto implements Serializable {

    private static final long serialVersionUID = 1662899537386034320L;

    private String code;

    private String message;

    private String date;

    private String data;




}
