package gr.aueb.cf.projectmanagementapp.authentication;

import gr.aueb.cf.projectmanagementapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    public boolean hasOwnership(User requester, String targetUuid) {
        return requester.getUuid().equals(targetUuid);
    }

    public boolean hasAuthority(User requester, String authority) {
        return requester.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}
