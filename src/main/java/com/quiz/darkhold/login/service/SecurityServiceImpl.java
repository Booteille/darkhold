package com.quiz.darkhold.login.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class SecurityServiceImpl implements SecurityService {
    private static final String UNREGISTERED_USER = "UNREGISTERED_USER";
    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    private final Logger logger = LogManager.getLogger(SecurityServiceImpl.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public String findLoggedInUsername() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails) userDetails).getUsername();
        }

        return null;
    }

    @Override
    public void autoLogin(final String username, final String password) {
        boolean unRegistered = password.equalsIgnoreCase(UNREGISTERED_USER);
        UserDetails userDetails = getUserDetails(username, unRegistered);
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) userDetails.getAuthorities();
        logger.info("Successfully fetched user details : " + userDetails);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
        if (!unRegistered) {
            authenticationManager.authenticate(token);
        }
        if (token.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(token);
            logger.info(String.format("Auto login %s successfully!", username));
        }
    }

    private UserDetails getUserDetails(final String username, final boolean unRegistered) {
        UserDetails userDetails;
        if (unRegistered) {
            userDetails = new User(username, UNREGISTERED_USER, new ArrayList<>());
        } else {
            userDetails = userDetailsService.loadUserByUsername(username);
            // this is real user
            if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty()) {
                logger.info("empty authorities found, adding moderator role");
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority(ROLE_MODERATOR));
                userDetails = new User(userDetails.getUsername(), userDetails.getPassword(), authorities);
            }
        }
        return userDetails;
    }
}
