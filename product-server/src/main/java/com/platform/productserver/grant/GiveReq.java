package com.platform.productserver.grant;

import com.platform.authcommon.base.BaseReq;
import com.platform.productserver.dto.UserDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 积分分发请求-单笔
 * @Date 2023-09-07 4:37 PM
 */

@Data
@ApiOperation(value = "积分分发-单笔请求")
public class GiveReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 7016044287596558311L;

    @ApiModelProperty(value = "交易订单号")
    @NotBlank(message = "交易订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    @ApiModelProperty("活动编号")
    private String activityNo;

    @ApiModelProperty(value = "出账商户号")
    @NotBlank(message = "出账商户号不能为空")
    private String merchantNo;

    @ApiModelProperty(value = "出账账户编号")
    @NotBlank(message = "出账账户编号不能为空")
    private String outAccNo;

    @ApiModelProperty(value = "出账账户类型")
    @NotNull(message = "出账账号类型不能为空")
    private Integer outAccNoType;

    @ApiModelProperty(value = "入账账号编号")
    @NotBlank(message = "入账账号不能为空")
    private String userId;

    @ApiModelProperty(value = "入账账号类型")
    @NotNull(message = "入账账号类型不能为空")
    private Integer inAccNoType;

    @ApiModelProperty(value = "分发金额")
    @NotNull(message = "分发金额不能为空")
    @Digits(integer = 14, fraction = 2, message = "分发金额不合法")
    private BigDecimal amount;

    @ApiModelProperty(value = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String prodType;

    @NotBlank(message = "业务系统编号不能为空")
    @ApiModelProperty("业务系统编号")
    private String appId;

    @ApiModelProperty("积分到期时间 格式：yyyy-MM-dd")
    private String expireTime;

    @ApiModelProperty("渠道信息")
    private String channel;

    public static void main(String[] args) {


        Map<String, Long> map = new HashMap<>();


        List<UserDto> ids = new ArrayList<>();
        UserDto dto = new UserDto();
        dto.setUsername("123");
        dto.setId(34L);

        UserDto dto1 = new UserDto();
        dto1.setUsername("123");
        dto1.setId(34L);
        ids.add(dto);
        ids.add(dto1);

        // compute 的用法
        for (UserDto id : ids) {
            map.compute(id.getUsername(), (k, v) -> (v == null ? 0 : v) + id.getId());
            map.computeIfAbsent(id.getUsername(), k -> map.getOrDefault(k, 0L));
            map.computeIfPresent(id.getUsername(), (k, v) -> (v == null ? 0 : v) + id.getId());
        }

        System.out.println(map.get("123"));


    }


}
