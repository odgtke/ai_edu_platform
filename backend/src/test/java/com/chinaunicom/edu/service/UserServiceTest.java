package com.chinaunicom.edu.service;

import com.chinaunicom.edu.entity.User;
import com.chinaunicom.edu.mapper.UserMapper;
import com.chinaunicom.edu.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetAllUsers() {
        // Given
        List<User> mockUsers = Arrays.asList(
            createUser(1L, "student1", "学生1"),
            createUser(2L, "teacher1", "教师1")
        );
        when(userMapper.selectList(null)).thenReturn(mockUsers);

        // When
        List<User> result = userService.list();

        // Then
        assertEquals(2, result.size());
        assertEquals("student1", result.get(0).getUsername());
        assertEquals("teacher1", result.get(1).getUsername());
        verify(userMapper).selectList(null);
    }

    @Test
    void testCreateUser() {
        // Given
        User user = createUser(1L, "newuser", "新用户");
        when(userMapper.insert(user)).thenReturn(1);

        // When
        boolean result = userService.save(user);

        // Then
        assertTrue(result);
        verify(userMapper).insert(user);
    }

    @Test
    void testUpdateUser() {
        // Given
        User user = createUser(1L, "updateduser", "更新用户");
        when(userMapper.updateById(user)).thenReturn(1);

        // When
        boolean result = userService.updateById(user);

        // Then
        assertTrue(result);
        verify(userMapper).updateById(user);
    }

    @Test
    void testDeleteUser() {
        // Given
        Long userId = 1L;
        when(userMapper.deleteById(userId)).thenReturn(1);

        // When
        boolean result = userService.removeById(userId);

        // Then
        assertTrue(result);
        verify(userMapper).deleteById(userId);
    }

    @Test
    void testGetUserById() {
        // Given
        Long userId = 1L;
        User mockUser = createUser(userId, "testuser", "测试用户");
        when(userMapper.selectById(userId)).thenReturn(mockUser);

        // When
        User result = userService.getById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testuser", result.getUsername());
        verify(userMapper).selectById(userId);
    }

    private User createUser(Long id, String username, String realName) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setRealName(realName);
        user.setUserType(1); // 学生
        user.setStatus(1);   // 启用
        return user;
    }
}