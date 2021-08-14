package shared.request.user_request;

public class BanUserRequest extends UserRequest {
    private final String userId;
    private final int banLength;
    private final String adminId;

    public BanUserRequest(String sessionID, String adminId, String userId, int banLength) {
        super(sessionID);
        this.banLength = banLength;
        this.userId = userId;
        this.adminId = adminId;
    }

    public String getUserId() {
        return userId;
    }

    public int getBanLength() {
        return banLength;
    }

    public String getAdminId() {
        return adminId;
    }
}
