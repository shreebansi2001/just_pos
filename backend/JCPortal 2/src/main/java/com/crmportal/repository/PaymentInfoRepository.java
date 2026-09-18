package com.crmportal.repository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.PaymentOrderDto;
import com.crmportal.entity.UserPlansHistoryEntity;
@Repository
public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
	List<PaymentInfo> getByOdId(String odId);

	@Query("SELECT new com.crmportal.response.dto.PaymentOrderDto(o.user.id,o.userpaymentid,o.payid,o.paysignature,o.amount,o.paidamount, o.surcharge,o.internalorderid,o.paymentdone,o.paymentdonetimestamp"
			+ ",CONCAT(o.user.firstName, ' ', o.user.lastName),o.user.contactNo,o.user.email) from #{#entityName} o where o.id =:id")
	PaymentOrderDto getPaymentResponse(Long id);

	@Query("SELECT new com.crmportal.response.dto.PaymentOrderDto(o.user.id,o.userpaymentid,o.payid,o.paysignature,o.amount,o.paidamount, o.surcharge,o.internalorderid,o.paymentdone,o.paymentdonetimestamp"
			+ ",CONCAT(o.user.firstName, ' ', o.user.lastName),o.user.contactNo,o.user.email) from #{#entityName} o order by o.paymentdonetimestamp")
	List<PaymentOrderDto> getAllPaymentDetails();

	PaymentInfo findByIdAndUser(Long id, UserMasterEntity user);

	List<PaymentInfo> findAllByUser(UserMasterEntity user);
	
	@Query("SELECT p.userPlanHist.plan.id,p.userPlanHist.plan.name, COALESCE(SUM(p.paidamount), 0) FROM PaymentInfo p WHERE p.paymentdone = true GROUP BY p.userPlanHist.plan.id")
	List<Object[]> getPlanWiseTotalAmountReceived();
	
	@Query("SELECT MONTH(p.userPlanHist.createdAt), YEAR(p.userPlanHist.createdAt), COALESCE(SUM(p.paidamount), 0) FROM PaymentInfo p WHERE p.paymentdone = true AND p.userPlanHist.createdAt BETWEEN :start AND :end GROUP BY YEAR(p.userPlanHist.createdAt), MONTH(p.userPlanHist.createdAt)")
	List<Object[]> getMonthWiseTotalNoPlan(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	
	@Query("SELECT MONTH(p.userPlanHist.createdAt), YEAR(p.userPlanHist.createdAt), COALESCE(SUM(p.paidamount), 0) FROM PaymentInfo p WHERE p.paymentdone = true AND p.userPlanHist.plan.id = :planId AND p.userPlanHist.createdAt BETWEEN :start AND :end GROUP BY YEAR(p.userPlanHist.createdAt), MONTH(p.userPlanHist.createdAt)")
	List<Object[]> getMonthWiseTotalByPlan(@Param("planId") Long planId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query(
		    value = "SELECT SUM(up.paidamount)  FROM userpaymentinfo up  WHERE up.user_plan_id = :id  GROUP BY up.user_plan_id",
		    nativeQuery = true
		)
		BigDecimal getByUserPlanHistId(@Param("id") Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	PaymentInfo findByIdAndIsDeleteFalse(Long id);

	List<PaymentInfo> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	@Query(
		    value = "SELECT COALESCE(SUM(up.paidamount), 0)  FROM userpaymentinfo up  JOIN user_plan_histories uph ON up.user_plan_id = uph.plan_history_id WHERE uph.is_active = true",
		    nativeQuery = true
		)
	Long getPaidAmt();

	@Query(value = ""
			+ " SELECT "
			+ "	 	SUM(COALESCE(uph.plan_base_amount, 0)) AS total_amount, "
			+ " 	SUM(COALESCE(upi.paidamount, 0)) AS paid_amount, "
			+ "  	SUM(COALESCE(uph.plan_base_amount, 0)) - SUM(COALESCE(upi.paidamount, 0)) AS remaing_amount "
			+ " FROM users u"
			+ " LEFT JOIN user_basic_details ubd ON u.user_id = ubd.user_id "
			+ " LEFT JOIN user_plan_histories uph ON uph.user_id = u.user_id AND uph.is_active = TRUE "
			+ " LEFT JOIN userpaymentinfo upi ON upi.user_plan_id = uph.plan_history_id "
			+ " WHERE "
			+ " 	ubd.role_id = 2 "
			+ " 	AND u.is_delete = FALSE "
			+ " 	AND ubd.is_delete = FALSE "
			+ " 	AND (:flag = FALSE OR u.is_active = :isActive)"
			+ " 	AND (:type = 'ALL' OR UPPER(ubd.type) = :type) ",
	        nativeQuery = true)
	List<Object[]> getPaymentSummary(@Param("type") String type, @Param("flag") Boolean flag, @Param("isActive") Boolean isActive);

	@Query(value = 
		    " SELECT COALESCE(SUM(uph.plan_base_amount), 0) AS total_amount"
		    + " FROM users u "
		    + " LEFT JOIN user_basic_details ubd ON u.user_id = ubd.user_id "
		    + " LEFT JOIN user_plan_histories uph ON uph.user_id = u.user_id AND uph.is_active = TRUE "
		    + " LEFT JOIN userpaymentinfo upi ON upi.user_plan_id = uph.plan_history_id "
		    + " WHERE ubd.role_id = 2 "
		    + " AND ubd.is_delete = FALSE "
		    + " AND u.is_delete = FALSE",
		    nativeQuery = true)
	Double getTotalPlanBaseAmount();
}
