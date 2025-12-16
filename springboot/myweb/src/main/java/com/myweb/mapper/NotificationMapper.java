package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
