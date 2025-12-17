package com.wallet.biochain.controllers;

import com.wallet.biochain.dto.WalletBalanceDTO;
import com.wallet.biochain.dto.WalletCreationDTO;
import com.wallet.biochain.dto.WalletResponseDTO;
import com.wallet.biochain.services.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void createWallet_success() throws Exception {
        WalletCreationDTO dto = new WalletCreationDTO("address", "publicKey", "privateKey");
        
        when(walletService.createWallet()).thenReturn(dto);

        mockMvc.perform(post("/api/wallets"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("address"))
                .andExpect(jsonPath("$.publicKey").value("publicKey"))
                .andExpect(jsonPath("$.privateKey").value("privateKey"));

        verify(walletService).createWallet();
    }

    @Test
    void createWallet_failure() throws Exception {
        when(walletService.createWallet()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/wallets"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallet_found() throws Exception {
        WalletResponseDTO dto = new WalletResponseDTO(
                1L, "address", "publicKey", BigDecimal.TEN, LocalDateTime.now(), 5);
        
        when(walletService.getWallet("address")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/wallets/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("address"))
                .andExpect(jsonPath("$.balance").value(10));
    }

    @Test
    void getWallet_notFound() throws Exception {
        when(walletService.getWallet("address")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/wallets/address"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllWallets_success() throws Exception {
        WalletResponseDTO dto1 = new WalletResponseDTO(
                1L, "address1", "publicKey1", BigDecimal.TEN, LocalDateTime.now(), 5);
        WalletResponseDTO dto2 = new WalletResponseDTO(
                2L, "address2", "publicKey2", BigDecimal.ONE, LocalDateTime.now(), 3);
        
        when(walletService.getAllWallets()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getWalletBalance_found() throws Exception {
        WalletBalanceDTO dto = new WalletBalanceDTO("address", BigDecimal.TEN);
        
        when(walletService.getWalletBalance("address")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/wallets/address/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("address"))
                .andExpect(jsonPath("$.balance").value(10));
    }

    @Test
    void getWalletBalance_notFound() throws Exception {
        when(walletService.getWalletBalance("address")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/wallets/address/balance"))
                .andExpect(status().isNotFound());
    }

    @Test
    void validateAddress_valid() throws Exception {
        when(walletService.validateAddress("address")).thenReturn(true);

        mockMvc.perform(get("/api/wallets/validate/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void validateAddress_invalid() throws Exception {
        when(walletService.validateAddress("address")).thenReturn(false);

        mockMvc.perform(get("/api/wallets/validate/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}
