package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreationDto;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityToChangeDto;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.mapper.DepartmentMapper;
import com.shalhlad.product_delivery_service.service.DepartmentService;
import com.shalhlad.product_delivery_service.util.Utils;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
  public DepartmentDetailsDto getById(@PathVariable Long id) {
    return mapper.toDetailsDto(service.findById(id));
  }

  @GetMapping
  public Iterable<DepartmentDetailsDto> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "25") int size) {
    return mapper.toDetailsDto(service.findAll(PageRequest.of(page, size)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('DB_EDITOR')")
  public DepartmentDetailsDto create(
      @RequestBody @Valid DepartmentCreationDto departmentCreationDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toDetailsDto(service.create(departmentCreationDto));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('WAREHOUSEMAN')")
  public Department changeProductQuantity(
      @PathVariable Long id,
      @RequestBody @Valid ProductQuantityToChangeDto productQuantityToChangeDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.changeProductQuantity(id, productQuantityToChangeDto);
  }
}
