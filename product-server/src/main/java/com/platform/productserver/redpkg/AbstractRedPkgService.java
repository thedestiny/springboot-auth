package com.platform.productserver.redpkg;
import java.math.BigDecimal;
import java.util.Date;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.productserver.entity.PkgInLog;
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
        return SqlHelper.retBool(outLogMapper.insert(out)); // 保存发送红包信息
    }
    /**
     * 保存红包数据到缓存
     */
    public boolean saveRedPkg2Redis(String orderNo, List<RedPkgNode> nodeList) {
        String value = JSONObject.toJSONString(nodeList);
        String key = Constant.RED_PKG_PREFIX + orderNo;
        // 保存红包列表到 redis 缓存中,过期时间 24h + 1分钟
        // redisClient.valueSet(key, value, 24 * 60 * 60 + 60);
        // 使用 list 结构存储红包
        redisClient.listLeftPushAll(key, nodeList);

        return true;
    }

    /**
     * 红包领取
     * @param pkgReq
     * @return
     */
    public Boolean receiveRedPkg(ReceivePkgReq pkgReq){

        // 获取红包的 key
        String key = Constant.RED_PKG_PREFIX + pkgReq.getOrderNo();
        Object value = redisClient.valueGet(key);
        List<RedPkgNode> nodes = JSONObject.parseArray(value.toString(), RedPkgNode.class);
        // 判断红包是否存在
        if(CollUtil.isEmpty(nodes)){
            throw new AppException("红包不存在或已过期!");
        }
        // 查询发红包记录
        PkgOutLog outLog = outLogMapper.selectByOrderNo(pkgReq.getOrderNo());
        if(ObjectUtil.isEmpty(outLog)){
            throw new AppException("红包不存在或已过期!");
        }
        // 红包类型 100 单个红包  101 拼手气红包
        String prodType = outLog.getProdType();

        RedPkgNode redPkgNode = nodes.get(0);
        PkgInLog inLog = new PkgInLog();
        inLog.setFid(outLog.getId());
        inLog.setSource(pkgReq.getSource());
        inLog.setRequestNo(pkgReq.getRequestNo());
        inLog.setOrderNo(pkgReq.getOrderNo());
        inLog.setUserId(pkgReq.getReceiverId());
        inLog.setAmount(redPkgNode.getAmount());
        inLog.setStatus(0);
        inLog.setErrorMsg("");
        inLog.setProdType("");
        inLog.setActionType(0);
        inLog.setRemark(pkgReq.getRemark());
        // 保存红包领取记录
        int insert = inLogMapper.insert(inLog);
        // 调用 C 端入账接口



        // 入账成功修改领取记录并发红包记录
        inLogMapper.updateById(inLog);


        if(StrUtil.equals("100", prodType)){


        }

        if(StrUtil.equals("101", prodType)){
            // 获取第一个红包

        }
        outLogMapper.updateById(outLog);

        return true;
    }

}
