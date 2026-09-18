package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TicketEntity;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
	
	Optional<TicketEntity> findByIdAndIsDeleteFalse(Long id);

	@Query("SELECT t.id, t.ticketcode, t.ticketcodecounter, t.interactionid, t.interactionname, t.interactiontype, t.ticketfrom, t.actualclosedate, t.expactedclosedate, t.usermsg, t.clientmsg, t.assigntouserid, t.assigntoname, t.isDelete, t.createdAt, u.id, CONCAT(u.firstName,' ',u.lastName), t.documentpath,t.status FROM TicketEntity t LEFT JOIN InteractionEntity i ON i.id = t.interactionid LEFT JOIN UserMasterEntity u ON u.id = t.userid WHERE t.isDelete = false")
    List<Object[]> fetchAllTicketsOptimized();


    @Query("SELECT t.id, t.ticketcode, t.ticketcodecounter, t.interactionid, t.interactionname, t.interactiontype, t.ticketfrom, t.actualclosedate, t.expactedclosedate, t.usermsg, t.clientmsg, t.assigntouserid, t.assigntoname, t.isDelete, t.createdAt, u.id, CONCAT(u.firstName,' ',u.lastName), t.documentpath,t.status FROM TicketEntity t LEFT JOIN InteractionEntity i ON i.id = t.interactionid LEFT JOIN UserMasterEntity u ON u.id = t.userid WHERE t.isDelete = false AND t.userid = :userId")
    List<Object[]> fetchTicketsByUserIdOptimized(@Param("userId") Long userId);


    @Query("SELECT MAX(t.ticketcodecounter) FROM TicketEntity t")
    Long findMaxTicketCounter();
    
    @Modifying
    @Transactional
    @Query("UPDATE TicketEntity t SET t.isDelete = true WHERE t.id = :id")
    int softDeleteTicket(@Param("id") Long id);
}