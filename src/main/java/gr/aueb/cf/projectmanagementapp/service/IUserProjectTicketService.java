package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.projectmanagementapp.dto.TicketCreateDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketPatchDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketReadOnlyDTO;
import gr.aueb.cf.projectmanagementapp.dto.TicketUpdateDTO;

import java.util.List;

public interface IUserProjectTicketService {
    List<TicketReadOnlyDTO> getProjectTickets(String userUuid, String projectUuid) throws AppObjectNotFoundException;
    TicketReadOnlyDTO getProjectTicketByUuid(String userUuid, String projectUuid, String ticketUuid) throws AppObjectNotFoundException;
    TicketReadOnlyDTO createProjectTicket(String userUuid, String projectUuid, TicketCreateDTO createDTO) throws AppObjectNotFoundException;
    TicketReadOnlyDTO updateProjectTicket(String userUuid, String projectUuid, String ticketUuid, TicketUpdateDTO updateDTO) throws AppObjectNotFoundException;
    TicketReadOnlyDTO updateProjectTicket(String userUuid, String projectUuid, String ticketUuid, TicketPatchDTO patchDTO) throws AppObjectNotFoundException;
    void deleteProjectTicket(String userUuid, String projectUuid, String ticketUuid) throws AppObjectNotFoundException;
}
