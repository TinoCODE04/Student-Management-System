package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 专业Mapper接口
 */
@Mapper
public interface MajorMapper extends BaseMapper<Major> {
    
    /**
     * 根据学院ID查询专业列表
     */
    @Select("SELECT * FROM major WHERE college_id = #{collegeId}")
    List<Major> selectByCollegeId(Long collegeId);
}
