package next.gen.consulting.service.request;

public abstract class AbstractRequestActionHandler implements RequestActionHandler {

    private RequestActionHandler nextHandler;

    @Override
    public void setNext(RequestActionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(RequestActionContext context) {
        doHandle(context);
        if (nextHandler != null) {
            nextHandler.handle(context);
        }
    }

    protected abstract void doHandle(RequestActionContext context);
}

