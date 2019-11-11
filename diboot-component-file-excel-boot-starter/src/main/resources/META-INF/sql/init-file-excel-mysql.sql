CREATE TABLE `excel_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_class` varchar(50) NOT NULL COMMENT 'Java对象类',
  `model_field` varchar(50) NOT NULL COMMENT 'Java对象属性',
  `col_name` varchar(50) NOT NULL COMMENT '列标题',
  `col_index` int(5) NOT NULL COMMENT '列索引',
  `data_type` varchar(20) DEFAULT NULL COMMENT '数据类型',
  `validation` varchar(100) DEFAULT NULL COMMENT '校验',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `excel_import_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
  `file_uuid` varchar(32) DEFAULT NULL COMMENT '文件ID',
  `rel_obj_type` varchar(100) DEFAULT NULL COMMENT '关联类型',
  `rel_obj_id` int(11) unsigned DEFAULT NULL COMMENT '关联ID',
  `rel_obj_uid` varchar(32) DEFAULT NULL COMMENT '关联UUID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='导入记录';
