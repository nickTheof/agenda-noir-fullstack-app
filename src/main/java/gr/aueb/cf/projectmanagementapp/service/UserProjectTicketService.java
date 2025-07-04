package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.TicketCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketPatchDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketUpdateDTO;
import gr.aueb.cf.projectmanagementapp.mapper.Mapper;
import gr.aueb.cf.projectmanagementapp.model.Project;
import gr.aueb.cf.projectmanagementapp.model.Ticket;
import gr.aueb.cf.projectmanagementapp.repository.ProjectRepository;
import gr.aueb.cf.projectmanagementapp.repository.TicketRepository;
import gr.aueb.cf.projectmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProjectTicketService implements IUserProjectTicketService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<TicketReadOnlyDTO> getProjectTickets(String userUuid, String projectUuid) throws AppObjectNotFoundException {
        if (!userRepository.existsByUuid(userUuid)) {
            throw new AppObjectNotFoundException("User", "User with uuid " + userUuid + " not found");
        }
        if (!projectRepository.existsByUuidAndOwnerUuid(projectUuid, userUuid)) {
            throw new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found");
        }
        return ticketRepository.findByProjectUuid(projectUuid).stream()
                .map(mapper::mapToTicketReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public TicketReadOnlyDTO getProjectTicketByUuid(String userUuid, String projectUuid, String ticketUuid) throws AppObjectNotFoundException {
        return mapper.mapToTicketReadOnlyDTO(getValidatedTicket(userUuid, projectUuid, ticketUuid));
    }

    @Transactional
    @Override
    public TicketReadOnlyDTO createProjectTicket(String userUuid, String projectUuid, TicketCreateDTO createDTO) throws AppObjectNotFoundException {
        if (!userRepository.existsByUuid(userUuid)) {
            throw new AppObjectNotFoundException("User", "User with uuid " + userUuid + " not found");
        }
        Project project = projectRepository.findByUuidAndOwnerUuid(projectUuid, userUuid).orElseThrow(() -> new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found"));
        Ticket toCreate = mapper.mapToTicket(createDTO);
        toCreate.setProject(project);
        return mapper.mapToTicketReadOnlyDTO(ticketRepository.save(toCreate));
    }

    @Transactional
    @Override
    public TicketReadOnlyDTO updateProjectTicket(String userUuid, String projectUuid, String ticketUuid, TicketUpdateDTO updateDTO) throws AppObjectNotFoundException {
        Ticket ticket = getValidatedTicket(userUuid, projectUuid, ticketUuid);
        Ticket toUpdate = mapper.mapToTicket(updateDTO, ticket);
        return mapper.mapToTicketReadOnlyDTO(ticketRepository.save(toUpdate));
    }

    @Transactional
    @Override
    public TicketReadOnlyDTO updateProjectTicket(String userUuid, String projectUuid, String ticketUuid, TicketPatchDTO patchDTO) throws AppObjectNotFoundException {
        Ticket ticket = getValidatedTicket(userUuid, projectUuid, ticketUuid);
        Ticket toUpdate = mapper.mapToTicket(patchDTO, ticket);
        return mapper.mapToTicketReadOnlyDTO(ticketRepository.save(toUpdate));
    }

    @Transactional
    @Override
    public void deleteProjectTicket(String userUuid, String projectUuid, String ticketUuid) throws AppObjectNotFoundException {
        Ticket ticket = getValidatedTicket(userUuid, projectUuid, ticketUuid);
        ticketRepository.delete(ticket);
    }

    private Ticket getValidatedTicket(String userUuid, String projectUuid, String ticketUuid) throws AppObjectNotFoundException {
        if (!userRepository.existsByUuid(userUuid)) {
            throw new AppObjectNotFoundException("User", "User with uuid " + userUuid + " not found");
        }
        if (!projectRepository.existsByUuidAndOwnerUuid(projectUuid, userUuid)) {
            throw new AppObjectNotFoundException("Project", "Project with uuid " + projectUuid + " not found");
        }
        return ticketRepository.findByUuidAndProjectUuid(ticketUuid, projectUuid)
                .orElseThrow(() -> new AppObjectNotFoundException("Ticket",
                        "Ticket with uuid: " + ticketUuid + " not found"));
    }
}
