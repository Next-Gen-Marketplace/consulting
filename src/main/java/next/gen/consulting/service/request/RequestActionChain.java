package next.gen.consulting.service.request;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestActionChain {

    private final List<RequestActionHandler> handlers;

    @PostConstruct
    void initializeChain() {
        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).setNext(handlers.get(i + 1));
        }
    }

    @Async
    public void process(RequestActionContext context) {
        if (!handlers.isEmpty()) {
            handlers.get(0).handle(context);
        }
    }
}

