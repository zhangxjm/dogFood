package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.ResultCode;
import com.campus.entity.Activity;
import com.campus.entity.ActivityRegistration;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.mapper.ActivityMapper;
import com.campus.mapper.ActivityRegistrationMapper;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import com.campus.service.impl.ActivityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService单元测试")
class ActivityServiceImplTest {

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private ActivityRegistrationMapper registrationMapper;

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private ClubMemberMapper clubMemberMapper;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private Activity testActivity;
    private ActivityRegistration testRegistration;
    private Club testClub;
    private ClubMember testMember;

    @BeforeEach
    void setUp() {
        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setClubId(1L);
        testActivity.setTitle("Java编程入门培训");
        testActivity.setDescription("面向零基础同学的Java编程入门培训");
        testActivity.setLocation("教学楼A101");
        testActivity.setStartTime(LocalDateTime.now().plusDays(1));
        testActivity.setEndTime(LocalDateTime.now().plusDays(1).plusHours(3));
        testActivity.setMaxParticipants(30);
        testActivity.setCurrentParticipants(5);
        testActivity.setStatus(Activity.STATUS_REGISTRATION);

        testRegistration = new ActivityRegistration();
        testRegistration.setId(1L);
        testRegistration.setActivityId(1L);
        testRegistration.setUserId(3L);
        testRegistration.setStatus(ActivityRegistration.STATUS_REGISTERED);

        testClub = new Club();
        testClub.setId(1L);
        testClub.setName("计算机协会");
        testClub.setPresidentId(2L);
        testClub.setStatus(Club.STATUS_APPROVED);

        testMember = new ClubMember();
        testMember.setId(1L);
        testMember.setClubId(1L);
        testMember.setUserId(2L);
        testMember.setRole(ClubMember.ROLE_PRESIDENT);
        testMember.setStatus(ClubMember.STATUS_APPROVED);

        ReflectionTestUtils.setField(activityService, "baseMapper", activityMapper);
    }

    @Test
    @DisplayName("测试获取活动详情")
    void testGetActivityDetail() {
        when(activityMapper.getByIdWithDetail(1L)).thenReturn(testActivity);

        Activity result = activityService.getActivityDetail(1L);

        assertNotNull(result);
        assertEquals("Java编程入门培训", result.getTitle());
    }

    @Test
    @DisplayName("测试创建活动成功")
    void testCreateActivity_Success() {
        Activity newActivity = new Activity();
        newActivity.setClubId(1L);
        newActivity.setTitle("新活动");

        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        boolean result = activityService.createActivity(newActivity, 2L);

        assertTrue(result);
        assertEquals(0, newActivity.getCurrentParticipants());
        assertEquals(Activity.STATUS_REGISTRATION, newActivity.getStatus());
        verify(activityMapper).insert(any(Activity.class));
    }

    @Test
    @DisplayName("测试创建活动-社团不存在")
    void testCreateActivity_ClubNotExist() {
        Activity newActivity = new Activity();
        newActivity.setClubId(999L);

        when(clubMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.createActivity(newActivity, 2L);
        });

        assertEquals("社团不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试创建活动-不是社长")
    void testCreateActivity_NotPresident() {
        Activity newActivity = new Activity();
        newActivity.setClubId(1L);

        testMember.setRole(ClubMember.ROLE_MEMBER);
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.createActivity(newActivity, 3L);
        });

        assertEquals("只有社长可以发布活动", exception.getMessage());
    }

    @Test
    @DisplayName("测试更新活动")
    void testUpdateActivity() {
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        boolean result = activityService.updateActivity(testActivity);

        assertTrue(result);
        verify(activityMapper).updateById(eq(testActivity));
    }

    @Test
    @DisplayName("测试报名活动成功-新报名")
    void testRegisterActivity_Success_New() {
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(registrationMapper.insert(any(ActivityRegistration.class))).thenReturn(1);
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        boolean result = activityService.registerActivity(1L, 3L);

        assertTrue(result);
        verify(registrationMapper).insert(any(ActivityRegistration.class));
    }

    @Test
    @DisplayName("测试报名活动成功-重新报名")
    void testRegisterActivity_Success_ReRegister() {
        testRegistration.setStatus(ActivityRegistration.STATUS_CANCELLED);
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testRegistration);
        when(registrationMapper.updateById(any(ActivityRegistration.class))).thenReturn(1);
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        boolean result = activityService.registerActivity(1L, 3L);

        assertTrue(result);
        assertEquals(ActivityRegistration.STATUS_REGISTERED, testRegistration.getStatus());
        verify(registrationMapper).updateById(any(ActivityRegistration.class));
    }

    @Test
    @DisplayName("测试报名活动-活动不存在")
    void testRegisterActivity_ActivityNotExist() {
        when(activityMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.registerActivity(999L, 3L);
        });

        assertEquals("活动不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试报名活动-不在报名期")
    void testRegisterActivity_NotRegistrationPeriod() {
        testActivity.setStatus(Activity.STATUS_CANCELLED);
        when(activityMapper.selectById(1L)).thenReturn(testActivity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.registerActivity(1L, 3L);
        });

        assertEquals("活动不在报名期", exception.getMessage());
    }

    @Test
    @DisplayName("测试报名活动-已报名")
    void testRegisterActivity_AlreadyRegistered() {
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testRegistration);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.registerActivity(1L, 3L);
        });

        assertEquals(ResultCode.ALREADY_REGISTERED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试报名活动-活动已满")
    void testRegisterActivity_Full() {
        testActivity.setCurrentParticipants(30);
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.registerActivity(1L, 3L);
        });

        assertEquals(ResultCode.ACTIVITY_FULL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试取消报名成功")
    void testCancelRegistration_Success() {
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testRegistration);
        when(registrationMapper.updateById(any(ActivityRegistration.class))).thenReturn(1);
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        boolean result = activityService.cancelRegistration(1L, 3L);

        assertTrue(result);
        assertEquals(ActivityRegistration.STATUS_CANCELLED, testRegistration.getStatus());
    }

    @Test
    @DisplayName("测试取消报名-未报名")
    void testCancelRegistration_NotRegistered() {
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.cancelRegistration(1L, 3L);
        });

        assertEquals("未报名该活动", exception.getMessage());
    }

    @Test
    @DisplayName("测试取消报名-已经取消")
    void testCancelRegistration_AlreadyCancelled() {
        testRegistration.setStatus(ActivityRegistration.STATUS_CANCELLED);
        when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testRegistration);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activityService.cancelRegistration(1L, 3L);
        });

        assertEquals("未报名该活动", exception.getMessage());
    }

    @Test
    @DisplayName("测试获取活动报名列表")
    void testGetActivityRegistrations() {
        List<ActivityRegistration> mockRegistrations = new ArrayList<>();
        mockRegistrations.add(testRegistration);

        when(registrationMapper.listByActivityId(1L)).thenReturn(mockRegistrations);

        List<ActivityRegistration> result = activityService.getActivityRegistrations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("测试获取我的活动列表")
    void testGetMyActivities() {
        List<ActivityRegistration> mockRegistrations = new ArrayList<>();
        testRegistration.setStatus(ActivityRegistration.STATUS_REGISTERED);
        mockRegistrations.add(testRegistration);

        ActivityRegistration cancelled = new ActivityRegistration();
        cancelled.setStatus(ActivityRegistration.STATUS_CANCELLED);
        mockRegistrations.add(cancelled);

        when(registrationMapper.listByUserId(3L)).thenReturn(mockRegistrations);

        List<ActivityRegistration> result = activityService.getMyActivities(3L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ActivityRegistration.STATUS_REGISTERED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("测试获取社团活动列表")
    void testGetClubActivities() {
        List<Activity> mockActivities = new ArrayList<>();
        mockActivities.add(testActivity);

        when(activityMapper.listByClubId(1L)).thenReturn(mockActivities);

        List<Activity> result = activityService.getClubActivities(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
