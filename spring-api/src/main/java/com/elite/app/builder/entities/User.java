package com.elite.app.builder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class User implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String username;
        @Column(unique = true)
        private String email;
        private String password;
        private boolean enabled = false;
        private boolean subscribed = false ;
        private String paymentRef = "";
        private String activationCode = "";
        private String phone;
        @Enumerated(EnumType.STRING)
        private Role role;
        @JsonIgnore
        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
        private List<Application> applications = new ArrayList<>();

        @JsonIgnore
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(role.name()));
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return enabled;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

    }
