package ${package.Service};

import ${package.Entity}.${entity};

/**
 * ${table.comment!} 服务类
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} {

       /**
          新增数据
       */
       Integer insert(${entity} entity);

      /**
          更新数据
       */
       Integer updateById(${entity} entity);
       /**
          查询单条数据
       */
    ${entity} queryById(Long id);




}
</#if>
