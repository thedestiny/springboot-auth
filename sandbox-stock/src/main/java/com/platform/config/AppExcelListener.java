package com.platform.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 读取 excel
 */
@Slf4j
public class AppExcelListener<T> extends AnalysisEventListener<T> {

    // 创建list集合封装最终的数据
    private List<T> list = new ArrayList<>();
    public List<T> getList() {
        return list;
    }

    //一行一行去读取excle内容
    @Override
    public void invoke(T user, AnalysisContext context) {
        // log.info(" invoke entity {}", user);
        list.add(user);
    }

    //读取excel表头信息
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info(" 表头信息 {}", headMap);
    }

    //读取完成后执行
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
