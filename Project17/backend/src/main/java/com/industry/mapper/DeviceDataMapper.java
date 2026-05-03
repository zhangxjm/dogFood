package com.industry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.industry.entity.DeviceData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DeviceDataMapper extends BaseMapper<DeviceData> {

    @Select("SELECT * FROM device_data WHERE device_id = #{deviceId} ORDER BY create_time DESC LIMIT #{limit}")
    List<DeviceData> selectRecentData(Long deviceId, int limit);

    @Select("SELECT d.* FROM device_data d INNER JOIN (SELECT device_id, MAX(create_time) as max_time FROM device_data GROUP BY device_id) latest ON d.device_id = latest.device_id AND d.create_time = latest.max_time")
    List<DeviceData> selectLatestData();
}
