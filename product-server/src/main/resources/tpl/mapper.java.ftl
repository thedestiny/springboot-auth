package ${package.Mapper};

import ${package.Entity}.${entity};
<#if mybatisPlusFlag>
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
</#if>
<#--import com.baomidou.mybatisplus.core.mapper.BaseMapper;-->

import java.util.List;
/**
 * ${table.comment!} Mapper 接口
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
<#--public interface ${table.mapperName} {-->
public interface ${table.mapperName} <#if mybatisPlusFlag > extends BaseMapper<${table.entityName}> </#if> {

   /**
     * 批量插入
     * */
   Integer insertEntityList(List<${table.entityName}> entityList);

      /**
     * 新增数据
     * */
   Integer insertEntity(${table.entityName} entity);

      /**
     * 更新数据
     * */
   Integer updateByEntityId(${table.entityName} entity);

   /**
     * 查询数据
     * */
    ${table.entityName} selectEntityById(Long id);

     /**
     * 查询多条数据
     * */
   List<${table.entityName}> queryEntityList(${table.entityName} entity);




}
</#if>
