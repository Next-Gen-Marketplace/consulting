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
public class RequestAdminNotificationHandler extends AbstractRequestActionHandler {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    protected void doHandle(RequestActionContext context) {
        RequestDto request = context.getRequest();
        if (request == null) {
            return;
        }

        switch (context.getActionType()) {
            case CREATED -> notifyAdmins("Создана новая заявка \"" + request.getDescription().substring(0,10) + "...\" (ID: " + request.getId() + ").");
            case STATUS_CHANGED -> {
                RequestStatus newStatus = request.getStatus();
                notifyAdmins("Заявка \"" + request.getDescription().substring(0,10) + "...\" (ID: " + request.getId() + ") изменила статус на " + newStatus + ".");
            }
            default -> {
            }
        }
    }

    private void notifyAdmins(String message) {
        List<User> admins = userRepository.findAllByRole(UserRole.ADMIN);
        if (admins.isEmpty()) {
            log.debug("No admins found for notification message: {}", message);
            return;
        }

        admins.stream()
                .map(User::getId)
                .forEach(adminId -> {
                    try {
                        notificationService.createNotification(adminId, message);
                    } catch (Exception ex) {
                        log.error("Failed to notify admin {}: {}", adminId, ex.getMessage(), ex);
                    }
                });
    }
}

