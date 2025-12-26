package com.myproject.service.impl;

import com.myproject.common.UserStatus;
import com.myproject.dto.request.UserCreationRequest;
import com.myproject.dto.request.UserPasswordRequest;
import com.myproject.dto.request.UserUpdateRequest;
import com.myproject.dto.response.UserPageResponse;
import com.myproject.dto.response.UserResponse;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.AddressEntity;
import com.myproject.model.Role;
import com.myproject.model.UserEntity;
import com.myproject.model.UserHasRole;
import com.myproject.repository.AddressRepository;
import com.myproject.repository.RoleRepository;
import com.myproject.repository.UserHasRoleRepository;
import com.myproject.repository.UserRepository;
import com.myproject.service.EmailService;
import com.myproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final UserHasRoleRepository userHasRoleRepository;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all users");

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // column_name:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()){
                if (matcher.group(3).equalsIgnoreCase("asc")){
                    order = new Sort.Order(Sort.Direction.ASC, matcher.group(1));
                } else if (matcher.group(3).equalsIgnoreCase("desc")){
                    order = new Sort.Order(Sort.Direction.DESC, matcher.group(1));
                }
            }
        }

        // when Fe want to get page 1, it will return page 0
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        // Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<UserEntity> entityPage;

        if (StringUtils.hasLength(keyword)){
            // call search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = userRepository.searchByKeyword(keyword, pageable);
        } else {

            entityPage = userRepository.findAll(pageable);
        }

        return getUserPageResponse(page, size, entityPage);
    }

    @Override
    public UserResponse findById(Long id) {
        log.info("Find user by id: {}", id);

        UserEntity user = getUserEntity(id);

        return UserResponse.builder()
                .id(id)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .username(user.getUsername())
                .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest req) {
        log.info("Saving user: {}", req);

        UserEntity userByEmail = userRepository.findByEmail(req.getEmail());
        if (userByEmail != null) {
            throw new InvalidDataException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setType(req.getType());
        user.setStatus(UserStatus.ACTIVE);



        UserEntity result = userRepository.save(user);
        log.info("User saved: {}", user);

        if (result.getId() != null) {
            log.info("User id: {}", result.getId());
            List<AddressEntity> addresses = new ArrayList<>();
            req.getAddresses().forEach(address -> {
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setApartmentNumber(address.getApartmentNumber());
                addressEntity.setStreet(address.getStreet());
                addressEntity.setStreetNumber(address.getStreetNumber());
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setFloor(address.getFloor());
                addressEntity.setBuilding(address.getBuilding());
                addressEntity.setAddressType(address.getAddressType());
                addressEntity.setUserId(result.getId());
                addresses.add(addressEntity);
            });

            addressRepository.saveAll(addresses);
            log.info("Addresses saved: {}", addresses);
        }

        // set roles
        UserHasRole userHasRole = new UserHasRole();
        userHasRole.setUser(user);

        // role user default
        Role userRole = roleRepository.findByName("user");
        userHasRole.setRole(userRole);

        userHasRoleRepository.save(userHasRole);

        // Send email verification
        try {
            emailService.emailVerification(user.getEmail(), user.getFirstName());
        } catch (IOException e) {
            throw new InvalidDataException("Send email failed");
        }

        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Updating user: {}", req);
        // Get user by id
        UserEntity user = getUserEntity(req.getId());
        // set data
        if (req.getFirstName() != null) {
            user.setFirstName(req.getFirstName());
        }

        if (req.getLastName() != null) {
            user.setLastName(req.getLastName());
        }

        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }

        if (req.getGender() != null){
            user.setGender(req.getGender());
        }

        if (req.getBirthday() != null){
            user.setBirthday(req.getBirthday());
        }
        if (req.getPhone() != null){
            user.setPhone(req.getPhone());
        }

        if (req.getUsername() != null){
            user.setUsername(req.getUsername());
        }
        // save to db
        userRepository.save(user);
        log.info("User updated: {}", user);

        if (req.getAddresses() != null){
            // Save address
            List<AddressEntity> addresses = new ArrayList<>();

            req.getAddresses().forEach(address -> {
                AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(user.getId(), address.getAddressType());

                if (addressEntity == null) {
                    addressEntity = new AddressEntity();
                }

                addressEntity.setApartmentNumber(address.getApartmentNumber());
                addressEntity.setStreet(address.getStreet());
                addressEntity.setStreetNumber(address.getStreetNumber());
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setFloor(address.getFloor());
                addressEntity.setBuilding(address.getBuilding());
                addressEntity.setAddressType(address.getAddressType());
                addressEntity.setUserId(user.getId());

                addresses.add(addressEntity);
            });
            addressRepository.saveAll(addresses);
        }
    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing password: {}", req);

        // Get user by id
        UserEntity user = getUserEntity(req.getId());
        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(user);
            log.info("Password changed: {}", user);
        } else {
            log.error("Password not match");
        }

    }

    @Override
    public void delete(Long id) {
        log.info("Deleting user: {}", id);

        UserEntity user = getUserEntity(id);
        user.setStatus(UserStatus.INACTIVE);

        userRepository.save(user);
        log.info("User deleted: {}", user);

    }

    /**
     * Get user by id
     * @param id
     * @return
     */
    private UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Convert Page<UserEntity> to UserPageResponse
     * @param page
     * @param size
     * @param userEntities
     * @return
     */
    private static UserPageResponse getUserPageResponse(int page, int size, Page<UserEntity> userEntities) {
        log.info("Convert Page<UserEntity> to UserPageResponse");
        List<UserResponse> userList = userEntities.stream().map(
                entity -> UserResponse.builder()
                        .id(entity.getId())
                        .firstName(entity.getFirstName())
                        .lastName(entity.getLastName())
                        .phone(entity.getPhone())
                        .username(entity.getUsername())
                        .email(entity.getEmail())
                        .birthday(entity.getBirthday())
                        .gender(entity.getGender())
                        .build()
        ).toList();

        UserPageResponse response = new UserPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(userEntities.getTotalElements());
        response.setTotalPages(userEntities.getTotalPages());
        response.setUsers(userList);
        return response;
    }

    @Override
    public Long findUserIdByUsername(String username) {
        log.info("Find user id by username: {}", username);

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        return user.getId();
    }
}
