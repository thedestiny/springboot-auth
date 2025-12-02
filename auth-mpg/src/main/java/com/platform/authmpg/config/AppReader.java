package com.platform.authmpg.config;

import lombok.Data;

import java.io.Serializable;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-11-18 6:30 PM
 */

@Data
public class AppReader implements ItemReader<String> {

    private String[] message = {"ming", "mingming", "mingmingming"};

    private int count = 0;

    @Override
    public String read()  {
        if(count < message.length){
            return message[count++];
        }else{
            count = 0;
        }
        return "";
    }
}
