package com.example.backend.chat.controller;

import com.example.backend.chat.dto.ChatMessageRequestDto;
import com.example.backend.chat.service.ChatMessageService;
import com.example.backend.user.common.LoadUser;
import com.example.backend.user.token.AuthToken;
import com.example.backend.user.token.AuthTokenProvider;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final AuthTokenProvider tokenProvider;

    @ApiOperation(value = "메세지 전송(/pub)")
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequestDto message, @Header("Authorization") String tokenStr) {
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
        String name = token.getTokenClaims().getId();

        // 입장, 퇴장 시 Participant 에 추가
        if (ChatMessageRequestDto.MessageType.ENTER.equals((message.getType()))) {
            chatMessageService.addParticipant(LoadUser.getEmail(), message.getRoomId());
            message.setMessage(name + "님이 입장했습니다");
        } else if (ChatMessageRequestDto.MessageType.QUIT.equals((message.getType()))) {
            chatMessageService.deleteParticipant(LoadUser.getEmail(), message.getRoomId());
            message.setMessage(name + "님이 퇴장했습니다");
        } else {
            message.setSender(name);
        }

        chatMessageService.sendChatMessage(message);
    }

}