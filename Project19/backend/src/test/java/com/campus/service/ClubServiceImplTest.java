package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.ResultCode;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import com.campus.service.impl.ClubServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClubService单元测试")
class ClubServiceImplTest {

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private ClubMemberMapper clubMemberMapper;

    @InjectMocks
    private ClubServiceImpl clubService;

    private Club testClub;
    private ClubMember testMember;

    @BeforeEach
    void setUp() {
        testClub = new Club();
        testClub.setId(1L);
        testClub.setName("计算机协会");
        testClub.setDescription("计算机协会是一个技术社团");
        testClub.setCategoryId(1L);
        testClub.setPresidentId(2L);
        testClub.setMaxMembers(100);
        testClub.setCurrentMembers(5);
        testClub.setStatus(Club.STATUS_APPROVED);

        testMember = new ClubMember();
        testMember.setId(1L);
        testMember.setClubId(1L);
        testMember.setUserId(3L);
        testMember.setRole(ClubMember.ROLE_MEMBER);
        testMember.setStatus(ClubMember.STATUS_PENDING);

        ReflectionTestUtils.setField(clubService, "baseMapper", clubMapper);
    }

    @Test
    @DisplayName("测试获取社团详情")
    void testGetClubDetail() {
        when(clubMapper.getByIdWithDetail(1L)).thenReturn(testClub);

        Club result = clubService.getClubDetail(1L);

        assertNotNull(result);
        assertEquals("计算机协会", result.getName());
    }

    @Test
    @DisplayName("测试创建社团成功")
    void testCreateClub_Success() {
        Club newClub = new Club();
        newClub.setName("新社团");
        newClub.setCategoryId(2L);

        doReturn(null).when(clubMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());
        when(clubMapper.insert(any(Club.class))).thenReturn(1);

        boolean result = clubService.createClub(newClub, 1L);

        assertTrue(result);
        assertEquals(1L, newClub.getPresidentId());
        assertEquals(0, newClub.getCurrentMembers());
        assertEquals(Club.STATUS_PENDING, newClub.getStatus());
        verify(clubMapper).insert(any(Club.class));
    }

    @Test
    @DisplayName("测试创建社团-名称已存在")
    void testCreateClub_NameExist() {
        Club newClub = new Club();
        newClub.setName("计算机协会");

        doReturn(testClub).when(clubMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.createClub(newClub, 1L);
        });

        assertEquals(ResultCode.CLUB_NAME_EXIST.getMessage(), exception.getMessage());
        verify(clubMapper, never()).insert(any(Club.class));
    }

    @Test
    @DisplayName("测试审核社团通过")
    void testAuditClub_Approved() {
        testClub.setStatus(Club.STATUS_PENDING);
        testClub.setPresidentId(2L);

        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMapper.updateById(any(Club.class))).thenReturn(1);
        when(clubMemberMapper.insert(any(ClubMember.class))).thenReturn(1);

        boolean result = clubService.auditClub(1L, Club.STATUS_APPROVED, "审核通过");

        assertTrue(result);
        assertEquals(Club.STATUS_APPROVED, testClub.getStatus());
        assertEquals("审核通过", testClub.getAuditRemark());
        assertEquals(1, testClub.getCurrentMembers());
        verify(clubMemberMapper).insert(any(ClubMember.class));
    }

    @Test
    @DisplayName("测试审核社团拒绝")
    void testAuditClub_Rejected() {
        testClub.setStatus(Club.STATUS_PENDING);

        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMapper.updateById(any(Club.class))).thenReturn(1);

        boolean result = clubService.auditClub(1L, Club.STATUS_REJECTED, "审核拒绝");

        assertTrue(result);
        assertEquals(Club.STATUS_REJECTED, testClub.getStatus());
        verify(clubMemberMapper, never()).insert(any(ClubMember.class));
    }

    @Test
    @DisplayName("测试审核社团-社团不存在")
    void testAuditClub_NotExist() {
        when(clubMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.auditClub(999L, Club.STATUS_APPROVED, "");
        });

        assertEquals("社团不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试申请加入社团成功")
    void testApplyJoin_Success() {
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(clubMemberMapper.insert(any(ClubMember.class))).thenReturn(1);

        boolean result = clubService.applyJoin(1L, 3L, "想加入社团");

        assertTrue(result);
        verify(clubMemberMapper).insert(any(ClubMember.class));
    }

    @Test
    @DisplayName("测试申请加入社团-社团不存在")
    void testApplyJoin_ClubNotExist() {
        when(clubMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.applyJoin(999L, 3L, "");
        });

        assertEquals("社团不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试申请加入社团-社团未审核通过")
    void testApplyJoin_ClubNotApproved() {
        testClub.setStatus(Club.STATUS_PENDING);
        when(clubMapper.selectById(1L)).thenReturn(testClub);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.applyJoin(1L, 3L, "");
        });

        assertEquals("社团未审核通过，无法加入", exception.getMessage());
    }

    @Test
    @DisplayName("测试申请加入社团-已申请")
    void testApplyJoin_AlreadyApplied() {
        testMember.setStatus(ClubMember.STATUS_PENDING);
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.applyJoin(1L, 3L, "");
        });

        assertEquals(ResultCode.ALREADY_APPLIED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试申请加入社团-已是成员")
    void testApplyJoin_AlreadyMember() {
        testMember.setStatus(ClubMember.STATUS_APPROVED);
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.applyJoin(1L, 3L, "");
        });

        assertEquals(ResultCode.ALREADY_MEMBER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试申请加入社团-社团已满")
    void testApplyJoin_ClubFull() {
        testClub.setCurrentMembers(100);
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.applyJoin(1L, 3L, "");
        });

        assertEquals(ResultCode.CLUB_FULL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试审核加入申请通过")
    void testAuditJoin_Approved() {
        testMember.setClubId(1L);
        testMember.setStatus(ClubMember.STATUS_PENDING);

        when(clubMemberMapper.selectById(1L)).thenReturn(testMember);
        when(clubMemberMapper.updateById(any(ClubMember.class))).thenReturn(1);
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMapper.updateById(any(Club.class))).thenReturn(1);

        boolean result = clubService.auditJoin(1L, ClubMember.STATUS_APPROVED, "通过");

        assertTrue(result);
        assertEquals(ClubMember.STATUS_APPROVED, testMember.getStatus());
        assertEquals("通过", testMember.getAuditRemark());
        assertNotNull(testMember.getJoinTime());
    }

    @Test
    @DisplayName("测试审核加入申请-申请不存在")
    void testAuditJoin_NotExist() {
        when(clubMemberMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.auditJoin(999L, ClubMember.STATUS_APPROVED, "");
        });

        assertEquals("申请记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试退出社团成功")
    void testQuitClub_Success() {
        testMember.setStatus(ClubMember.STATUS_APPROVED);
        testMember.setRole(ClubMember.ROLE_MEMBER);
        testMember.setClubId(1L);

        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);
        when(clubMemberMapper.updateById(any(ClubMember.class))).thenReturn(1);
        when(clubMapper.selectById(1L)).thenReturn(testClub);
        when(clubMapper.updateById(any(Club.class))).thenReturn(1);

        boolean result = clubService.quitClub(1L, 3L);

        assertTrue(result);
        assertEquals(ClubMember.STATUS_QUIT, testMember.getStatus());
    }

    @Test
    @DisplayName("测试退出社团-不是成员")
    void testQuitClub_NotMember() {
        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.quitClub(1L, 999L);
        });

        assertEquals("不是该社团成员", exception.getMessage());
    }

    @Test
    @DisplayName("测试退出社团-社长不能退出")
    void testQuitClub_PresidentCannotQuit() {
        testMember.setStatus(ClubMember.STATUS_APPROVED);
        testMember.setRole(ClubMember.ROLE_PRESIDENT);

        when(clubMemberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clubService.quitClub(1L, 2L);
        });

        assertEquals("社长不能退出社团，请先转让社长职位", exception.getMessage());
    }

    @Test
    @DisplayName("测试获取社团成员列表")
    void testGetClubMembers() {
        List<ClubMember> mockMembers = new ArrayList<>();
        mockMembers.add(testMember);

        when(clubMemberMapper.listByClubId(1L)).thenReturn(mockMembers);

        List<ClubMember> result = clubService.getClubMembers(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("测试获取我的社团列表")
    void testGetMyClubs() {
        List<ClubMember> mockMembers = new ArrayList<>();
        testMember.setStatus(ClubMember.STATUS_APPROVED);
        mockMembers.add(testMember);

        ClubMember pendingMember = new ClubMember();
        pendingMember.setStatus(ClubMember.STATUS_PENDING);
        mockMembers.add(pendingMember);

        when(clubMemberMapper.listByUserId(3L)).thenReturn(mockMembers);

        List<ClubMember> result = clubService.getMyClubs(3L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClubMember.STATUS_APPROVED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("测试获取我的申请列表")
    void testGetMyApplyClubs() {
        List<ClubMember> mockMembers = new ArrayList<>();
        testMember.setStatus(ClubMember.STATUS_PENDING);
        mockMembers.add(testMember);

        ClubMember approvedMember = new ClubMember();
        approvedMember.setStatus(ClubMember.STATUS_APPROVED);
        mockMembers.add(approvedMember);

        when(clubMemberMapper.listByUserId(3L)).thenReturn(mockMembers);

        List<ClubMember> result = clubService.getMyApplyClubs(3L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClubMember.STATUS_PENDING, result.get(0).getStatus());
    }

    @Test
    @DisplayName("测试获取待审核申请列表")
    void testGetPendingApprovals() {
        List<ClubMember> mockMembers = new ArrayList<>();
        mockMembers.add(testMember);

        when(clubMemberMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(mockMembers);

        List<ClubMember> result = clubService.getPendingApprovals(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
