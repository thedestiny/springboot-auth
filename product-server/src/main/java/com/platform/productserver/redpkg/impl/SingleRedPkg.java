package com.platform.productserver.redpkg.impl;

import com.platform.productserver.redpkg.ReceivePkgReq;
import com.platform.productserver.redpkg.RedPkgService;
import com.platform.productserver.redpkg.SendPkgReq;
import lombok.extern.slf4j.Slf4j;

/**
 * 个人红包实现
 */
@Slf4j
public class SingleRedPkg implements RedPkgService {


    @Override
    public Boolean sendRedPkg(SendPkgReq pkgReq) {

        return null;
    }

    @Override
    public Boolean receiveRedPkg(ReceivePkgReq pkgReq) {
        return null;
    }
}
