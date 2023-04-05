package uz.platform.forestyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.platform.forestyapp.entity.OnlineUser;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.TextMessageDTO;
import uz.platform.forestyapp.repository.OnlineUserRepo;

import java.security.Principal;
import java.util.Date;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping
public class TextMessageController {
    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    OnlineUserRepo onlineUserRepo;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody TextMessageDTO textMessageDTO) {
        template.convertAndSend("/topic/message", textMessageDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @Scheduled(fixedRate = 15000)
//    public void sendDate(){
//        Date date = new Date(System.currentTimeMillis());
//        TextMessageDTO textMessageDTO = new TextMessageDTO(date.toString());
//
//            onlineUserRepo.findAll().forEach(item->template.convertAndSendToUser(item.getSessionId(),"/topic/message","Ha nima deysan"));
//        template.convertAndSendToUser("jahon99king@gmail.com","/topic/message","Ha nima deysan");
//
//    }

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload TextMessageDTO textMessageDTO) {
        System.out.println(textMessageDTO.getMessage());
    }



    @SendTo("/topic/message")
    public TextMessageDTO broadcastMessage(@Payload TextMessageDTO textMessageDTO) {
        return textMessageDTO;
    }

    @MessageMapping("/message")
    @SendToUser("/topic/message")
    public TextMessageDTO broadcastMessageUser(TextMessageDTO textMessageDTO, Principal principal) {
        System.out.println("dffdsfdfg");
        OnlineUser onlineUser = new OnlineUser(new User(),principal.getName());
        onlineUserRepo.save(onlineUser);
        return textMessageDTO;
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();

    }
}
