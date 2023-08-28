package com.platform.productserver.business;

import cn.hutool.core.util.StrUtil;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.common.aop.annotation.DistributedLock;
import com.platform.authcommon.exception.AppException;
import com.platform.productserver.entity.PkgOutLog;
import com.platform.productserver.mapper.PkgOutLogMapper;
import com.platform.productserver.redpkg.ReceivePkgReq;
import com.platform.productserver.redpkg.RedPkgEnum;
import com.platform.productserver.redpkg.RedPkgService;
import com.platform.productserver.redpkg.SendPkgReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 * @Description 红包业务
 * @Date 2023-08-17 9:41 AM
 */
@Service
public class RedPkgBusiness {


    @Autowired
    private Map<String, RedPkgService> redPkgServiceMap;
    @Autowired
    private PkgOutLogMapper outLogMapper;


    /**
     * 红包发送
     * @param req
     */
    @DistributedLock(prefix = "redis-red-send", key = "#req.orderNo")
    public boolean sendRedPkg(SendPkgReq req) {
        // 红包类型 1-个人红包 2-群红包平分模式 2群红包拼手气
        Integer redType = req.getRedType();
        RedPkgEnum redPkgEnum = RedPkgEnum.queryPkgByType(redType);
        if(redPkgEnum == null){
            throw new AppException("红包类型不存在");
        }
        RedPkgService redPkgService = redPkgServiceMap.get(redPkgEnum.name);
        Boolean redPkg = redPkgService.sendRedPkg(req);
        return redPkg;
    }

    @DistributedLock(prefix = "redis-red-receive", key = "#req.requestNo")
    public boolean receiveRedPkg(ReceivePkgReq req) {
        // 红包类型 1-个人红包 2-群红包平分模式 2群红包拼手气
        PkgOutLog pkgOutLog = outLogMapper.selectByOrderNo(req.getOrderNo());
        String prodType = pkgOutLog.getProdType();
        if(StrUtil.equalsAny(prodType, "100", "101")){
            throw new AppException("红包类型不存在");
        }
        RedPkgEnum redPkgEnum = RedPkgEnum.queryPkgByCode(prodType);
        RedPkgService redPkgService = redPkgServiceMap.get(redPkgEnum.name);
        Boolean redPkg = redPkgService.receiveRedPkg(req);
        return redPkg;
    }
}
