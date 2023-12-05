package com.platform.flex.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable {

    private static final long serialVersionUID = 2506788493356776767L;

    private String orderId;

}
