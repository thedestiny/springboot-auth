package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ${table.comment!} 服务实现类
 * @author ${author}
 * @since ${date}
 */
@Slf4j
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} implements ${table.serviceName} {

     @Autowired
     private ${table.mapperName} baseMapper;

     @Override
     public Integer insert(${entity} entity) {
          return baseMapper.insertEntity(entity);
     }

     @Override
     public Integer updateById(${entity} entity) {
          return baseMapper.updateByEntityId(entity);
     }

     @Override
     public ${entity} queryById(Long id) {
          return baseMapper.selectEntityById(id);
     }






}
</#if>
