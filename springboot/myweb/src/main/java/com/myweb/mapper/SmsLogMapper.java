package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.SmsLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信日志Mapper
 */
@Mapper
public interface SmsLogMapper extends BaseMapper<SmsLog> {
}
