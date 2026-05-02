package com.estore.service;

import com.estore.entity.UserAddress;
import com.estore.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressService {
    
    private final UserAddressRepository userAddressRepository;
    
    public List<UserAddress> getByUserId(Long userId) {
        return userAddressRepository.findByUserIdOrderByIsDefaultDesc(userId);
    }
    
    public UserAddress getByIdAndUserId(Long id, Long userId) {
        return userAddressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
    }
    
    public UserAddress getDefault(Long userId) {
        return userAddressRepository.findByUserIdAndIsDefault(userId, 1)
                .orElse(null);
    }
    
    @Transactional
    public UserAddress create(Long userId, UserAddress address) {
        address.setUserId(userId);
        
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        
        if (address.getIsDefault() == 1) {
            List<UserAddress> addresses = userAddressRepository.findByUserIdOrderByIsDefaultDesc(userId);
            for (UserAddress a : addresses) {
                if (a.getIsDefault() == 1) {
                    a.setIsDefault(0);
                    userAddressRepository.save(a);
                }
            }
        }
        
        return userAddressRepository.save(address);
    }
    
    @Transactional
    public UserAddress update(Long userId, Long id, UserAddress address) {
        UserAddress existingAddress = userAddressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        if (address.getName() != null) {
            existingAddress.setName(address.getName());
        }
        if (address.getPhone() != null) {
            existingAddress.setPhone(address.getPhone());
        }
        if (address.getProvince() != null) {
            existingAddress.setProvince(address.getProvince());
        }
        if (address.getCity() != null) {
            existingAddress.setCity(address.getCity());
        }
        if (address.getDistrict() != null) {
            existingAddress.setDistrict(address.getDistrict());
        }
        if (address.getDetail() != null) {
            existingAddress.setDetail(address.getDetail());
        }
        
        if (address.getIsDefault() != null && address.getIsDefault() == 1 && existingAddress.getIsDefault() != 1) {
            List<UserAddress> addresses = userAddressRepository.findByUserIdOrderByIsDefaultDesc(userId);
            for (UserAddress a : addresses) {
                if (a.getIsDefault() == 1) {
                    a.setIsDefault(0);
                    userAddressRepository.save(a);
                }
            }
            existingAddress.setIsDefault(1);
        }
        
        return userAddressRepository.save(existingAddress);
    }
    
    @Transactional
    public void delete(Long userId, Long id) {
        UserAddress address = userAddressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        userAddressRepository.delete(address);
    }
    
    @Transactional
    public UserAddress setDefault(Long userId, Long id) {
        List<UserAddress> addresses = userAddressRepository.findByUserIdOrderByIsDefaultDesc(userId);
        UserAddress targetAddress = null;
        
        for (UserAddress a : addresses) {
            if (a.getId().equals(id)) {
                targetAddress = a;
                a.setIsDefault(1);
            } else if (a.getIsDefault() == 1) {
                a.setIsDefault(0);
            }
            userAddressRepository.save(a);
        }
        
        if (targetAddress == null) {
            throw new RuntimeException("地址不存在");
        }
        
        return targetAddress;
    }
}
