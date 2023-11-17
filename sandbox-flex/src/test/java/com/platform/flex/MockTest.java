package com.platform.flex;

import com.platform.flex.entity.Student;
import com.platform.flex.utils.TestStastic;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @Description
 * @Author liangwenchao
 * @Date 2023-11-15 2:36 PM
 */

@Slf4j
public class MockTest {


    @Test
    public void test01() {

        log.info("start test !");

        TestStastic sta = new TestStastic();

        Student student = new Student();
        sta.test(student);


    }


}
