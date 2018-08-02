package org.softuni.eventures.repository;

import org.softuni.eventures.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User getByUsername(String username);
}