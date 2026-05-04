package com.homemaking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homemaking.common.PageResult;
import com.homemaking.common.Result;
import com.homemaking.entity.SysUser;
import com.homemaking.entity.Worker;
import com.homemaking.mapper.SysUserMapper;
import com.homemaking.mapper.WorkerMapper;
import com.homemaking.security.JwtTokenUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {
    
    @Autowired
    private WorkerMapper workerMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/apply")
    @Transactional
    public Result<String> applyWorker(@RequestBody ApplyWorkerRequest request,
                                        @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        LambdaQueryWrapper<Worker> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Worker::getUserId, userId);
        if (workerMapper.selectOne(queryWrapper) != null) {
            return Result.error("您已申请过师傅入驻");
        }
        
        Worker worker = new Worker();
        worker.setUserId(userId);
        worker.setServiceCategoryId(request.getServiceCategoryId());
        worker.setIdCard(request.getIdCard());
        worker.setIdCardFront(request.getIdCardFront());
        worker.setIdCardBack(request.getIdCardBack());
        worker.setSkillDesc(request.getSkillDesc());
        worker.setWorkYears(request.getWorkYears());
        worker.setAuditStatus("PENDING");
        workerMapper.insert(worker);
        
        SysUser user = userMapper.selectById(userId);
        user.setRole("WORKER");
        userMapper.updateById(user);
        
        return Result.success("申请提交成功，请等待审核");
    }
    
    @GetMapping("/profile")
    public Result<Worker> getWorkerProfile(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        LambdaQueryWrapper<Worker> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Worker::getUserId, userId);
        Worker worker = workerMapper.selectOne(queryWrapper);
        
        if (worker == null) {
            return Result.error("您还不是师傅");
        }
        
        SysUser user = userMapper.selectById(worker.getUserId());
        if (user != null) {
            worker.setRealName(user.getRealName());
            worker.setPhone(user.getPhone());
            worker.setAvatar(user.getAvatar());
        }
        
        return Result.success(worker);
    }
    
    @PutMapping("/profile")
    public Result<String> updateWorkerProfile(@RequestBody UpdateWorkerRequest request,
                                                @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        LambdaQueryWrapper<Worker> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Worker::getUserId, userId);
        Worker worker = workerMapper.selectOne(queryWrapper);
        
        if (worker == null) {
            return Result.error("您还不是师傅");
        }
        
        if (request.getSkillDesc() != null) {
            worker.setSkillDesc(request.getSkillDesc());
        }
        if (request.getWorkYears() != null) {
            worker.setWorkYears(request.getWorkYears());
        }
        if (request.getServiceCategoryId() != null) {
            worker.setServiceCategoryId(request.getServiceCategoryId());
        }
        
        workerMapper.updateById(worker);
        return Result.success("更新成功");
    }
    
    @Data
    public static class ApplyWorkerRequest {
        private Long serviceCategoryId;
        private String idCard;
        private String idCardFront;
        private String idCardBack;
        private String skillDesc;
        private Integer workYears;
    }
    
    @Data
    public static class UpdateWorkerRequest {
        private Long serviceCategoryId;
        private String skillDesc;
        private Integer workYears;
    }
}
