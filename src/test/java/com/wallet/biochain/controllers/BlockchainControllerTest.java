package com.wallet.biochain.controllers;

import com.wallet.biochain.dto.BlockchainStatusDTO;
import com.wallet.biochain.dto.BlockDTO;
import com.wallet.biochain.entities.Blockchain;
import com.wallet.biochain.enums.ConsensusType;
import com.wallet.biochain.services.BlockService;
import com.wallet.biochain.services.BlockchainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlockchainController.class)
class BlockchainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockchainService blockchainService;

    @MockBean
    private BlockService blockService;

    @Test
    void initializeBlockchain_success() throws Exception {
        Blockchain blockchain = new Blockchain();
        blockchain.setChainId("chain1");
        blockchain.setName("Test Chain");
        
        when(blockchainService.initializeBlockchain(anyString(), any())).thenReturn(blockchain);

        mockMvc.perform(post("/api/blockchain/initialize")
                .param("name", "Test Chain")
                .param("consensusType", "PROOF_OF_WORK"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.chainId").value("chain1"));

        verify(blockchainService).initializeBlockchain("Test Chain", ConsensusType.PROOF_OF_WORK);
    }

    @Test
    void initializeBlockchain_failure() throws Exception {
        when(blockchainService.initializeBlockchain(anyString(), any()))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/blockchain/initialize")
                .param("name", "Test Chain")
                .param("consensusType", "PROOF_OF_WORK"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBlockchainStatus_success() throws Exception {
        BlockchainStatusDTO status = new BlockchainStatusDTO(
                "chain1", "Test Chain", 100, BigDecimal.valueOf(1000), 4, ConsensusType.PROOF_OF_WORK, true);
        
        when(blockchainService.getBlockchainStatus("chain1")).thenReturn(status);

        mockMvc.perform(get("/api/blockchain/chain1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chainId").value("chain1"))
                .andExpect(jsonPath("$.height").value(100));
    }

    @Test
    void getBlockchainStatus_notFound() throws Exception {
        when(blockchainService.getBlockchainStatus("chain1"))
                .thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(get("/api/blockchain/chain1/status"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBlockchains_success() throws Exception {
        Blockchain b1 = new Blockchain();
        b1.setChainId("chain1");
        Blockchain b2 = new Blockchain();
        b2.setChainId("chain2");
        
        when(blockchainService.getAllBlockchains()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/blockchain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBlockchainHeight_success() throws Exception {
        when(blockchainService.getBlockchainHeight("chain1")).thenReturn(100);

        mockMvc.perform(get("/api/blockchain/chain1/height"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100));
    }

    @Test
    void getBlockchainHeight_notFound() throws Exception {
        when(blockchainService.getBlockchainHeight("chain1"))
                .thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(get("/api/blockchain/chain1/height"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDifficulty_success() throws Exception {
        doNothing().when(blockchainService).updateDifficulty("chain1", 5);

        mockMvc.perform(put("/api/blockchain/chain1/difficulty")
                .param("difficulty", "5"))
                .andExpect(status().isOk());

        verify(blockchainService).updateDifficulty("chain1", 5);
    }

    @Test
    void updateDifficulty_failure() throws Exception {
        doThrow(new RuntimeException("Error"))
                .when(blockchainService).updateDifficulty("chain1", 5);

        mockMvc.perform(put("/api/blockchain/chain1/difficulty")
                .param("difficulty", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateBlockchain_success() throws Exception {
        when(blockchainService.validateBlockchain("chain1")).thenReturn(true);

        mockMvc.perform(get("/api/blockchain/chain1/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void getBlocks_success() throws Exception {
        BlockDTO block = new BlockDTO(1L, 1, "hash", "prevHash", 
                "merkle", null, 0L, 1234567890L, "miner", List.of());
        
        when(blockService.getBlocks("chain1", 0, 10)).thenReturn(List.of(block));

        mockMvc.perform(get("/api/blockchain/chain1/blocks")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getLatestBlocks_success() throws Exception {
        BlockDTO block = new BlockDTO(1L, 1, "hash", "prevHash", 
                "merkle", null, 0L, 1234567890L, "miner", List.of());
        
        when(blockService.getLatestBlocks("chain1", 5)).thenReturn(List.of(block));

        mockMvc.perform(get("/api/blockchain/chain1/blocks/latest")
                .param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBlockByHeight_success() throws Exception {
        BlockDTO block = new BlockDTO(1L, 1, "hash", "prevHash", 
                "merkle", null, 0L, 1234567890L, "miner", List.of());
        
        when(blockService.getBlockByHeight("chain1", 1)).thenReturn(java.util.Optional.of(block));

        mockMvc.perform(get("/api/blockchain/chain1/blocks/height/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockHeight").value(1));
    }

    @Test
    void getBlockByHeight_notFound() throws Exception {
        when(blockService.getBlockByHeight("chain1", 1)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/blockchain/chain1/blocks/height/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBlockByHash_success() throws Exception {
        BlockDTO block = new BlockDTO(1L, 1, "hash", "prevHash", 
                "merkle", null, 0L, 1234567890L, "miner", List.of());
        
        when(blockService.getBlockByHash("hash")).thenReturn(java.util.Optional.of(block));

        mockMvc.perform(get("/api/blockchain/chain1/blocks/hash/hash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash").value("hash"));
    }
}
