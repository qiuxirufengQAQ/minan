package com.minan.game.service;

import com.minan.game.mapper.ConversationRecordMapper;
import com.minan.game.mapper.NpcCharacterMapper;
import com.minan.game.mapper.SceneMapper;
import com.minan.game.model.ConversationRecord;
import com.minan.game.model.NpcCharacter;
import com.minan.game.model.Scene;
import com.minan.game.utils.AesEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 对话服务测试
 */
class ConversationServiceTest {

    @Mock
    private ConversationRecordMapper conversationRecordMapper;

    @Mock
    private NpcCharacterMapper npcCharacterMapper;

    @Mock
    private SceneMapper sceneMapper;

    @Mock
    private AiNpcService aiNpcService;

    @Mock
    private AiConfigService aiConfigService;

    @Mock
    private AesEncryptor aesEncryptor;

    @InjectMocks
    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 模拟加密器行为
        when(aesEncryptor.encrypt(any(String.class)))
            .thenAnswer(invocation -> "ENCRYPTED:" + invocation.getArgument(0));
        when(aesEncryptor.decrypt(any(String.class)))
            .thenAnswer(invocation -> invocation.getArgument(0).toString().replace("ENCRYPTED:", ""));
    }

    @Test
    void testIsOwner_WhenUserIsOwner() {
        // 给定
        String conversationId = "conv-123";
        Long userId = 100L;
        
        List<ConversationRecord> records = new ArrayList<>();
        ConversationRecord record = new ConversationRecord();
        record.setUserId("100");
        records.add(record);
        
        when(conversationRecordMapper.selectByConversationId(conversationId))
            .thenReturn(records);

        // 当
        boolean isOwner = conversationService.isOwner(conversationId, userId);

        // 则
        assertTrue(isOwner, "用户应该是对话所有者");
    }

    @Test
    void testIsOwner_WhenUserIsNotOwner() {
        // 给定
        String conversationId = "conv-123";
        Long userId = 200L;
        
        List<ConversationRecord> records = new ArrayList<>();
        ConversationRecord record = new ConversationRecord();
        record.setUserId("100");
        records.add(record);
        
        when(conversationRecordMapper.selectByConversationId(conversationId))
            .thenReturn(records);

        // 当
        boolean isOwner = conversationService.isOwner(conversationId, userId);

        // 则
        assertFalse(isOwner, "用户不应该是对话所有者");
    }

    @Test
    void testIsOwner_WhenConversationNotFound() {
        // 给定
        String conversationId = "conv-123";
        Long userId = 100L;
        
        when(conversationRecordMapper.selectByConversationId(conversationId))
            .thenReturn(new ArrayList<>());

        // 当
        boolean isOwner = conversationService.isOwner(conversationId, userId);

        // 则
        assertFalse(isOwner, "不存在的对话应该返回 false");
    }

    @Test
    void testGetConversationHistory_DecryptsContent() {
        // 给定
        String conversationId = "conv-123";
        
        List<ConversationRecord> encryptedRecords = new ArrayList<>();
        ConversationRecord record = new ConversationRecord();
        record.setUserInput("ENCRYPTED:你好");
        record.setNpcResponse("ENCRYPTED:你好，有什么可以帮助你的？");
        encryptedRecords.add(record);
        
        when(conversationRecordMapper.selectByConversationId(conversationId))
            .thenReturn(encryptedRecords);

        // 当
        List<ConversationRecord> decryptedRecords = conversationService.getConversationHistory(conversationId);

        // 则
        assertEquals(1, decryptedRecords.size());
        assertEquals("你好", decryptedRecords.get(0).getUserInput());
        assertEquals("你好，有什么可以帮助你的？", decryptedRecords.get(0).getNpcResponse());
    }
}
