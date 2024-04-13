
CREATE TABLE `tb_menu` (
   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
   `name` varchar(64) NOT NULL COMMENT '菜单名称',
   `seq` int NULL COMMENT '序号',
   `parent_id` int NULL COMMENT '父级id',
   `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
   `update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';


