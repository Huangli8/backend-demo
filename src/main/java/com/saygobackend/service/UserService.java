package com.saygobackend.service;

import com.saygobackend.dto.UserRegisterDTO;
import com.saygobackend.dto.UserLoginDTO;
import com.saygobackend.dto.UserResponseDTO;
import com.saygobackend.entity.User;
import com.saygobackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.saygobackend.exception.UsernameExistsException;
import com.saygobackend.exception.EmailExistsException;
import com.saygobackend.exception.UserNotFoundException;
import com.saygobackend.exception.PasswordErrorException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO register(UserRegisterDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameExistsException("用户名已存在");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailExistsException("邮箱已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return toResponseDTO(user);
    }

    public UserResponseDTO login(UserLoginDTO dto) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(dto.getUsernameOrEmail(), dto.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new PasswordErrorException("密码错误");
        }
        return toResponseDTO(user);
    }

    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
