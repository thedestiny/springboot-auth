package com.platform.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.contract.entity.UserMaterial;
import org.apache.ibatis.annotations.Param;

public interface UserMaterialMapper extends BaseMapper<UserMaterial> {

    UserMaterial selectUserMaterialByUserIdAndType(@Param("userId") String userId, @Param("accountType") String accountType);

}
