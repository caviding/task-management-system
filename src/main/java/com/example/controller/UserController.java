package com.example.controller;

import com.example.Enum.Role;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.UserUpdateResponse;
import jakarta.validation.Valid;
import com.example.entity.RootEntity;
import com.example.dto.response.UserResponseDto;
import com.example.service.IUserService;
import com.example.dto.request.UserRequestDto;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<Page<@NonNull UserResponseDto>> getAllUsers(@RequestParam(name = "pageNumber",defaultValue = "0") @Min(value = 0,message = "Page number cannot be negative !!") int pageNumber,
                                                                  @RequestParam(name = "pageSize",defaultValue = "5") @Min(value = 1,message = "Page size cannot be lower than 1 !!") int pageSize,
                                                                  @RequestParam(name = "sort",defaultValue = "id") String sort) {
        Sort.Order order = Sort.Order.asc(sort);
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(order));
        return RootEntity.success(userService.getAllUsers(pageable), HttpStatus.OK);
    }

    @GetMapping("/me")
    public RootEntity<UserResponseDto> getProfile() {
        return RootEntity.success(userService.getProfile(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<UserResponseDto> createUser(@RequestBody @Valid RegisterRequest registerRequest,
                                                         @RequestParam(name = "role",defaultValue = "USER") Role role) {
        return RootEntity.success(userService.createUser(registerRequest, role), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<Void> deleteUser(@PathVariable @Positive(message = "Id cannot be negative !1") Long id) {
        userService.deleteUser(id);
        return RootEntity.success(null, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<UserResponseDto> getUserById(@PathVariable @Positive(message = "Id cannot be negative !1") Long id) {
        return RootEntity.success(userService.getUserById(id),HttpStatus.OK);
    }

    @PutMapping({"/{id}","/{id}/"})
    @PreAuthorize("hasRole('ADMIN') or @userServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<UserUpdateResponse> updateUser(@PathVariable @Positive(message = "Id cannot be negative !1") Long id,
                                                     @RequestBody @Valid UserRequestDto userRequestDto) {
        return RootEntity.success(userService.updateUser(id, userRequestDto),HttpStatus.OK);
    }
}
