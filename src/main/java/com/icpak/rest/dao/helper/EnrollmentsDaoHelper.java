package com.icpak.rest.dao.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.icpak.rest.IDUtils;
import com.icpak.rest.dao.BookingsDao;
import com.icpak.rest.dao.EventsDao;
import com.icpak.rest.dao.UsersDao;
import com.icpak.rest.exceptions.ServiceException;
import com.icpak.rest.models.ErrorCodes;
import com.icpak.rest.models.event.Booking;
import com.icpak.rest.models.event.Delegate;
import com.icpak.rest.models.event.Event;
import com.icpak.rest.models.membership.Contact;
import com.icpak.rest.models.util.Attachment;
import com.icpak.rest.utils.Doc;
import com.icpak.rest.utils.DocumentHTMLMapper;
import com.icpak.rest.utils.DocumentLine;
import com.icpak.rest.utils.EmailServiceHelper;
import com.icpak.rest.utils.HTMLToPDFConvertor;
import com.workpoint.icpak.shared.model.InvoiceDto;
import com.workpoint.icpak.shared.model.InvoiceLineDto;
import com.workpoint.icpak.shared.model.events.ContactDto;
import com.workpoint.icpak.shared.model.events.DelegateDto;
import com.workpoint.icpak.shared.model.events.EnrollmentDto;

@Transactional
public class EnrollmentsDaoHelper {

	@Inject
	BookingsDao dao;
	@Inject
	UsersDao userDao;
	@Inject
	EventsDao eventDao;
	
	@Inject InvoiceDaoHelper invoiceHelper;
	
	@Inject TransactionDaoHelper trxHelper;

	public List<EnrollmentDto> getAllBookings(String uriInfo, String eventId,
			Integer offset, Integer limit) {

		List<Booking> list = null;
		if(eventId!=null){
			list = dao.getAllBookings(eventId, offset, limit);
		}else{
			list = dao.getAllBookings("",offset, limit);
		}
		 

		List<EnrollmentDto> clones = new ArrayList<>();
		for (Booking booking : list) {
			EnrollmentDto dto = booking.toEnrollmentDto();
			dto.setUri(uriInfo + "/" + dto.getRefId());
			// dto.getEvent().setUri(uriInfo.getBaseUri()+"events/"+clone.getEvent().getRefId());
			clones.add(dto);
		}

		return clones;
	}

	public EnrollmentDto getBookingById(String eventId, String bookingId) {

		Booking booking = dao.getByBookingId(bookingId);
		EnrollmentDto bookingDto = booking.toEnrollmentDto();
		bookingDto.setInvoiceRef(dao.getInvoiceRef(bookingId));
		
		return bookingDto;
	}

	public EnrollmentDto createBooking(String eventId, EnrollmentDto dto) {
		assert dto.getRefId() == null;
		Event event = eventDao.getByEventId(eventId);
		
		Booking booking = new Booking();
		booking.setRefId(IDUtils.generateId());
		booking.setEvent(event);
		Contact poContact = new Contact();
		if (dto.getContact() != null) {
			ContactDto contactDto = dto.getContact();
			poContact.copyFrom(contactDto);
		}
		booking.setContact(poContact);
		booking.copyFrom(dto);
		
		if(booking.getMemberId()!=null){
			booking.setAmountDue(event.getMemberPrice());//Total
		}else{
			booking.setAmountDue(event.getNonMemberPrice());
		}
		dao.createBooking(booking);
		sendProInvoice(booking);
				
		//Copy into dto
		dto.setRefId(booking.getRefId());		
		dao.getEntityManager().merge(booking);
		dto.setInvoiceRef(dao.getInvoiceRef(booking.getRefId()));
		return dto;
	}

	private void sendProInvoice(Booking booking) {
		InvoiceDto invoice = generateInvoice(booking);
		Event event = booking.getEvent();
		String subject = booking.getEvent().getName()+"' Event Registration";
		
		try{
			
			Map<String,Object> values  = new HashMap<String, Object>();
			values.put("companyName", invoice.getCompanyName());
			values.put("companyAddress", invoice.getCompanyAddress());
			values.put("quoteNo", booking.getId());
			values.put("date", invoice.getDate());
			values.put("firstName", invoice.getContactName());
			
			values.put("DocumentURL", "http://127.0.0.1:8888/icpakportal.html");
			values.put("email", booking.getContact().getEmail());
			values.put("eventId", booking.getEvent().getRefId());
			values.put("bookingId", booking.getRefId());
			Doc doc = new Doc(values);

			for(InvoiceLineDto dto : invoice.getLines()){
				Map<String,Object> line  = new HashMap<String, Object>();
				line.put("description", dto.getDescription());
				line.put("unitPrice", dto.getUnitPrice());
				line.put("amount", dto.getTotalAmount());
				doc.addDetail(new DocumentLine("invoiceDetails",line));
			}
			
			values.put("totalAmount", invoice.getInvoiceAmount());
			
			
			//PDF Invoice Generation
			InputStream inv = EmailServiceHelper.class.getClassLoader().getResourceAsStream("proforma-invoice.html");
			String invoiceHTML = IOUtils.toString(inv);
			byte[] invoicePDF = new HTMLToPDFConvertor().convert(doc, new String(invoiceHTML));
			Attachment attachment = new Attachment();
			attachment.setAttachment(invoicePDF);
			attachment.setName("ProForma Invoice_"+booking.getContact().getContactName()+".pdf");
			
			//Email Template parse and map variables
			InputStream is = EmailServiceHelper.class.getClassLoader().getResourceAsStream("booking-email.html");
			String html = IOUtils.toString(is);
			html = new DocumentHTMLMapper().map(doc, html);
			EmailServiceHelper.sendEmail(html, "RE: ICPAK '"+subject,
					Arrays.asList(booking.getContact().getEmail()),
					Arrays.asList(booking.getContact().getContactName()), attachment);	
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String trxRef = trxHelper.charge(booking.getMemberId(),
				booking.getBookingDate(), subject, event.getStartDate(), invoice.getInvoiceAmount(),
				"Booking #"+booking.getId(), invoice.getRefId());
	}
	
	public InvoiceDto generateInvoice(Booking booking){
		InvoiceDto invoice   = new InvoiceDto();
		invoice.setDescription("Event Booking Invoice");
		double amount = 0.0;
		for(Delegate delegate : booking.getDelegates()){
			InvoiceLineDto dto = new InvoiceLineDto();
			dto.setDescription( delegate.getSurname()+" "+delegate.getOtherNames());
			dto.setUnitPrice(delegate.getAmount());
			dto.setTotalAmount(delegate.getAmount());
			invoice.addLine(dto);
			
			amount+=delegate.getAmount();
		}
		invoice.setAmount(amount);
		invoice.setDocumentNo(booking.getId()+"");
		invoice.setCompanyName(booking.getContact().getCompany());
		invoice.setDate(booking.getBookingDate()==null? new Date().getTime(): booking.getBookingDate().getTime());
		invoice.setCompanyAddress(booking.getContact().getAddress());
		invoice.setContactName(booking.getContact().getContactName());
		invoice.setPhoneNumber(booking.getContact().getTelephoneNumbers());
		invoice.setBookingRefId(booking.getRefId());
		
		invoice = invoiceHelper.save(invoice);
		
		return invoice;
	}

	public EnrollmentDto updateBooking(String eventId, String bookingId,
			EnrollmentDto dto) {
		assert dto.getRefId() != null;

		Booking poBooking = dao.getByBookingId(bookingId);
		poBooking.copyFrom(dto);
		poBooking.setEvent(eventDao.getByEventId(eventId));
		if (dto.getContact() != null) {
			Contact poContact = poBooking.getContact();
			if (poContact == null) {
				poContact = new Contact();
			}
			
			ContactDto contactDto = dto.getContact();
			poContact.copyFrom(contactDto);
		}	
		dao.save(poBooking);
		
		sendProInvoice(poBooking);

		EnrollmentDto bookingDto = poBooking.toEnrollmentDto();
		dto.setInvoiceRef(dao.getInvoiceRef(bookingId));
		return bookingDto;
	}

	private Delegate get(DelegateDto delegateDto) {
		Delegate delegate = new Delegate();
		if(delegateDto.getRefId()!=null){
			delegate = eventDao.findByRefId(delegate.getRefId(), Delegate.class);
		}
		
		delegate.copyFrom(delegateDto);
		return delegate;
	}

	public void deleteBooking(String eventId, String bookingId) {
		Booking booking = dao.getByBookingId(bookingId);
		dao.delete(booking);
	}

	public EnrollmentDto processPayment(String eventId, String bookingId,
			String paymentMode, String paymentRef) {
		Booking booking = dao.getByBookingId(bookingId);
		// Check if payment ref already exists
		boolean exists = dao.isPaymentValid(paymentRef);
		if (exists) {
			throw new ServiceException(ErrorCodes.DUPLICATEVALUE, "Payment Ref");
		}

		if (booking != null) {
			booking.setPaymentRef(paymentRef);
			booking.setPaymentMode(paymentMode);
			booking.setPaymentDate(new Date());
		}
		
		dao.save(booking);
		
		return booking.toEnrollmentDto();
	}

	public DelegateDto updateDelegate(String bookingId, String delegateId,
			DelegateDto delegateDto) {
		
		Delegate delegate = dao.findByRefId(delegateId, Delegate.class);
		delegate.setAttendance(delegateDto.getAttendance());
		dao.save(delegate);
		return delegate.toDto();
	}

}
