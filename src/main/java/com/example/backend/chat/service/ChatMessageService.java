package com.example.backend.chat.service;

import com.example.backend.chat.domain.ChatMessage;
import com.example.backend.chat.domain.ChatRoom;
import com.example.backend.chat.domain.MessageType;
import com.example.backend.chat.domain.Type;
import com.example.backend.chat.dto.request.ChatMessageRequestDto;
import com.example.backend.chat.dto.response.ChatMessageResponseDto;
import com.example.backend.chat.repository.ChatMessageRepository;
import com.example.backend.chat.repository.ChatRoomRepository;
import com.example.backend.chat.repository.ParticipantRepository;
import com.example.backend.exception.CustomException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.user.domain.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void sendChatMessage(ChatMessageRequestDto message, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        ChatRoom room = chatRoomRepository.findById(message.getRoomId()).orElseThrow(
                () -> new CustomException(ErrorCode.ROOM_NOT_FOUND)
        );
        if (message.getType() == MessageType.QUIT) {
            if (room.getType() == Type.PRIVATE) {
                message.setSender("[알림]");
                message.setMessage(user.getUsername() + "님이 채팅방을 나가셨습니다. 새로운 채팅방에서 채팅을 진행해 주세요!");
            } else {
                message.setSender("[알림]");
                message.setMessage(user.getUsername() + "님이 채팅방을 나가셨습니다");
            }
        }
        this.saveChatMessage(message);
        messageSendingOperations.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    // 페이징으로 받아서 무한 스크롤 가능할듯
    public Page<ChatMessageResponseDto> getSavedMessages(String roomId) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by("createdDate").descending());
        Page<ChatMessage> messagePage = chatMessageRepository.findAllByRoomId(pageable, roomId);
        return messagePage.map(ChatMessageResponseDto::new);
    }

    @Transactional
    public void saveChatMessage(ChatMessageRequestDto message) {

        // if 문 안에서 participant 숫자로 read 숫자를 계산
        // ChatMessage 생성 하면서 Reader 함께 생성 -아직 안함-

        if (!Objects.equals(message.getSender(), "[알림]")) {
            User user = userRepository.findByUsername(message.getSender()).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            chatMessageRepository.save(new ChatMessage(message, user));
        }
        else {
            chatMessageRepository.save(new ChatMessage(message));
        }
    }
}
