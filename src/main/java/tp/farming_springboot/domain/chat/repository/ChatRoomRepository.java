package tp.farming_springboot.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tp.farming_springboot.domain.chat.model.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}