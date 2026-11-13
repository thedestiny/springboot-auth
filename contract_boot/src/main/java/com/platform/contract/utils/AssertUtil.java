package com.platform.contract.utils;

import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class AssertUtil {

    public static void notTrue(boolean bool, ApiCodeEnum apiCodeEnum, String memo, Object... params) throws ApiException {
        if (!bool) {
            throw new ApiException(apiCodeEnum, memo, params);
        }
    }

    public static void isTrue(boolean bool, ApiCodeEnum apiCodeEnum, String memo, Object... params) throws ApiException {
        if (bool) {
            throw new ApiException(apiCodeEnum, memo, params);
        }
    }

    public static void notEmpty(Object object, ApiCodeEnum apiCodeEnum, String memo, Object... params) throws ApiException {
        if (!ObjectUtils.isEmpty(object)) {
            throw new ApiException(apiCodeEnum, memo, params);
        }
    }

    public static void empty(Object object, ApiCodeEnum apiCodeEnum, String memo, Object... params) throws ApiException {
        if (ObjectUtils.isEmpty(object)) {
            throw new ApiException(apiCodeEnum, memo, params);
        }
    }

    public static void notEquals(String arg0, String arg1, ApiCodeEnum apiCodeEnum, String memo, Object... params) throws ApiException {
        if (StringUtils.hasLength(arg0) && StringUtils.hasLength(arg1)) {
            if (!StringUtils.pathEquals(arg0, arg1)) {
                throw new ApiException(apiCodeEnum, memo, params);
            }
        } else {
            throw new ApiException(apiCodeEnum, memo, params);
        }
    }

}
