package com.se1933g01.steamclonebackend.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import com.se1933g01.steamclonebackend.dto.LoginDTO;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.entity.user.Role;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.service.AuthService;
import com.se1933g01.steamclonebackend.utils.JwtUtil;

public class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserRepo userRepo;
    private AuthService authService;

    @Before
    public void setup() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        userRepo = mock(UserRepo.class);
        authService = new AuthService(userRepo, null);
        authService.setAuthenticationManager(authenticationManager);
        try {
            var jwtField = AuthService.class.getDeclaredField("jwtUtil");
            jwtField.setAccessible(true);
            jwtField.set(authService, jwtUtil);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to inject jwtUtil via reflection", e);
        }
    }

    @Test
    public void login_validUsernameAndPassword_returnsTokenAndUsername() {

        LoginDTO request = new LoginDTO();
        request.setUsername("loc");
        request.setPassword("123");

        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setAvatarUrl("avatar.png");

        Role mockRole = new Role();
        mockRole.setRoleName("USER");
        mockUser.setRole(mockRole);

        CustomUserDetail mockUserDetail = mock(CustomUserDetail.class);
        when(mockUserDetail.getUsername()).thenReturn("loc");
        when(mockUserDetail.getUser()).thenReturn(mockUser);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUserDetail);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(jwtUtil.generateToken("loc", 1L, "USER", "avatar.png",true))
                .thenReturn("mocked-token");

        ResponseEntity<?> response = authService.login(request);

        assertEquals(200, response.getStatusCode().value());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("mocked-token", body.get("token"));
        assertEquals("loc", body.get("username"));
    }

    @Test
    public void login_invalidPassword_returns401() {
        LoginDTO request = new LoginDTO();
        request.setUsername("loc");
        request.setPassword("1234");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        ResponseEntity<?> response = authService.login(request);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    public void login_invalidUsername_returns401() {
        LoginDTO request = new LoginDTO();
        request.setUsername("lmao");
        request.setPassword("123");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        ResponseEntity<?> response = authService.login(request);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid username or password", response.getBody());
    }
}
