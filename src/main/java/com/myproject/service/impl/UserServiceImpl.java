package com.myproject.service.impl;

import com.myproject.common.UserStatus;
import com.myproject.controller.request.UserCreationRequest;
import com.myproject.controller.request.UserPasswordRequest;
import com.myproject.controller.request.UserUpdateRequest;
import com.myproject.controller.response.UserResponse;
import com.myproject.model.AddressEntity;
import com.myproject.model.UserEntity;
import com.myproject.repository.AddressRepository;
import com.myproject.repository.UserRepository;
import com.myproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public List<UserResponse> findAll() {
        return List.of();
    }

    @Override
    public UserResponse findById(Long id) {
        return null;
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
        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setType(req.getType());
        user.setStatus(UserStatus.NONE);
        userRepository.save(user);
        log.info("User saved: {}", user);

        if (user.getId() != null) {
            log.info("User id: {}", user.getId());
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
                addressEntity.setUser(user);
                addresses.add(addressEntity);
            });

            addressRepository.saveAll(addresses);
            log.info("Addresses saved: {}", addresses);
        }

        return user.getId();
    }

    @Override
    public int update(UserUpdateRequest req) {
        return 0;
    }

    @Override
    public void changePassword(UserPasswordRequest req) {

    }

    @Override
    public void delete(Long id) {

    }
}
