package io.invoice_system.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import io.invoice_system.model.Role;
import io.invoice_system.model.UserEntity;
import io.invoice_system.repository.RoleRepository;
import io.invoice_system.repository.UserRepository;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);


        String email = oauth2User.getAttribute("email");

       
        UserEntity user = userRepository.findByUsername(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setUsername(email);
            newUser.setName(oauth2User.getAttribute("name"));
            newUser.setPhone("");  
            newUser.setCity("");
            newUser.setStreet("");
            

           /* Role roles = roleRepository.findByName("Support_User")
                    .orElseThrow(() -> new RuntimeException("Role 'Support_User' not found"));
            newUser.setRoles(Collections.singletonList(roles));
*/
            return userRepository.save(newUser); 
        });

        return oauth2User;  
    }
}

