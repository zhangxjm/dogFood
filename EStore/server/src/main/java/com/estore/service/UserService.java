package com.estore.service;

import com.estore.config.JwtProperties;
import com.estore.config.WeixinProperties;
import com.estore.entity.User;
import com.estore.repository.UserRepository;
import com.estore.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final WeixinProperties weixinProperties;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Transactional
    public Map<String, Object> wxLogin(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weixinProperties.getAppId(),
                weixinProperties.getAppSecret(),
                code
        );
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.has("errcode")) {
                log.error("微信登录失败: {}", jsonNode.get("errmsg").asText());
                throw new RuntimeException("微信登录失败: " + jsonNode.get("errmsg").asText());
            }
            
            String openId = jsonNode.get("openid").asText();
            String sessionKey = jsonNode.has("session_key") ? jsonNode.get("session_key").asText() : null;
            
            User user = userRepository.findByOpenId(openId).orElse(null);
            if (user == null) {
                user = new User();
                user.setOpenId(openId);
                user.setSessionKey(sessionKey);
                user.setStatus(1);
                user = userRepository.save(user);
            } else {
                user.setSessionKey(sessionKey);
                user = userRepository.save(user);
            }
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("openId", openId);
            String token = jwtUtil.generateToken(jwtProperties.getSecret(), jwtProperties.getExpiration(), claims);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            
            return result;
        } catch (Exception e) {
            log.error("微信登录异常", e);
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }
    
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    @Transactional
    public User update(Long userId, User user) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        
        return userRepository.save(existingUser);
    }
}
