package com.icpak.rest.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.icpak.rest.exceptions.ServiceException;
import com.icpak.rest.models.ErrorCodes;
import com.icpak.rest.models.event.Booking;
import com.workpoint.icpak.shared.model.EventStatus;
import com.workpoint.icpak.shared.model.PaymentStatus;
import com.workpoint.icpak.shared.model.events.AttendanceStatus;
import com.workpoint.icpak.shared.model.events.MemberBookingDto;

public class BookingsDao extends BaseDao {
	Logger logger = Logger.getLogger(BookingsDao.class);

	public Booking getByBookingId(String refId) {
		
		Booking booking = getSingleResultOrNull(getEntityManager().createQuery(
				"from Booking u where u.refId=:refId").setParameter("refId",
				refId));
		if(booking==null){
			throw new ServiceException(ErrorCodes.NOTFOUND, "Booking", "'"+refId+"'");
		}
		return booking;
	}

	public void createBooking(Booking booking) {
		save(booking);
	}

	public List<Booking> getAllBookings(Integer offSet, Integer limit) {
		return getResultList(getEntityManager().createQuery("from Booking where isActive=1 order by created"),
				offSet, limit);
	}
	
	public List<Booking> getAllBookings(String eventId,Integer offSet, Integer limit) {
		return getResultList(getEntityManager().createQuery("from Booking b where b.isActive=1 "
				+ "and b.event.refId=:refId order by created")
				.setParameter("refId", eventId),
				offSet, limit);
	}

	public void updateBooking(Booking booking) {
		createBooking(booking);
	}

	public int getBookingCount(String eventId) {
		
		Number number = null;
		
		if(eventId!=null){
			number = getSingleResultOrNull(getEntityManager()
					.createQuery("select count(*) from Booking b where b.isActive=1 "
				+ "and b.event.refId=:refId")
				.setParameter("refId", eventId));
		}else{
			number = getSingleResultOrNull(getEntityManager()
					.createQuery("select count(*) from booking where isactive=1"));
		}

		return number.intValue();
	}

	public boolean isPaymentValid(String paymentRef) {
		return false;
	}

	public String getInvoiceRef(String bookingRefId) {
		return getSingleResultOrNull(getEntityManager()
				.createNativeQuery("select refId from Invoice where bookingRefId=:bookingRefId")
				.setParameter("bookingRefId", bookingRefId));
		
	}
	
	public void deleteAllBookingInvoice(String bookingRefId){
		logger.error("Deleteing invoice line ====== ><><<>>>>>>>>===");
		Query deleteQuery = getEntityManager().createNativeQuery(
	            "delete from InvoiceLine "
	            + "where invoiceId in ( "
	                + "select d.id from Invoice d "
	                + "where d.bookingRefId = :bookingRefId)");
	        deleteQuery.setParameter("bookingRefId", bookingRefId);
	        deleteQuery.executeUpdate();
	        
	        logger.error("Deleteing invoice ====== ><><<>>>>>>>>===");
	        deleteQuery = getEntityManager().createNativeQuery(
		            "delete from Invoice "
		            + "where bookingRefId = :bookingRefId");
		        deleteQuery.setParameter("bookingRefId", bookingRefId);
		        deleteQuery.executeUpdate();
		        
		        logger.error("Deleteing Success ====== ><><<>>>>>>>>===");
	}
	
	
	public List<MemberBookingDto> getMemberBookings(String memberRefId, int offset, int limit){
		String sql = "select b.refId bookingRefId, "
				+ "e.refId eventRefId, "
				+ "d.refId delegateRefId, "
				+ "e.name, "
				+ "e.startdate, "
				+ "e.enddate, "
				+ "e.venue, "
				+ "e.cpdhours, "
				+ "(case when e.enddate<current_date then 'CLOSED' else 'OPEN' end) eventStatus, "
				+ "d.attendance,"
				+ "b.paymentStatus, "
				+ "t.status trxStatus, "
				+ "a.hotel "
				+ "from event e "
				+ "inner join booking b on (e.id=b.event_Id)  "
				+ "inner join delegate d on (d.booking_id=b.id) "
				+ "left join accommodation a on (a.id=d.accommodationid) "
				+ "left join invoice i on (i.bookingRefId=b.refId) "
				+ "left join transaction t on (t.invoiceRef=i.refId) "
				+ "where d.memberRefId=:memberRefId";
		
		List<Object[]> rows =  getResultList(getEntityManager()
				.createNativeQuery(sql)
				.setParameter("memberRefId", memberRefId),
				offset,limit);
		List<MemberBookingDto> memberEvents = new ArrayList<>();
		
		for(Object[] row: rows){
			int i=0;
			Object value=null;
			String bookingRefId=(value=row[i++])==null? null: value.toString();
			String eventRefId=(value=row[i++])==null? null: value.toString();
			String delegateRefId=(value=row[i++])==null? null: value.toString();
			String eventName=(value=row[i++])==null? null: value.toString();
			Date startDate=(value=row[i++])==null? null: (Date)value;
			Date endDate=(value=row[i++])==null? null: (Date)value;
			String venue=(value=row[i++])==null? null: value.toString();
			String cpdHours=(value=row[i++])==null? null: value.toString();
			String eventStatus=(value=row[i++])==null? null: value.toString();
			Integer attendance=(value=row[i++])==null? null: ((Number)value).intValue();
			Integer paymentStatus=(value=row[i++])==null? null: ((Number)value).intValue();//Should remove this field
			String trxStatus=(value=row[i++])==null? null: value.toString();//From Transactions
			String hotel=(value=row[i++])==null? null: value.toString();
			
			MemberBookingDto dto = new MemberBookingDto();
			dto.setAccommodation(hotel);
			dto.setAttendance(attendance==AttendanceStatus.ATTENDED.ordinal()? AttendanceStatus.ATTENDED:
				AttendanceStatus.NOTATTENDED);
			dto.setBookingRefId(bookingRefId);
			dto.setCpdHours(cpdHours);
			dto.setDelegateRefId(delegateRefId);
			dto.setEndDate(endDate);
			dto.setEventName(eventName);
			dto.setEventRefId(eventRefId);
			dto.setEventStatus(EventStatus.valueOf(eventStatus));
			dto.setLocation(venue);
//			dto.setPaymentStatus(PaymentStatus.PAID.ordinal()==paymentStatus? 
//					PaymentStatus.PAID: PaymentStatus.NOTPAID);
			dto.setPaymentStatus(trxStatus==null? PaymentStatus.NOTPAID : 
				PaymentStatus.valueOf(trxStatus));
			
			dto.setStartDate(startDate);
			memberEvents.add(dto);
		}
		
		
		return memberEvents;
	}

	public String getErn(String refId) {
		
		return getSingleResultOrNull(getEntityManager().
				createNativeQuery("select ern from delegate where refId=:refId")
				.setParameter("refId", refId));
	}

}
