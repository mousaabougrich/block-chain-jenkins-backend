package com.wallet.biochain.dto;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String username,
        String email,
        String fullName,
        String phoneNumber,
        Boolean isActive,
        Integer walletCount,
        LocalDateTime createdAt
) {}