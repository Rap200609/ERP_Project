package edu.univ.erp.service.auth;

import edu.univ.erp.data.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {
    private final UserRepository userRepository;

    public PasswordService() {
        this(new UserRepository());
    }

    public PasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean verifyCurrentPassword(int userId, String currentPassword) throws Exception {
        return userRepository.getPasswordHash(userId)
                .map(hash -> {
                    try {
                        if (hash != null && hash.startsWith("$2a$")) {
                            return BCrypt.checkpw(currentPassword, hash);
                        } else {
                            return currentPassword.equals(hash);
                        }
                    } catch (Exception ex) {
                        return currentPassword.equals(hash);
                    }
                })
                .orElse(false);
    }

    public void changePassword(int userId, String newPassword) throws Exception {
        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userRepository.updatePassword(userId, newHash);
    }
}

