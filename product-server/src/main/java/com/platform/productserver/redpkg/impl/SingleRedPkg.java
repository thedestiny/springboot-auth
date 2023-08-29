package com.platform.productserver.redpkg.impl;

import com.google.common.collect.Lists;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.redpkg.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 个人红包实现
 */
@Slf4j
@Service(value = "singleRedPkg")
public class SingleRedPkg extends AbstractRedPkgService implements RedPkgService {

    @Override
    public Boolean sendRedPkg(SendPkgReq pkgReq) {

        List<RedPkgNode> nodeList = Lists.newArrayList();
        String id = IdGenUtils.id();
        RedPkgNode node = new RedPkgNode();
        node.setAmount(pkgReq.getTotal());
        node.setOrderNo(pkgReq.getOrderNo());
        node.setId(id);
        // 存入缓存中
        saveRedPkg2Redis(pkgReq.getOrderNo(), nodeList);
        // 保存到数据库
        saveRedPkg2Db(pkgReq);
        return true;
    }

    @Override
    public Boolean receiveRedPkg(ReceivePkgReq pkgReq) {
        return super.receiveRedPkg(pkgReq);
    }
}
