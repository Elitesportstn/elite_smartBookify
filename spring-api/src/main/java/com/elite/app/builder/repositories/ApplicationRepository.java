package com.elite.app.builder.repositories;

import com.elite.app.builder.entities.Application;
import com.elite.app.builder.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application , Long> {
    Optional<Application> findAllByOwner(User user);
}
