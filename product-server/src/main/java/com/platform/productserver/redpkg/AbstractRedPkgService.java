package com.platform.productserver.redpkg;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.platform.authcommon.common.AccountTypeEnum;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.common.ResultCode;
import com.platform.authcommon.common.TransTypeEnum;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.PkgInLog;
import com.platform.productserver.entity.PkgOutLog;
import com.platform.productserver.mapper.PkgInLogMapper;
import com.platform.productserver.mapper.PkgOutLogMapper;
import com.platform.productserver.service.AccountService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-15 10:44 AM
 */
public abstract class AbstractRedPkgService implements RedPkgService {

    @Autowired
    private AccountService accountService;
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

        PkgOutLog out = buildPkgOutLog(pkgReq);
        Integer result = outLogMapper.insert(out); // 保存发送红包信息
        if (result <= 0) {
            throw new AppException(ResultCode.FAILED, "红包发送失败！");
        }
        // 调用 C 端 出账接口
        tradeAccountOut(pkgReq);
        // 修改红包出账记录
        out.setStatus(1);
        int i = outLogMapper.updateById(out);
        return SqlHelper.retBool(i);

    }

    private PkgOutLog buildPkgOutLog(SendPkgReq pkgReq) {
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
        return out;
    }

    /**
     * 调用 出账接口
     * @param pkgReq
     */
    private void tradeAccountOut(SendPkgReq pkgReq) {
        TradeDto tradeDto = new TradeDto();
        tradeDto.setUserId(pkgReq.getSenderId());
        tradeDto.setAccountType(AccountTypeEnum.INNER.getCode());
        tradeDto.setAmount(pkgReq.getTotal());
        tradeDto.setRequestNo(IdGenUtils.orderNo());
        tradeDto.setOrderNo(pkgReq.getOrderNo());
        tradeDto.setOtherAccount("");
        tradeDto.setOtherAccountType(0);
        tradeDto.setProdType(pkgReq.getProdType());
        tradeDto.setTransType(TransTypeEnum.TRANS_OUT.getCode());
        tradeDto.setSource(pkgReq.getSource());
        tradeDto.setRemark(pkgReq.getRemark());
        tradeDto.setAppId(pkgReq.getAppId());
        tradeDto.setCredit(false);
        // 调用C端出账接口
        boolean trade = accountService.trade(tradeDto);
        if (!trade) {
            throw new AppException(ResultCode.FAILED, "红包发送失败！");
        }
    }


    /**
     * 保存红包数据到缓存
     */
    public boolean saveRedPkg2Redis(String orderNo, List<RedPkgNode> nodeList) {
        String key = Constant.RED_PKG_PREFIX + orderNo;
        // 保存红包列表到 redis 缓存中,过期时间 24h + 1分钟
        List<String> dataList = Lists.newArrayList();
        for (RedPkgNode node : nodeList) {
            dataList.add(JSONObject.toJSONString(node));
        }
        redisClient.listLeftPushAll(key, dataList);
        redisClient.expire(key, 24 * 60 * 60 + 60);
        return true;
    }


    /**
     * 红包领取
     */
    public Boolean receiveRedPkg(ReceivePkgReq pkgReq) {
        // 获取红包的 key
        String key = Constant.RED_PKG_PREFIX + pkgReq.getOrderNo();
        Object value = redisClient.listLeftPop(key);
        if (ObjectUtil.isEmpty(value)) {
            throw new AppException("红包不存在或已过期!");
        }
        // 弹出单个红包
        RedPkgNode redPkgNode = JSONObject.parseObject(value.toString(), RedPkgNode.class);
        // 判断红包是否存在
        if (ObjectUtil.isEmpty(redPkgNode)) {
            throw new AppException("红包不存在或已过期!");
        }
        // 查询发红包记录
        PkgOutLog outLog = outLogMapper.selectByOrderNo(pkgReq.getOrderNo());
        if (ObjectUtil.isEmpty(outLog)) {
            throw new AppException("红包不存在或已过期!");
        }
        // 红包类型 100 个人红包  101 群红包
        String prodType = outLog.getProdType();
        PkgInLog inLog = buildPkgInLog(pkgReq, redPkgNode, outLog);
        // 保存红包领取记录
        inLogMapper.insert(inLog);
        // 调用 C 端入账接口
        tradeAccountIn(pkgReq, key, redPkgNode, outLog);
        // 入账成功修改领取记录并发红包记录
        inLog.setStatus(1);
        inLogMapper.updateById(inLog);

        return true;
    }

    /**
     * 调用账户入账
     */
    private void tradeAccountIn(ReceivePkgReq pkgReq, String key, RedPkgNode redPkgNode, PkgOutLog outLog) {
        TradeDto tradeDto = new TradeDto();
        tradeDto.setUserId(pkgReq.getReceiverId());
        tradeDto.setAccountType(AccountTypeEnum.INNER.getCode());
        tradeDto.setAmount(redPkgNode.getAmount());
        tradeDto.setRequestNo(IdGenUtils.orderNo());
        tradeDto.setOrderNo(pkgReq.getOrderNo());
        tradeDto.setOtherAccount(outLog.getUserId());
        tradeDto.setOtherAccountType(AccountTypeEnum.INNER.getCode());
        tradeDto.setProdType(pkgReq.getProdType());
        tradeDto.setTransType(TransTypeEnum.TRANS_IN.getCode());
        tradeDto.setSource(pkgReq.getSource());
        tradeDto.setRemark(pkgReq.getRemark());
        tradeDto.setAppId(pkgReq.getAppId());
        // 调用C端出账接口
        boolean trade = accountService.trade(tradeDto);
        if (!trade) {
            //  如果处理失败，需要将红包放回到 redis 中 红包放入缓存池子中
            redisClient.listLeftPush(key, JSONObject.toJSONString(redPkgNode));
            throw new AppException(ResultCode.FAILED, "红包领取失败");
        }
    }

    @NotNull
    private PkgInLog buildPkgInLog(ReceivePkgReq pkgReq, RedPkgNode redPkgNode, PkgOutLog outLog) {
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
        return inLog;
    }

}
