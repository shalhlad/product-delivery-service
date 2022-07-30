package com.shalhlad.product_delivery_service.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface UserMapper {

  UserDetailsDto toUserDetailsDto(User user);

  Iterable<UserDetailsDto> toUserDetailsDto(Iterable<User> user);

  @Mapping(target = "department", ignore = true)
  Employee toEmployee(User user);

  @Mappings({
      @Mapping(target = "userId", ignore = true),
      @Mapping(target = "role", ignore = true),
      @Mapping(target = "registrationDate", ignore = true),
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "encryptedPassword", ignore = true),
      @Mapping(target = "email", ignore = true),
      @Mapping(target = "firstName", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "lastName", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "patronymic", nullValuePropertyMappingStrategy = IGNORE)
  })
  void update(@MappingTarget User user, UserDetailsUpdateDto userDetailsUpdateDto);
}
