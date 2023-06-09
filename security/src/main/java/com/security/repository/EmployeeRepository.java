package com.security.repository;

import com.security.entities.Employee;
import com.security.entities.Roles;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);

    @Modifying
    @Query("UPDATE Employee e SET e.deleted = true WHERE id =?1")
    void setDeletedTrue(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.username = COALESCE(:username, e.username), e.password = COALESCE(:password, e.password)," +
            " e.email = COALESCE(:email, e.email), e.roles = COALESCE(:roles, e.roles) WHERE e.id = :id") // e.roles = COALESCE(:roles, e.roles) todo impelent roles
    void updateEmployeeById(Long id, String username, String password, String email, Set<Roles> roles);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Employee e SET e.roles = :roles WHERE e.id = :id") // e.roles = COALESCE(:roles, e.roles)
//    void updateEmployeeRoles(Long id, Set<Roles> roles);


//    @Modifying
//    @Transactional
//    @Query("UPDATE Employee e SET e.roles = :nul WHERE e.id = :id") // e.roles = COALESCE(:roles, e.roles)
//    void deleteRoles(Long id, Null nul);
//    @Modifying
//    @Transactional
//    @Query("UPDATE Employee e SET e.roles = :roles WHERE e.id = :id") // e.roles = COALESCE(:roles, e.roles)
//    void putRoles(Long id, Set<Roles> roles);
}
