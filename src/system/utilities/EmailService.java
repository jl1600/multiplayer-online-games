package system.utilities;

public interface EmailService {
    /**
     * send a email to receiver email address of their tempPassword for reset password's temporary login
     * @param receiverEmail receiver email address
     * @param tempPassword tempPassword that will be sent and used for temporary login
     */
    void sendResetPasswordEmail(String receiverEmail, String tempPassword);
}
