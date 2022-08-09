package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequestDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmployeeServiceImplTest {

  @InjectMocks
  private EmployeeServiceImpl service;

  @Mock
  private UserRepository userRepository;
  @Mock
  private DepartmentRepository departmentRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private EmployeeMapper employeeMapper;
  @Mock
  private UserMapper userMapper;

  private User firstUser;
  private User secondUser;
  private Employee firstEmployee;
  private Employee secondEmployee;
  private Department department;
  private Principal principal;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    department = new Department();
    department.setId(1L);

    firstUser = new User();
    firstUser.setId(1L);
    firstUser.setRole(Role.ROLE_COLLECTOR);
    firstUser.setEmail("test");
    firstUser.setUserId("test");

    secondUser = new User();
    secondUser.setId(2L);
    secondUser.setRole(Role.ROLE_CUSTOMER);
    secondUser.setEmail("test");
    secondUser.setUserId("test");

    firstEmployee = new Employee();
    firstEmployee.setId(firstUser.getId());
    firstEmployee.setRole(firstUser.getRole());
    firstEmployee.setEmail(firstUser.getEmail());
    firstEmployee.setUserId(firstUser.getUserId());
    firstEmployee.setDepartment(department);

    secondEmployee = new Employee();
    secondEmployee.setId(2L);
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);
    secondEmployee.setEmail("test");
    secondEmployee.setUserId("test");
    secondEmployee.setDepartment(department);

    principal = () -> "Test";
  }

  @Test
  public void findAllEmployeesOfAuthorizationDepartment() {

    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(employeeRepository.findAllByDepartment(any(Department.class))).thenReturn(
        List.of(firstEmployee));

    Iterable<Employee> actual = service.findAllEmployeesOfAuthorizationDepartment(principal);
    assertThat(actual).isEqualTo(List.of(firstEmployee));
  }

  @Test
  public void findAllByDepartmentId() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(employeeRepository.findAllByDepartment(any(Department.class))).thenReturn(
        List.of(firstEmployee));

    Iterable<Employee> actual = service.findAllEmployeesByDepartmentId(principal, 1L);
    assertThat(actual).isEqualTo(List.of(firstEmployee));
  }

  @Test
  public void findAllByDepartment_NoRightsException() {
    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);

    Department anotherDepartment = new Department();
    anotherDepartment.setId(2L);
    firstEmployee.setDepartment(anotherDepartment);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.findAllEmployeesByDepartmentId(principal, 1L));
  }

  @Test
  public void findEmployeeOfCurrentDepartmentByUserId() {
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(employeeRepository.findByDepartmentAndUserId(any(Department.class),
        anyString())).thenReturn(Optional.of(firstEmployee));

    Employee actual = service.findEmployeeOfCurrentDepartmentByUserId(principal, "test");
    assertThat(actual).isEqualTo(firstEmployee);
  }

  @Test
  public void whenPrincipalIsAdmin_recruit() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondUser.setRole(Role.ROLE_CUSTOMER);

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_DEPARTMENT_HEAD);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(secondUser));
    when(userMapper.toEmployee(secondUser)).thenReturn(secondEmployee);
    doNothing().when(userRepository).delete(any(User.class));
    when(employeeRepository.save(any(Employee.class))).thenReturn(secondEmployee);

    Employee actual = service.recruit(principal, userRecruitRequestDto);

    assertThat(actual).isEqualTo(secondEmployee);
  }

  @Test
  public void whenPrincipalIsAdminAndRequestRoleIsNotDepartmentHead_recruit_NoRightException() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondUser.setRole(Role.ROLE_CUSTOMER);

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_WAREHOUSEMAN);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsAdminAndTargetUserIsNotCustomer_recruit() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondUser.setRole(Role.ROLE_ADMIN);

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_DEPARTMENT_HEAD);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(secondUser));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsAdminAndNonExistentDepartmentInRequest_recruit() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondUser.setRole(Role.ROLE_ADMIN);

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_DEPARTMENT_HEAD);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsDepartmentHead_recruit() {
    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    secondUser.setRole(Role.ROLE_CUSTOMER);
    department.setId(firstEmployee.getId());

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_WAREHOUSEMAN);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(secondUser));
    when(userMapper.toEmployee(secondUser)).thenReturn(secondEmployee);
    doNothing().when(userRepository).delete(any(User.class));
    when(employeeRepository.save(any(Employee.class))).thenReturn(secondEmployee);

    Employee actual = service.recruit(principal, userRecruitRequestDto);

    assertThat(actual).isEqualTo(secondEmployee);
  }

  @Test
  public void whenPrincipalIsDepartmentHeadAndTargetRoleIsNotInvalid_recruit_NoRightsException() {
    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    secondUser.setRole(Role.ROLE_CUSTOMER);
    department.setId(firstEmployee.getId());

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_DEPARTMENT_HEAD);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsDepartmentHeadAndTargetIsNotEqualToCurrentDepartment_recruit_NoRightsException() {
    Department anotherDepartment = new Department();
    anotherDepartment.setId(5L);

    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    secondUser.setRole(Role.ROLE_CUSTOMER);
    department.setId(firstEmployee.getId());

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_WAREHOUSEMAN);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(anotherDepartment));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsInvalidRole_recruit_NoRightsException() {
    firstUser.setRole(Role.ROLE_CUSTOMER);
    secondUser.setRole(Role.ROLE_CUSTOMER);
    department.setId(firstEmployee.getId());

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_WAREHOUSEMAN);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsAdminAndTargetUserNonExist_recruit_NotFoundException() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondUser.setRole(Role.ROLE_CUSTOMER);

    UserRecruitRequestDto userRecruitRequestDto = new UserRecruitRequestDto();
    userRecruitRequestDto.setDepartmentId(1L);
    userRecruitRequestDto.setRole(Role.ROLE_DEPARTMENT_HEAD);
    userRecruitRequestDto.setUserId("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.recruit(principal, userRecruitRequestDto));
  }

  @Test
  public void whenPrincipalIsAdmin_fire() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondEmployee.setRole(Role.ROLE_DEPARTMENT_HEAD);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));
    when(employeeMapper.toUser(any(Employee.class))).thenReturn(secondUser);
    doNothing().when(employeeRepository).delete(any(Employee.class));
    when(userRepository.save(any(User.class))).thenReturn(secondUser);

    User actual = service.fire(principal, "test");
    assertThat(actual).isEqualTo(secondUser);
  }

  @Test
  public void whenPrincipalIsAdminAndTargetEmployeeIsNonExistent_fire_NotFoundException() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondEmployee.setRole(Role.ROLE_DEPARTMENT_HEAD);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.fire(principal, "test"));
  }

  @Test
  public void whenPrincipalIsAdminAndTargetEmployeeIsNotDepartmentHead_NoRightsException() {
    firstUser.setRole(Role.ROLE_ADMIN);
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.fire(principal, "test"));
  }

  @Test
  public void whenPrincipalIsDepartmentHeadAndFiringOfTargetEmployeeIsNotAvailable_fire_NoRightsException() {
    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    secondEmployee.setRole(Role.ROLE_DEPARTMENT_HEAD);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.fire(principal, "test"));
  }

  @Test
  public void whenPrincipalIsDepartmentHeadAndCurrentDepartmentIsNotEqualToTarget_fire_NoRightsException() {
    Department anotherDepartment = new Department();
    anotherDepartment.setId(5L);

    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);
    secondEmployee.setDepartment(anotherDepartment);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.fire(principal, "test"));
  }

  @Test
  public void whenPrincipalIsInvalid_fire_NoRightsException() {
    firstUser.setRole(Role.ROLE_WAREHOUSEMAN);
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.fire(principal, "test"));
  }

  @Test
  public void whenPrincipalIsDepartmentHead_fire() {
    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));
    when(employeeMapper.toUser(any(Employee.class))).thenReturn(secondUser);
    doNothing().when(employeeRepository).delete(any(Employee.class));
    when(userRepository.save(any(User.class))).thenReturn(secondUser);

    User actual = service.fire(principal, "test");
    assertThat(actual).isEqualTo(secondUser);
  }

  @Test
  public void changePosition() {
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));
    when(employeeRepository.save(any(Employee.class))).thenReturn(secondEmployee);

    Employee actual = service.changePosition(principal, "test", Role.ROLE_COLLECTOR);
    assertThat(actual).isEqualTo(secondEmployee);
  }

  @Test
  public void changePosition_InvalidValueException() {
    assertThatExceptionOfType(InvalidValueException.class).isThrownBy(
        () -> service.changePosition(principal, "test", Role.ROLE_DB_EDITOR));
  }

  @Test
  public void whenNonExistentTargetEmployee_changePosition_NotFoundException() {
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.changePosition(principal, "test", Role.ROLE_WAREHOUSEMAN));
  }

  @Test
  public void whenTargetEmployeeDepartmentIsNotEqualToPrincipal_changePosition_NoRightsException() {
    Department anotherDepartment = new Department();
    anotherDepartment.setId(5L);

    secondEmployee.setDepartment(anotherDepartment);
    secondEmployee.setRole(Role.ROLE_WAREHOUSEMAN);
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.changePosition(principal, "test", Role.ROLE_WAREHOUSEMAN));
  }

  @Test
  public void whenTargetRoleIsDepartmentHead_changePosition_NoRightsException() {
    secondEmployee.setRole(Role.ROLE_DEPARTMENT_HEAD);
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(employeeRepository.findByUserId(anyString())).thenReturn(Optional.of(secondEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.changePosition(principal, "test", Role.ROLE_WAREHOUSEMAN));
  }


}
