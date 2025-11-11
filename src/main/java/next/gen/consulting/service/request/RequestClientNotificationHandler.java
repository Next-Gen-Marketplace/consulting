package next.gen.consulting.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import next.gen.consulting.dto.request.RequestDto;
import next.gen.consulting.model.RequestStatus;
import next.gen.consulting.service.NotificationService;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestClientNotificationHandler extends AbstractRequestActionHandler {

    private final NotificationService notificationService;

    @Override
    protected void doHandle(RequestActionContext context) {
        RequestDto request = context.getRequest();
        if (request == null || request.getClientId() == null) {
            return;
        }

        switch (context.getActionType()) {
            case CREATED -> notifyClient(
                    request.getClientId(),
                    "Ваша заявка \"" + request.getDescription().substring(0,15) + "...\" успешно создана и ожидает обработки."
            );
            case STATUS_CHANGED -> {
                RequestStatus newStatus = request.getStatus();
                notifyClient(
                        request.getClientId(),
                        "Статус вашей заявки \"" + request.getDescription().substring(0,15) + "...\" изменён на " + newStatus + "."
                );
            }
            default -> {
            }
        }
    }

    private void notifyClient(UUID clientId, String message) {
        try {
            notificationService.createNotification(clientId, message);
        } catch (Exception ex) {
            log.error("Failed to create notification for client {}: {}", clientId, ex.getMessage(), ex);
        }
    }
}

