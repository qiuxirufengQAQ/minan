-- 添加微信登录相关字段
ALTER TABLE `user` 
ADD COLUMN `wechat_openid` VARCHAR(128) DEFAULT NULL COMMENT '微信 openid' AFTER `password`,
ADD COLUMN `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称' AFTER `wechat_openid`,
ADD COLUMN `total_score` INT DEFAULT 0 COMMENT '总积分' AFTER `level`,
ADD COLUMN `completed_scenes` INT DEFAULT 0 COMMENT '已完成场景数' AFTER `total_score`;

-- 添加索引
CREATE INDEX `idx_wechat_openid` ON `user`(`wechat_openid`);

-- 添加 total_score 和 completed_scenes 到现有用户（如果有数据）
UPDATE `user` SET `total_score` = 0, `completed_scenes` = 0 WHERE `total_score` IS NULL;
