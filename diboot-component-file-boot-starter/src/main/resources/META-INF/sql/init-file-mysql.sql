CREATE TABLE `file` (
  `uuid` varchar(32) NOT NULL COMMENT '编号',
  `rel_obj_type` varchar(50) DEFAULT NULL COMMENT '关联对象类型',
  `rel_obj_id` int(11) DEFAULT NULL COMMENT '关联对象ID',
  `name` varchar(100) NOT NULL COMMENT '文件名',
  `link` varchar(255) DEFAULT NULL COMMENT '文件链接',
  `path` varchar(255) NOT NULL COMMENT '路径',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型',
  `data_count` smallint(5) DEFAULT '0' COMMENT '数据量',
  `size` int(11) DEFAULT NULL COMMENT '文件大小',
  `status` char(1) NOT NULL DEFAULT 'S' COMMENT '保存状态',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件';