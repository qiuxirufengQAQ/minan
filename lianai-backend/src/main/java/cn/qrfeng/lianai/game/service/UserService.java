package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.qrfeng.lianai.game.dto.UserLoginRequest;
import cn.qrfeng.lianai.game.dto.UserRegisterRequest;
import cn.qrfeng.lianai.game.mapper.UserMapper;
import cn.qrfeng.lianai.game.model.User;
import cn.qrfeng.lianai.game.utils.IdGenerator;
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
}