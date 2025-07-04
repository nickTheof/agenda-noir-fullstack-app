package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.RoleReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.UserRoleInsertDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Role;
import gr.aueb.cf.projectmanagementapp.model.User;
import gr.aueb.cf.projectmanagementapp.repository.RoleRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService implements IUserRoleService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<RoleReadOnlyDTO> findAllUserRoles(String uuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        return user.getAllRoles().stream().map(mapper::mapToRoleReadOnlyDTO).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class})
    @Override
    public List<RoleReadOnlyDTO> changeUserRoles(String uuid, UserRoleInsertDTO dto) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        for (String roleName : dto.roleNames()){
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new AppObjectNotFoundException("Role", "Role with name " + roleName + " not found"));
            user.addRole(role);
        }
        User updatedUser = userRepository.save(user);
        return updatedUser.getAllRoles().stream().map(mapper::mapToRoleReadOnlyDTO).collect(Collectors.toList());
    }
}
