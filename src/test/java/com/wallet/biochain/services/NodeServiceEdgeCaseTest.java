package com.wallet.biochain.services;

import com.wallet.biochain.dto.NodeConnectionRequestDTO;
import com.wallet.biochain.entities.Node;
import com.wallet.biochain.enums.NodeStatus;
import com.wallet.biochain.enums.NodeType;
import com.wallet.biochain.mappers.NodeMapper;
import com.wallet.biochain.repositories.NodeRepository;
import com.wallet.biochain.services.impl.NodeServiceImpl;
import com.wallet.biochain.config.NetworkConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceEdgeCaseTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private NodeMapper nodeMapper;

    @Mock
    private NetworkConfig networkConfig;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Test
    void getNodeLatency_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.getNodeLatency("nonexistent"));
    }

    @Test
    void trustNode_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.trustNode("nonexistent"));
    }

    @Test
    void untrustNode_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.untrustNode("nonexistent"));
    }

    @Test
    void updateNodeStatus_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.updateNodeStatus("nonexistent", NodeStatus.ACTIVE));
    }

    @Test
    void updateBlockHeight_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.updateBlockHeight("nonexistent", 100));
    }

    @Test
    void connectToPeer_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("node1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.connectToPeer("node1", "node2"));
    }

    @Test
    void connectToPeer_peerNotFound_throwsException() {
        Node node = new Node();
        when(nodeRepository.findByNodeId("node1")).thenReturn(Optional.of(node));
        when(nodeRepository.findByNodeId("node2")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.connectToPeer("node1", "node2"));
    }

    @Test
    void disconnectFromPeer_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("node1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.disconnectFromPeer("node1", "node2"));
    }

    @Test
    void disconnectFromPeer_peerNotFound_throwsException() {
        Node node = new Node();
        when(nodeRepository.findByNodeId("node1")).thenReturn(Optional.of(node));
        when(nodeRepository.findByNodeId("node2")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.disconnectFromPeer("node1", "node2"));
    }

    @Test
    void getPeerList_nodeNotFound_throwsException() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> nodeService.getPeerList("nonexistent"));
    }

    @Test
    void getNodeById_notFound_returnsEmpty() {
        when(nodeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional result = nodeService.getNodeById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getNodeByNodeId_notFound_returnsEmpty() {
        when(nodeRepository.findByNodeId("nonexistent")).thenReturn(Optional.empty());

        Optional result = nodeService.getNodeByNodeId("nonexistent");

        assertFalse(result.isPresent());
    }
}
