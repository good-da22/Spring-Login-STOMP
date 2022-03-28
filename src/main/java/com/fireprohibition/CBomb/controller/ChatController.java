package com.fireprohibition.CBomb.controller;


import com.fireprohibition.CBomb.model.ChatMessage;
import com.fireprohibition.CBomb.pubsub.RedisPublisher;
import com.fireprohibition.CBomb.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    /**
     * basic websocket server
     */
//    private final ChatService chatService;
//
//    @PostMapping
//    public ChatRoom createRoom(@RequestParam String name) {
//        return chatService.createRoom(name);
//    }
//
//    @GetMapping
//    public List<ChatRoom> findAllRoom() {
//        return chatService.findAllRoom();
//    }

    /**
     * STMOP
     */
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    @MessageMapping("/chat/message")
//    public void message(ChatMessage message) {
//        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
//            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
//        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
//    }

    /**
     * Redis Pub/Sub
     */
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    //websocket "pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            //채팅방 입장시 채팅방(topic)에서 대화가 가능하도록 리스너를 연동하는 enterChatRoom 메서드 세팅
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        //websocket에 발행된 메시지를 서로 다른 서버로 공유하기 위해 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
