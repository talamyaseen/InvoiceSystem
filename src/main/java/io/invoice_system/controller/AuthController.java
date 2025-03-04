package io.invoice_system.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.invoice_system.config.JWTGenerator;
import io.invoice_system.dto.AuthResponseDTO;
import io.invoice_system.dto.LoginDto;
import io.invoice_system.dto.SignUpDto;
import io.invoice_system.model.Role;
import io.invoice_system.model.UserEntity;
import io.invoice_system.repository.RoleRepository;
import io.invoice_system.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
    	logger.info("Attempting login for user: {}", loginDto.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = jwtGenerator.generateToken(authentication);
        
        List<String> roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> {
                    // Assuming grantedAuthority is of type Role, you can extract the role name here
                    return grantedAuthority.getAuthority(); // Or modify this if you have a custom Role object
                })
                .collect(Collectors.toList());

        logger.info("Login successful for user: {}", loginDto.getUsername());
        return new ResponseEntity<>(new AuthResponseDTO(token, roles), HttpStatus.OK);
    }

    @PostMapping("signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto) {

        if (userRepository.existsByUsername(signUpDto.getUsername())) {
        	 logger.warn("Username is already taken: {}", signUpDto.getUsername());
        	 throw new RuntimeException("Username is already taken!");
        }


        UserEntity user = new UserEntity();
        user.setUsername(signUpDto.getUsername());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        user.setName(signUpDto.getName());
        user.setPhone(signUpDto.getPhone());
        user.setCity(signUpDto.getCity());
        user.setStreet(signUpDto.getStreet());

      
        Role roles = roleRepository.findByName("Support_User")
                .orElseThrow(() -> new RuntimeException("Role 'Support_User' not found"));
        user.setRoles(Collections.singletonList(roles));

   
        userRepository.save(user);
        logger.info("User successfully registered: {}", signUpDto.getUsername());
        return new ResponseEntity<>("User registered successfully!",  HttpStatus.CREATED);
    }

}