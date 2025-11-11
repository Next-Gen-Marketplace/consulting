package next.gen.consulting.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RequestStatus {
    PENDING,
    PROGRESS,
    COMPLETED,
    REJECTED
}
