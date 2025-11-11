package next.gen.consulting.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import next.gen.consulting.dto.request.RequestDto;
import next.gen.consulting.model.RequestStatus;
import next.gen.consulting.model.User;
import next.gen.consulting.model.UserRole;
import next.gen.consulting.repository.UserRepository;
import next.gen.consulting.service.NotificationService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class RequestNotificationHandler extends AbstractRequestActionHandler {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    protected void doHandle(RequestActionContext context) {
        RequestDto request = context.getRequest();
        if (request == null) {
            return;
        }

        switch (context.getActionType()) {
            case CREATED -> notify("Создана новая заявка \"" + request.getDescription().substring(0,15) + ".");
            case STATUS_CHANGED -> {
                RequestStatus newStatus = request.getStatus();
                notifyAdmins("Заявка \"" + request.getDescription().substring(0,15) + "...\"  изменила статус на " + newStatus + ".");
            }
            default -> {
            }
        }
    }

    private void notify(String message) {
        List<User> users = userRepository.findAllByRole_AdminOrConsultant();
        if (users.isEmpty()) {
            log.debug("No users found for notification message: {}", message);
            return;
        }

        users.stream()
                .map(User::getId)
                .forEach(userId -> {
                    try {
                        notificationService.createNotification(userId, message);
                    } catch (Exception ex) {
                        log.error("Failed to notify user {}: {}", userId, ex.getMessage(), ex);
                    }
                });
    }

    private void notifyAdmins(String message) {
        List<User> users = userRepository.findAllByRole(UserRole.ADMIN);
        if (users.isEmpty()) {
            log.debug("No users found for notification message: {}", message);
            return;
        }

        users.stream()
                .map(User::getId)
                .forEach(userId -> {
                    try {
                        notificationService.createNotification(userId, message);
                    } catch (Exception ex) {
                        log.error("Failed to notify user {}: {}", userId, ex.getMessage(), ex);
                    }
                });
    }
}

