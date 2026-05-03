package com.inventory.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements Serializable {

    private Long userId;
    private String username;
    private String role;
}
