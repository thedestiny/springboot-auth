package com.platform.productserver.redpkg;

/**
 * 红包方法和领取业务
 */
public interface RedPkgService {

    /**
     * 红包发送
     */
    Boolean sendRedPkg(SendPkgReq pkgReq);

    /**
     * 红包领取
     */
    Boolean receiveRedPkg(ReceivePkgReq pkgReq);
}
