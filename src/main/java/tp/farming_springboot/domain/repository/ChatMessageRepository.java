package tp.farming_springboot.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tp.farming_springboot.domain.entity.ChatMessage;
import tp.farming_springboot.domain.entity.MessageStatus;

import java.util.List;

public interface ChatMessageRepository
        extends MongoRepository<ChatMessage, String> {

    long countBySenderIdAndRecipientIdAndStatus(
            String senderId, String recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);
}
