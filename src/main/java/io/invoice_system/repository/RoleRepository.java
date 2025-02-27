package io.invoice_system.repository;

import org.springframework.data.repository.CrudRepository;

import io.invoice_system.model.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}