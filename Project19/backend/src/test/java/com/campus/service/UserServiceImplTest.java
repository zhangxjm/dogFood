package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.ResultCode;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import com.campus.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService单元测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRealName("测试用户");
        testUser.setRole(User.ROLE_STUDENT);
        testUser.setStatus(User.STATUS_ENABLED);

        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Test
    @DisplayName("测试登录成功")
    void testLogin_Success() {
        doReturn(testUser).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        User result = userService.login("testuser", "rawPassword");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("测试登录-用户不存在")
    void testLogin_UserNotExist() {
        doReturn(null).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("nonexistent", "password");
        });

        assertEquals(ResultCode.USERNAME_NOT_EXIST.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试登录-密码错误")
    void testLogin_PasswordError() {
        doReturn(testUser).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "wrongPassword");
        });

        assertEquals(ResultCode.PASSWORD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试登录-用户已禁用")
    void testLogin_UserDisabled() {
        testUser.setStatus(User.STATUS_DISABLED);
        doReturn(testUser).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "rawPassword");
        });

        assertEquals(ResultCode.USER_DISABLED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("测试注册成功")
    void testRegister_Success() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("rawPassword");
        newUser.setRealName("新用户");

        doReturn(null).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(newUser);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals(User.ROLE_STUDENT, result.getRole());
        assertEquals(User.STATUS_ENABLED, result.getStatus());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("测试注册-用户名已存在")
    void testRegister_UsernameExist() {
        User newUser = new User();
        newUser.setUsername("existinguser");

        doReturn(testUser).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(newUser);
        });

        assertEquals(ResultCode.USERNAME_EXIST.getMessage(), exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("测试根据用户名获取用户")
    void testGetUserByUsername() {
        doReturn(testUser).when(userMapper).selectOne(any(LambdaQueryWrapper.class), anyBoolean());

        User result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("测试更新用户状态")
    void testUpdateStatus() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        boolean result = userService.updateStatus(1L, User.STATUS_DISABLED);

        assertTrue(result);
        assertEquals(User.STATUS_DISABLED, testUser.getStatus());
    }

    @Test
    @DisplayName("测试更新用户状态-用户不存在")
    void testUpdateStatus_UserNotExist() {
        when(userMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateStatus(999L, User.STATUS_DISABLED);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试更新用户角色")
    void testUpdateRole() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        boolean result = userService.updateRole(1L, User.ROLE_PRESIDENT);

        assertTrue(result);
        assertEquals(User.ROLE_PRESIDENT, testUser.getRole());
    }

    @Test
    @DisplayName("测试更新用户角色-用户不存在")
    void testUpdateRole_UserNotExist() {
        when(userMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateRole(999L, User.ROLE_ADMIN);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试分页查询用户")
    void testPageUsers() {
        Page<User> page = new Page<>(1, 10);
        
        List<User> records = new ArrayList<>();
        records.add(testUser);
        
        Page<User> mockResult = new Page<>(1, 10);
        mockResult.setRecords(records);
        mockResult.setTotal(1L);

        when(userMapper.selectPage(eq(page), any(LambdaQueryWrapper.class))).thenReturn(mockResult);

        Page<User> result = userService.pageUsers(page, "test", null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertNull(result.getRecords().get(0).getPassword());
    }
}
