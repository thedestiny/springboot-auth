package ${package.Mapper};

import ${package.Entity}.${table.entityName};
import ${superMapperClassPackage};
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${table.entityName}> {


   /**
     * 批量插入
     * */
   Integer insertEntityList(List<${table.entityName}> entityList);

}
</#if>
