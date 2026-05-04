package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.AttendanceStatistics;
import com.attendance.service.AttendanceStatisticsService;
import com.attendance.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 考勤统计管理 Controller
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class AttendanceStatisticsController {

    private final AttendanceStatisticsService attendanceStatisticsService;

    @GetMapping("/page")
    public Result<PageResult<AttendanceStatistics>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer statisticsYear,
            @RequestParam(required = false) Integer statisticsMonth) {
        LambdaQueryWrapper<AttendanceStatistics> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(AttendanceStatistics::getEmpId, empId);
        }
        if (statisticsYear != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsYear, statisticsYear);
        }
        if (statisticsMonth != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsMonth, statisticsMonth);
        }
        wrapper.orderByDesc(AttendanceStatistics::getStatisticsYear)
                .orderByDesc(AttendanceStatistics::getStatisticsMonth);
        
        Page<AttendanceStatistics> page = new Page<>(current, size);
        attendanceStatisticsService.page(page, wrapper);
        
        PageResult<AttendanceStatistics> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<AttendanceStatistics>> list(
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer statisticsYear,
            @RequestParam(required = false) Integer statisticsMonth) {
        LambdaQueryWrapper<AttendanceStatistics> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(AttendanceStatistics::getEmpId, empId);
        }
        if (statisticsYear != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsYear, statisticsYear);
        }
        if (statisticsMonth != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsMonth, statisticsMonth);
        }
        wrapper.orderByDesc(AttendanceStatistics::getStatisticsYear)
                .orderByDesc(AttendanceStatistics::getStatisticsMonth);
        List<AttendanceStatistics> list = attendanceStatisticsService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<AttendanceStatistics> getById(@PathVariable Long id) {
        AttendanceStatistics statistics = attendanceStatisticsService.getById(id);
        return Result.success(statistics);
    }

    @PostMapping
    public Result<AttendanceStatistics> save(@RequestBody AttendanceStatistics statistics) {
        attendanceStatisticsService.save(statistics);
        return Result.success("新增成功", statistics);
    }

    @PutMapping
    public Result<AttendanceStatistics> update(@RequestBody AttendanceStatistics statistics) {
        attendanceStatisticsService.updateById(statistics);
        return Result.success("修改成功", statistics);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        attendanceStatisticsService.removeById(id);
        return Result.success();
    }

    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) Integer statisticsYear,
            @RequestParam(required = false) Integer statisticsMonth) throws IOException {
        LambdaQueryWrapper<AttendanceStatistics> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(AttendanceStatistics::getEmpId, empId);
        }
        if (statisticsYear != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsYear, statisticsYear);
        }
        if (statisticsMonth != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsMonth, statisticsMonth);
        }
        wrapper.orderByDesc(AttendanceStatistics::getStatisticsYear)
                .orderByDesc(AttendanceStatistics::getStatisticsMonth);
        
        List<AttendanceStatistics> list = attendanceStatisticsService.list(wrapper);

        String[] headers = {"员工ID", "年份", "月份", "应出勤天数", "实际出勤天数", 
                           "迟到天数", "早退天数", "旷工天数", "请假天数", "加班时长(小时)"};
        
        ExcelUtil.export(response, "考勤月统计列表", "统计数据", headers, list, stat -> {
            return Arrays.asList(
                    stat.getEmpId(),
                    stat.getStatisticsYear(),
                    stat.getStatisticsMonth(),
                    stat.getWorkDays(),
                    stat.getActualDays(),
                    stat.getLateDays(),
                    stat.getEarlyDays(),
                    stat.getAbsentDays(),
                    stat.getLeaveDays(),
                    stat.getOvertimeHours()
            );
        });
    }
}
