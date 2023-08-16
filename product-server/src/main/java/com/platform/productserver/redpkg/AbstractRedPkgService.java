package com.platform.productserver.redpkg;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.config.RedisUtils;
import com.platform.productserver.entity.PkgOutLog;
import com.platform.productserver.mapper.PkgInLogMapper;
import com.platform.productserver.mapper.PkgOutLogMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-15 10:44 AM
 */
public abstract class AbstractRedPkgService implements RedPkgService {

    @Autowired
    private PkgInLogMapper inLogMapper;
    @Autowired
    private PkgOutLogMapper outLogMapper;
    @Autowired
    private RedisUtils redisClient;

    /**
     * 保存红包发送数据到数据库
     */
    public boolean saveRedPkg2Db(SendPkgReq pkgReq) {

        PkgOutLog out = new PkgOutLog();
        out.setSource(pkgReq.getSource());
        out.setAppId(pkgReq.getAppId());
        out.setOrderNo(pkgReq.getOrderNo());
        out.setProdType(pkgReq.getProdType());
        out.setUserId(pkgReq.getSenderId());
        out.setAmount(pkgReq.getTotal());
        out.setRemark(pkgReq.getRemark());
        // 获取明天此刻的时间
        out.setExpireTime(DateUtil.tomorrow());
        out.setFlag(0);
        out.setStatus(0);
        // 保存发送红包信息
        return SqlHelper.retBool(outLogMapper.insert(out));
    }

    /**
     * 保存红包数据到缓存
     */
    public boolean saveRedPkg2Redis(String orderNo, List<RedPkgNode> nodeList) {

        String value = JSONObject.toJSONString(nodeList);
        String key = Constant.RED_PKG_PREFIX + orderNo;
        // 保存红包列表到 redis 缓存中,过期时间 24h + 1分钟
        redisClient.valueSet(key, value, 24 * 60 * 60 + 60);
        return true;
    }


}
