package com.platform.productserver.redpkg;

import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.common.Constant;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-15 10:44 AM
 */
public abstract class AbstractRedPkgService implements RedPkgService {



    /**
     * 保存红包发送数据到数据库
     *
     * @return
     */
    public boolean saveRedPkg2Db(SendPkgReq pkgReq) {



        return true;
    }

    /**
     * 保存红包数据到缓存
     *
     * @return
     */
    public boolean saveRedPkg2Redis(String orderNo, List<RedPkgNode> nodeList) {

        String value = JSONObject.toJSONString(nodeList);
        String key = Constant.RED_PKG_PREFIX + orderNo;

        return true;
    }


}
