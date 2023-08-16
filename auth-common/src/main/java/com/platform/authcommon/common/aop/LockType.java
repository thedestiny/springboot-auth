package com.platform.authcommon.common.aop;

/**
 * @Description 分布式锁类型
 */
public enum LockType {

    /** 可重入锁*/
    REENTRANT_LOCK,

    /** 公平锁*/
    FAIR_LOCK,

    /** 读锁*/
    READ_LOCK,

    /** 写锁*/
    WRITE_LOCK
}
