package next.gen.consulting.service.request;

public interface RequestActionHandler {

    void setNext(RequestActionHandler nextHandler);

    void handle(RequestActionContext context);
}

