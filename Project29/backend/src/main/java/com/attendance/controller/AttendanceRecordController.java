package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.AttendanceRecord;
import com.attendance.entity.EmpEmployee;
import com.attendance.service.AttendanceRecordService;
import com.attendance.service.EmpEmployeeService;
import com.attendance.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 考勤记录管理 Controller
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordService attendanceRecordService;
    private final EmpEmployeeService empEmployeeService;

    @Value("${attendance.work-time.morning-start}")
    private String morningStart;
    @Value("${attendance.work-time.afternoon-end}")
    private String afternoonEnd;
    @Value("${attendance.work-time.late-minutes}")
    private Integer lateMinutes;
    @Value("${attendance.work-time.early-minutes}")
    private Integer earlyMinutes;

    @GetMapping("/page")
    public Result<PageResult<AttendanceRecord>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer isLate,
            @RequestParam(required = false) Integer isEarly,
            @RequestParam(required = false) Integer isAbsent) {
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(AttendanceRecord::getEmpId, empId);
        }
        if (startDate != null) {
            wrapper.ge(AttendanceRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(AttendanceRecord::getRecordDate, endDate);
        }
        if (isLate != null) {
            wrapper.eq(AttendanceRecord::getIsLate, isLate);
        }
        if (isEarly != null) {
            wrapper.eq(AttendanceRecord::getIsEarly, isEarly);
        }
        if (isAbsent != null) {
            wrapper.eq(AttendanceRecord::getIsAbsent, isAbsent);
        }
        wrapper.orderByDesc(AttendanceRecord::getRecordDate);
        
        Page<AttendanceRecord> page = new Page<>(current, size);
        attendanceRecordService.page(page, wrapper);
        
        PageResult<AttendanceRecord> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/today/{empId}")
    public Result<AttendanceRecord> getTodayRecord(@PathVariable Long empId) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRecordService.lambdaQuery()
                .eq(AttendanceRecord::getEmpId, empId)
                .eq(AttendanceRecord::getRecordDate, today)
                .one();
        return Result.success(record);
    }

    @PostMapping("/clockIn")
    public Result<AttendanceRecord> clockIn(@RequestBody Map<String, Long> params) {
        Long empId = params.get("empId");
        if (empId == null) {
            return Result.error("员工ID不能为空");
        }

        EmpEmployee employee = empEmployeeService.getById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRecordService.lambdaQuery()
                .eq(AttendanceRecord::getEmpId, empId)
                .eq(AttendanceRecord::getRecordDate, today)
                .one();

        if (record == null) {
            record = new AttendanceRecord();
            record.setEmpId(empId);
            record.setRecordDate(today);
            record.setStatus(1);
        }

        if (record.getClockInTime() != null) {
            return Result.error("今日已打卡上班");
        }

        record.setClockInTime(java.time.LocalDateTime.now());
        
        LocalTime workStartTime = LocalTime.parse(morningStart);
        LocalTime actualClockInTime = LocalTime.now();
        
        long lateMinutesActual = java.time.Duration.between(workStartTime, actualClockInTime).toMinutes();
        if (lateMinutesActual > lateMinutes) {
            record.setIsLate(1);
        } else {
            record.setIsLate(0);
        }

        attendanceRecordService.saveOrUpdate(record);
        return Result.success("上班打卡成功", record);
    }

    @PostMapping("/clockOut")
    public Result<AttendanceRecord> clockOut(@RequestBody Map<String, Long> params) {
        Long empId = params.get("empId");
        if (empId == null) {
            return Result.error("员工ID不能为空");
        }

        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRecordService.lambdaQuery()
                .eq(AttendanceRecord::getEmpId, empId)
                .eq(AttendanceRecord::getRecordDate, today)
                .one();

        if (record == null) {
            return Result.error("今日未打卡上班");
        }

        if (record.getClockOutTime() != null) {
            return Result.error("今日已打卡下班");
        }

        record.setClockOutTime(java.time.LocalDateTime.now());
        
        LocalTime workEndTime = LocalTime.parse(afternoonEnd);
        LocalTime actualClockOutTime = LocalTime.now();
        
        long earlyMinutesActual = java.time.Duration.between(actualClockOutTime, workEndTime).toMinutes();
        if (earlyMinutesActual > earlyMinutes) {
            record.setIsEarly(1);
        } else {
            record.setIsEarly(0);
        }

        if (record.getClockInTime() != null && record.getClockOutTime() != null) {
            long minutes = java.time.Duration.between(record.getClockInTime(), record.getClockOutTime()).toMinutes();
            record.setWorkHours(java.math.BigDecimal.valueOf(minutes).divide(java.math.BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP));
        }

        attendanceRecordService.updateById(record);
        return Result.success("下班打卡成功", record);
    }

    @GetMapping("/{id}")
    public Result<AttendanceRecord> getById(@PathVariable Long id) {
        AttendanceRecord record = attendanceRecordService.getById(id);
        return Result.success(record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        attendanceRecordService.removeById(id);
        return Result.success();
    }

    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @RequestParam(required = false) Long empId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws IOException {
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (empId != null) {
            wrapper.eq(AttendanceRecord::getEmpId, empId);
        }
        if (startDate != null) {
            wrapper.ge(AttendanceRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(AttendanceRecord::getRecordDate, endDate);
        }
        wrapper.orderByDesc(AttendanceRecord::getRecordDate);
        
        List<AttendanceRecord> list = attendanceRecordService.list(wrapper);

        String[] headers = {"日期", "上班打卡时间", "下班打卡时间", "工时(小时)", "是否迟到", "是否早退", "是否旷工", "状态"};
        
        ExcelUtil.export(response, "考勤记录列表", "考勤数据", headers, list, record -> {
            String isLate = record.getIsLate() != null && record.getIsLate() == 1 ? "是" : "否";
            String isEarly = record.getIsEarly() != null && record.getIsEarly() == 1 ? "是" : "否";
            String isAbsent = record.getIsAbsent() != null && record.getIsAbsent() == 1 ? "是" : "否";
            String status = record.getStatus() != null && record.getStatus() == 1 ? "正常" : "异常";
            return Arrays.asList(
                    record.getRecordDate(),
                    record.getClockInTime(),
                    record.getClockOutTime(),
                    record.getWorkHours(),
                    isLate,
                    isEarly,
                    isAbsent,
                    status
            );
        });
    }
}
