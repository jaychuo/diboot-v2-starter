CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` varchar(100) NOT NULL DEFAULT 'SMS' COMMENT '消息类型',
  `template_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '模板ID',
  `business_type` varchar(100) DEFAULT NULL COMMENT '关联业务类型',
  `business_id` bigint(20) DEFAULT NULL COMMENT '关联业务ID',
  `sender` varchar(100) DEFAULT NULL COMMENT '发送人',
  `receiver` varchar(100) NOT NULL COMMENT '接收人',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `content` varchar(100) NOT NULL COMMENT '内容',
  `url` varchar(100) DEFAULT NULL COMMENT '链接',
  `status` varchar(100) NOT NULL DEFAULT 'NEW' COMMENT '发送状态',
  `schedule_time` timestamp NULL DEFAULT NULL COMMENT '计划时间',
  `response` varchar(100) DEFAULT NULL COMMENT '发送结果',
  `extdata` varchar(255) DEFAULT NULL COMMENT '扩展字段',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息';

CREATE TABLE `message_template` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `type` varchar(100) NOT NULL DEFAULT 'SMS' COMMENT '类型',
  `code` varchar(100) NOT NULL COMMENT '模板编码',
  `msg_type` varchar(100) DEFAULT NULL COMMENT '消息类型',
  `business_type` varchar(100) DEFAULT NULL COMMENT '适用业务类型',
  `business_id` bigint(20) DEFAULT NULL COMMENT '适用业务ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `msg_title` varchar(100) DEFAULT NULL COMMENT '消息标题',
  `content` varchar(100) NOT NULL COMMENT '内容',
  `extdata` varchar(255) DEFAULT NULL COMMENT '扩展字段',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息模板';
