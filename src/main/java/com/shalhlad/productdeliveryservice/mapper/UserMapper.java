package com.shalhlad.productdeliveryservice.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.productdeliveryservice.dto.request.UserDetailsUpdateRequest;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.entity.user.Employee;
import com.shalhlad.productdeliveryservice.entity.user.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

@Mapper
public abstract class UserMapper {

  public abstract UserDetailsResponse toDetailsResponse(User user);

  public abstract List<UserDetailsResponse> toDetailsResponse(Iterable<User> users);

  public Page<UserDetailsResponse> toDetailsResponse(Page<User> users) {
    return users.map(this::toDetailsResponse);
  }

  @Mapping(target = "department", ignore = true)
  public abstract Employee toEmployee(User user);

  @Mappings({
      @Mapping(target = "userId", ignore = true),
      @Mapping(target = "role", ignore = true),
      @Mapping(target = "registrationDate", ignore = true),
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "encryptedPassword", ignore = true),
      @Mapping(target = "email", ignore = true),
      @Mapping(target = "firstName", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "lastName", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "patronymic", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "card", ignore = true)
  })
  public abstract void update(@MappingTarget User user,
      UserDetailsUpdateRequest userDetailsUpdateRequest);
}
