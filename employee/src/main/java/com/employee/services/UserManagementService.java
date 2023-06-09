package com.employee.services;

import com.employee.dto.JwtDto;
import com.employee.dto.LoginRequestDto;
import com.employee.dto.RegisterRequestDto;
import com.employee.dto.UpdateRequestDto;
import com.employee.entities.Employee;
import com.employee.entities.Roles;
import com.employee.exceptions.NoEmployeeWithIdException;
import com.employee.exceptions.NoRolesException;
import com.employee.exceptions.TakenUserNameException;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.RolesRepository;
import com.employee.securityClient.SecurityClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserManagementService implements com.employee.interfaces.UserManagementService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final SecurityClient securityClient;

    //-----------------------------------------------------------------------------------------------

    /**
     Performs a login request with the provided login credentials and retrieves a JWT token.
     @param loginRequestDto The LoginRequestDto object containing the login credentials.
     @return A JwtDto object containing the JWT token retrieved from the login response.
     */
    @Override
    public JwtDto login(LoginRequestDto loginRequestDto) {

        ResponseEntity<JwtDto> jwtResponse = securityClient.login(loginRequestDto);

        return jwtResponse.getBody();
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * Registers multiple employees with the provided user details and saves them to the database.
     *
     * @param registerRequestDtoList The list of RegisterRequestDto objects containing the employee details for registration.
     * @return The set of registered employees.
     * @throws JsonProcessingException if an error occurs during JSON processing.
     * @throws TakenUserNameException  if the username is already taken by an existing employee.
     * @throws NoRolesException        if an employee does not have at least one role assigned.
     */
    @Override
    public Set<Employee> registerEmployee(List<RegisterRequestDto> registerRequestDtoList) throws JsonProcessingException {

        Set<Employee> employeeSet = new HashSet<>();

        for(RegisterRequestDto registerRequestDto: registerRequestDtoList) {

            Optional<Employee> employeeControl = employeeRepository.findByUsername(registerRequestDto.getUsername());

            if(employeeControl.isPresent()) {
                throw new TakenUserNameException("Username: " + employeeControl.get().getUsername() + " is taken");
            }

            String  username = registerRequestDto.getUsername();

            if(registerRequestDto.getRoleSet().size() == 0) {
                throw new NoRolesException("An employee mush have at least one role");
            }

            Employee employee = new Employee();
            employee.setUsername(username);
            employee.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            employee.setEmail(registerRequestDto.getEmail());
            employee.setRoles(getRolesSetFromRegisterRoleDtoSet(employee, registerRequestDto.getRoleSet()));
            employee.setDepartment(registerRequestDto.getDepartment());
            employee.setTerminal(registerRequestDto.getTerminal());
            employeeSet.add(employeeRepository.save(employee));
        }
        return employeeSet;
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * Deletes an employee with the specified ID by marking it as deleted in the database.
     *
     * @param id                  The ID of the employee to delete.
     * @return The ID of the deleted employee.
     * @throws JsonProcessingException   if an error occurs during JSON processing.
     * @throws NoEmployeeWithIdException if no employee with the specified ID exists.
     */
    @Transactional
    @Override
    public Long deleteEmployeeById(Long id) throws JsonProcessingException {

        if(!employeeRepository.existsById(id)) throw new NoEmployeeWithIdException("Employee with id " + id + " does not exists");

        else {
            employeeRepository.setDeletedTrue(id);
            return id;
        }
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * Updates the details of an employee with the specified ID.
     *
     * @param id                  The ID of the employee to update.
     * @param updateRequestDto    The UpdateRequestDto object containing the updated employee details.
     * @return The updated Employee object.
     * @throws JsonProcessingException    if an error occurs during JSON processing.
     * @throws NoEmployeeWithIdException      if no employee with the specified ID exists.
     */
    @Transactional
    @Override
    public Employee updateEmployee(Long id, UpdateRequestDto updateRequestDto) throws JsonProcessingException {

        if(!employeeRepository.existsById(id)) throw new NoEmployeeWithIdException("Employee with id " + id + " does not exists"); // string builder

        String password = null;
        if(updateRequestDto.getPassword() != null) {
             password = updateRequestDto.getPassword();
             password = passwordEncoder.encode(updateRequestDto.getPassword());
        }
        String username = updateRequestDto.getUsername();
        String email = updateRequestDto.getEmail();
        String department = updateRequestDto.getDepartment();
        String terminal = updateRequestDto.getTerminal();

        employeeRepository.updateEmployeeById(id, username, password, email, department, terminal);

        Employee employee = employeeRepository.findById(id).get();

        Set<Roles> rolesSet = getRolesSetFromUpdateRoleDtoSet(employee, updateRequestDto.getRoleSet());

        if(rolesSet.size() != 0) {
            rolesRepository.deleteRoleById(employee.getId());
            employee.updateRoles(rolesSet);
        }
        employeeRepository.save(employee);

        return employee;
    }

    //-----------------------------------------------------------------------------------------------

    /**
     Converts a Set of RegisterRequestDto.RoleDto objects into a Set of Roles objects associated with the provided employee.
     @param employee The Employee object to associate with the roles.
     @param roleDtoSet A Set of RegisterRequestDto.RoleDto objects containing role data.
     @return A Set of Roles objects associated with the provided employee and extracted from the roleDtoSet.
     */
    public Set<Roles> getRolesSetFromRegisterRoleDtoSet(Employee employee, Set<RegisterRequestDto.RoleDto> roleDtoSet) {
        Set<Roles> newRolesSet = new HashSet<>();

        for (RegisterRequestDto.RoleDto roleDto : roleDtoSet) {

            Roles role = new Roles(employee, roleDto.getRoleName());
            //Roles role = Roles.builder().employee(employee).roleName(roleDto.getRoleName()).build();
            newRolesSet.add(role);
        }
        return newRolesSet;
    }

    public Set<Roles> getRolesSetFromUpdateRoleDtoSet(Employee employee, Set<UpdateRequestDto.RoleDto> roleDtoSet) {
        Set<Roles> newRolesSet = new HashSet<>();

        for (UpdateRequestDto.RoleDto roleDto : roleDtoSet) {
            Roles role = new Roles(employee, roleDto.getRoleName());
            newRolesSet.add(role);
        }
        return newRolesSet;
    }
}
