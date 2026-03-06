package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minan.game.dto.UserLoginRequest;
import com.minan.game.dto.UserRegisterRequest;
import com.minan.game.mapper.UserMapper;
import com.minan.game.model.User;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(UserLoginRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername())
                   .eq(User::getPassword, IdGenerator.encryptPassword(request.getPassword()));
        return userMapper.selectOne(queryWrapper);
    }

    @Transactional
    public User register(UserRegisterRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            return null;
        }

        User user = new User();
        user.setUserId(IdGenerator.generateUserId());
        user.setUsername(request.getUsername());
        user.setPassword(IdGenerator.encryptPassword(request.getPassword()));
        user.setTotalCp(0);
        user.setLevel(1);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userMapper.insert(user);
        return user;
    }

    public User getUserByUserId(String userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据 openid 查找用户
     */
    public User findByOpenid(String openid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getWechatOpenid, openid);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 通过微信创建用户
     */
    @Transactional
    public User createByWechat(String openid) {
        // 先检查是否已存在
        User existingUser = findByOpenid(openid);
        if (existingUser != null) {
            return existingUser;
        }

        User user = new User();
        user.setUserId(IdGenerator.generateUserId());
        user.setWechatOpenid(openid);
        user.setNickname("探索者_" + (System.currentTimeMillis() % 10000));
        user.setAvatar("");
        user.setTotalCp(0);
        user.setLevel(1);
        user.setTotalScore(0);
        user.setCompletedScenes(0);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userMapper.insert(user);
        return user;
    }
}