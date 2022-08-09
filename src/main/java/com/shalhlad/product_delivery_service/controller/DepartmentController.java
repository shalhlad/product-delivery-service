package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreationDto;
import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateDto;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityToChangeDto;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsWithWarehouseDto;
import com.shalhlad.product_delivery_service.mapper.DepartmentMapper;
import com.shalhlad.product_delivery_service.service.DepartmentService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

  private final DepartmentService service;
  private final DepartmentMapper mapper;

  @Autowired
  public DepartmentController(DepartmentService service, DepartmentMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("/{id}")
  public DepartmentDetailsWithWarehouseDto getById(@PathVariable Long id) {
    return mapper.toDetailsWithWarehouseDto(service.findById(id));
  }

  @GetMapping("/me")
  @PreAuthorize("hasAnyRole({'COLLECTOR', 'COURIER', 'DEPARTMENT_HEAD'})")
  public DepartmentDetailsWithWarehouseDto getByAuthorization(
      @ApiIgnore Principal principal) {
    return mapper.toDetailsWithWarehouseDto(service.findByPrincipal(principal));
  }

  @GetMapping
  public Iterable<DepartmentDetailsWithWarehouseDto> getAll() {
    return mapper.toDetailsWithWarehouseDto(service.findAll());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  public DepartmentDetailsWithWarehouseDto create(
      @RequestBody @Valid DepartmentCreationDto departmentCreationDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toDetailsWithWarehouseDto(service.create(departmentCreationDto));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  public DepartmentDetailsWithWarehouseDto update(
      @PathVariable Long id,
      @RequestBody @Valid DepartmentUpdateDto departmentUpdateDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toDetailsWithWarehouseDto(service.update(id, departmentUpdateDto));
  }

  @PatchMapping("/{id}/warehouse")
  @PreAuthorize("hasRole('WAREHOUSEMAN')")
  public DepartmentDetailsWithWarehouseDto changeProductQuantity(
      @PathVariable Long id,
      @RequestBody @Valid ProductQuantityToChangeDto productQuantityToChangeDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toDetailsWithWarehouseDto(
        service.changeProductQuantity(id, productQuantityToChangeDto));
  }
}
