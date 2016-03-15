package netgloo.models;

/**
 * Created by roberthitchens3 on 3/7/16.
 */
public class AccountResponse {
    private int code;
    private String message;
    private Account objectCreated;

    public AccountResponse(int code, String message, Account objectCreated) {
        this.code = code;
        this.message = message;
        this.objectCreated = objectCreated;
        objectCreated.setGoal(0.0);
    }

    public AccountResponse() {
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Account getObjectCreated() {
        return objectCreated;
    }
}
