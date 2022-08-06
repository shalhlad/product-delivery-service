package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.ProfileService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

  private final ProfileService service;
  private final UserMapper userMapper;
  private final EmployeeMapper employeeMapper;

  public ProfileController(ProfileService service, UserMapper userMapper,
      EmployeeMapper employeeMapper) {
    this.service = service;
    this.userMapper = userMapper;
    this.employeeMapper = employeeMapper;
  }

  @GetMapping
  public UserDetailsDto getProfileDetails(Principal principal) {
    return mapToDetailsDto(service.getDetailsFromPrincipal(principal));
  }

  @PatchMapping
  public UserDetailsDto updateProfileDetails(
      Principal principal,
      @RequestBody @Valid UserDetailsUpdateDto userDetailsUpdateDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapToDetailsDto(service.updateDetailsByPrincipal(principal, userDetailsUpdateDto));
  }


  private UserDetailsDto mapToDetailsDto(User user) {
    return user.getRole().isEmployee() ?
        employeeMapper.toDetailsDto((Employee) user) :
        userMapper.toDetailsDto(user);
  }
}
