<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

<#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="org.mybatis.caches.ehcache.LoggingEhcache"/>

</#if>
<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
    </resultMap>

</#if>
<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
<#list table.commonFields as field>
        ${field.columnName},
</#list>
        ${table.fieldNames}
    </sql>

</#if>


    <insert id="insertEntityList" parameterType="arraylist">
        INSERT INTO ${table.name} (<#list table.fields as field>${field.name}<#if field_has_next>,</#if></#list>)
        VALUES
        <foreach collection="list" item="e" separator=",">
            (<#list table.fields as field>${r'#{e.'}${field.propertyName}${r"}"}<#if field_has_next>,</#if></#list>)
        </foreach>
    </insert>

    <insert id="insertEntity" parameterType="${package.Entity}.${table.entityName}">
        INSERT INTO ${table.name} (<#list table.fields as field>${field.name}<#if field_has_next>,</#if></#list>)
        VALUES
        (<#list table.fields as field>${r'#{'}${field.propertyName}${r"}"}<#if field_has_next>,</#if></#list>)
    </insert>

    <insert id="updateByEntityId" parameterType="${package.Entity}.${table.entityName}">
        UPDATE ${table.name}
        <set>
            <#list table.fields as field>
                 <#if !field.keyFlag ><#--生成主键排在第一位-->
            <if test="${field.propertyName} != null<#if field.columnType.type = 'String'> and  ${field.propertyName} != ''</#if>">
                ${field.name} = ${r'#{'}${field.propertyName}${r"}"} <#if field_has_next>,</#if>
            </if>
                 </#if>
            </#list>
        </set>
        <#list table.fields as field>
            <#if field.keyFlag><#--生成主键排在第一位-->
         where  ${field.name} = ${r'#{'}${field.propertyName}${r"}"}
            </#if>
        </#list>
    </insert>


    <select id="selectEntityById" resultMap="BaseResultMap">

        select
        <include refid="Base_Column_List"/>
        from ${table.name}
        where
         <#list table.fields as field>
             <#if field.keyFlag><#--生成主键排在第一位-->
        ${field.name} = ${r'#{'}${field.propertyName}${r"}"}
             </#if>
         </#list>

    </select>

    <select id="queryEntityList" resultMap="BaseResultMap">

        select
        <include refid="Base_Column_List"/>
        from ${table.name}
        <where>
             <#list table.fields as field>
                      <#if !field.keyFlag><#--生成主键排在第一位-->
         <if test="${field.propertyName} != null<#if field.columnType.type = 'String'> and ${field.propertyName} != '' </#if>">
             and ${field.name} = ${r'#{'}${field.propertyName}${r"}"} <#if field_has_next></#if>
         </if>
                      </#if>
             </#list>

        </where>


    </select>






</mapper>
