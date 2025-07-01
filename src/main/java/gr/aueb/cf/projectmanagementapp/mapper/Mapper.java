package gr.aueb.cf.projectmanagementapp.mapper;

import gr.aueb.cf.projectmanagementapp.dto.UserReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRegisterDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserUpdateDTO;
import gr.aueb.cf.projectmanagementapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Mapper {
    private final PasswordEncoder passwordEncoder;

    public User mapToUser(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(user.getId(), user.getUuid(), user.getUsername(), user.getFirstname(), user.getLastname(), user.getEnabled(), user.getVerified(), user.getIsDeleted(), user.getLoginConsecutiveFailAttempts());
    }

    public User mapToUser(UserUpdateDTO dto, User user) {
        if (dto.password() != null) {
            user.updatePassword(passwordEncoder.encode(dto.password()));
        }
        if (dto.firstname() != null) {
            user.setFirstname(dto.firstname());
        }
        if (dto.lastname() != null) {
            user.setLastname(dto.lastname());
        }
        if (dto.enabled() != null) {
            user.setEnabled(dto.enabled());
        }
        if (dto.verified() != null) {
            user.setVerified(dto.verified());
        }
        return user;
    }
}
