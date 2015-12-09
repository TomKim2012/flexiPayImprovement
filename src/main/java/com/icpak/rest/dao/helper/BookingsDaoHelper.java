package com.icpak.rest.dao.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.icpak.rest.dao.BookingsDao;
import com.icpak.rest.dao.EventsDao;
import com.icpak.rest.dao.InvoiceDao;
import com.icpak.rest.dao.InvoiceDaoHelper;
import com.icpak.rest.dao.MemberDao;
import com.icpak.rest.dao.UsersDao;
import com.icpak.rest.exceptions.ServiceException;
import com.icpak.rest.models.ErrorCodes;
import com.icpak.rest.models.event.Accommodation;
import com.icpak.rest.models.event.Booking;
import com.icpak.rest.models.event.Delegate;
import com.icpak.rest.models.event.Event;
import com.icpak.rest.models.membership.Contact;
import com.icpak.rest.models.membership.Member;
import com.icpak.rest.models.util.Attachment;
import com.icpak.rest.util.ApplicationSettings;
import com.icpak.rest.util.SMSIntegration;
import com.icpak.rest.utils.Doc;
import com.icpak.rest.utils.DocumentHTMLMapper;
import com.icpak.rest.utils.DocumentLine;
import com.icpak.rest.utils.EmailServiceHelper;
import com.icpak.rest.utils.HTMLToPDFConvertor;
import com.itextpdf.text.DocumentException;
import com.workpoint.icpak.server.integration.lms.LMSIntegrationUtil;
import com.workpoint.icpak.server.integration.lms.LMSResponse;
import com.workpoint.icpak.shared.model.EventType;
import com.workpoint.icpak.shared.model.InvoiceDto;
import com.workpoint.icpak.shared.model.InvoiceLineDto;
import com.workpoint.icpak.shared.model.events.AttendanceStatus;
import com.workpoint.icpak.shared.model.events.BookingDto;
import com.workpoint.icpak.shared.model.events.ContactDto;
import com.workpoint.icpak.shared.model.events.CourseRegDetailsPojo;
import com.workpoint.icpak.shared.model.events.DelegateDto;
import com.workpoint.icpak.shared.model.events.MemberBookingDto;

@Transactional
public class BookingsDaoHelper {

	Logger logger = Logger.getLogger(BookingsDaoHelper.class);

	@Inject
	BookingsDao dao;
	@Inject
	UsersDao userDao;
	@Inject
	MemberDao memberDao;
	@Inject
	MemberDaoHelper memberDaoHelper;
	@Inject
	EventsDao eventDao;
	@Inject
	CPDDaoHelper cpdDao;
	@Inject
	InvoiceDaoHelper invoiceHelper;
	@Inject
	TransactionDaoHelper trxHelper;
	@Inject
	InvoiceDao invoiceDao;

	@Inject
	SMSIntegration smsIntergration;

	@Inject
	ApplicationSettings settings;

	@Inject
	AccommodationsDaoHelper accommodationsDaoHelper;

	SimpleDateFormat formatter = new SimpleDateFormat("MMM d Y");

	public List<BookingDto> getAllBookings(String uriInfo, String eventId, Integer offset, Integer limit,
			String searchTerm) {
		List<Booking> list = null;
		List<BookingDto> clones = new ArrayList<>();

		if (searchTerm != null) {
			list = dao.getAllBookings(eventId, offset, limit, searchTerm);
		} else {
			list = dao.getAllBookings(eventId, offset, limit);
		}

		for (Booking booking : list) {
			BookingDto dto = booking.toDto();
			dto.setUri(uriInfo + "/" + dto.getRefId());
			// dto.getEvent().setUri(uriInfo.getBaseUri()+"events/"+clone.getEvent().getRefId());
			clones.add(dto);
		}

		return clones;
	}

	public List<DelegateDto> getAllDelegates(String uriInfo, String eventId, Integer offset, Integer limit,
			String searchTerm) {
		List<DelegateDto> delegateDtos = dao.getAllDelegates(eventId, offset, limit, searchTerm);
		return delegateDtos;
	}

	public Integer getCount(String eventId) {
		return dao.getDelegateCount(eventId);
	}

	public BookingDto getBookingById(String eventId, String bookingId) {
		Booking booking = dao.getByBookingId(bookingId);
		BookingDto bookingDto = booking.toDto();
		bookingDto.setInvoiceRef(dao.getInvoiceRef(bookingId));
		return bookingDto;
	}

	public BookingDto createBooking(String eventId, BookingDto dto) {
		Event event = eventDao.getByEventId(eventId);

		Booking booking = null;
		if (dto.getRefId() != null) {
			booking = dao.getByBookingId(dto.getRefId());
		} else {
			booking = new Booking();
		}

		booking.setEvent(event);

		if (dto.getContact() != null) {
			Contact poContact = booking.getContact();
			if (poContact == null) {
				poContact = new Contact();
			}
			ContactDto contactDto = dto.getContact();
			poContact.copyFrom(contactDto);
			booking.setContact(poContact);
		}
		booking.copyFrom(dto);

		List<DelegateDto> dtos = dto.getDelegates();
		Collection<Delegate> delegates = new ArrayList<>();

		double total = 0.0;
		if (dtos != null)
			for (DelegateDto delegateDto : dtos) {
				Delegate d = initDelegate(delegateDto, event);
				delegates.add(d);
				total += d.getAmount();
			}
		booking.setAmountDue(total);// Total
		booking.setDelegates(delegates);
		dao.createBooking(booking);

		deleteExistingInvoices(booking.getRefId());
		InvoiceDto invoice = generateInvoice(booking);
		dto.setInvoiceRef(invoice.getRefId());
		dto.setRefId(eventId);

		// Copy into dto
		dto.setRefId(booking.getRefId());
		int i = 0;
		for (Delegate delegate : booking.getDelegates()) {
			dto.getDelegates().get(i).setRefId(delegate.getRefId());
			dto.getDelegates().get(i).setErn(delegate.getErn());
		}
		return dto;
	}

	private void deleteExistingInvoices(String bookingId) {
		if (bookingId == null)
			return;
		dao.deleteAllBookingInvoice(bookingId);
	}

	public byte[] generateInvoicePdf(String bookingRefId) throws FileNotFoundException, IOException, SAXException,
			ParserConfigurationException, FactoryConfigurationError, DocumentException {
		assert bookingRefId != null;
		Booking bookingInDb = dao.findByRefId(bookingRefId, Booking.class);
		InvoiceDto invoice = invoiceHelper.getInvoice(dao.getInvoiceRef(bookingRefId));
		// Generate Email Document to be used to Map to HTML
		Map<String, Object> emailValues = generateEmailValues(invoice, bookingInDb);
		byte[] invoicePdf = generatePDFDocument(invoice, emailValues);

		return invoicePdf;
	}

	public void sendProInvoice(String bookingRefId) {
		assert bookingRefId != null;
		Booking bookingInDb = dao.findByRefId(bookingRefId, Booking.class);
		InvoiceDto invoice = invoiceHelper.getInvoice(dao.getInvoiceRef(bookingRefId));

		String subject = bookingInDb.getEvent().getName() + "' Event Registration";

		// Generate Email Document to be used to Map to HTML
		Map<String, Object> emailValues = generateEmailValues(invoice, bookingInDb);
		Doc emailDocument = new Doc(emailValues);

		try {
			byte[] invoicePdf = generatePDFDocument(invoice, emailValues);
			Attachment attachment = new Attachment();
			attachment.setAttachment(invoicePdf);
			attachment.setName("ProForma Invoice_" + bookingInDb.getContact().getContactName() + ".pdf");

			// Email Template parse and map variables
			InputStream is = EmailServiceHelper.class.getClassLoader().getResourceAsStream("booking-email.html");
			String html = IOUtils.toString(is);
			html = new DocumentHTMLMapper().map(emailDocument, html);

			EmailServiceHelper.sendEmail(html, "RE: ICPAK '" + subject,
					Arrays.asList(bookingInDb.getContact().getEmail()),
					Arrays.asList(bookingInDb.getContact().getContactName()), attachment);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] generatePDFDocument(InvoiceDto invoice, Map<String, Object> emailValues)
			throws FileNotFoundException, IOException, SAXException, ParserConfigurationException,
			FactoryConfigurationError, DocumentException {

		String documentNo = invoice.getDocumentNo();

		List<InvoiceLineDto> invoiceLines = invoiceDao.getByLinesByDocumentNo(documentNo);

		Doc proformaDocument = new Doc(emailValues);

		for (InvoiceLineDto dto : invoiceLines) {
			Map<String, Object> line = new HashMap<>();
			line.put("description", dto.getDescription());
			line.put("quantity", NumberFormat.getNumberInstance().format(dto.getQuantity()));
			line.put("unitPrice", NumberFormat.getNumberInstance().format(dto.getUnitPrice()));
			line.put("amount", NumberFormat.getNumberInstance().format(dto.getTotalAmount()));
			logger.warn("InvoiceDto: " + dto.getRefId() + " | " + dto.getDescription() + " | " + dto.getTotalAmount());
			proformaDocument.addDetail(new DocumentLine("invoiceDetails", line));
		}

		emailValues.put("totalAmount", NumberFormat.getNumberInstance().format(invoice.getInvoiceAmount()));
		logger.warn("Values = " + proformaDocument);

		// PDF Invoice Generation
		InputStream inv = EmailServiceHelper.class.getClassLoader().getResourceAsStream("proforma-invoice.html");
		String invoiceHTML = IOUtils.toString(inv);

		byte[] invoicePDF = new HTMLToPDFConvertor().convert(proformaDocument, new String(invoiceHTML));

		return invoicePDF;
	}

	public Map<String, Object> generateEmailValues(InvoiceDto invoice, Booking bookingInDb) {
		Map<String, Object> emailValues = new HashMap<String, Object>();
		emailValues.put("companyName", invoice.getCompanyName());
		emailValues.put("companyAddress", invoice.getCompanyAddress());
		emailValues.put("companyLocation", bookingInDb.getContact().getPhysicalAddress());
		emailValues.put("contactPhone", bookingInDb.getContact().getPhysicalAddress());

		emailValues.put("quoteNo", invoice.getDocumentNo());
		emailValues.put("date", invoice.getDate());
		emailValues.put("firstName", invoice.getContactName());
		emailValues.put("eventName", bookingInDb.getEvent().getName());
		emailValues.put("eventStartDate", formatter.format(bookingInDb.getEvent().getStartDate()));
		emailValues.put("DocumentURL", settings.getApplicationPath());
		emailValues.put("email", bookingInDb.getContact().getEmail());
		emailValues.put("eventId", bookingInDb.getEvent().getRefId());
		emailValues.put("bookingId", bookingInDb.getRefId());
		Doc emailDocument = new Doc(emailValues);

		// Collection of Delegates
		Collection<Delegate> delegates = bookingInDb.getDelegates();
		int counter = 0;

		for (Delegate delegate : delegates) {
			counter++;
			emailValues.put("counter", counter);
			emailValues.put("delegateNames", delegate.getSurname() + " " + delegate.getOtherNames());

			emailValues.put("memberType", (delegate.getMemberRegistrationNo() == null ? "Non-Member" : "Member"));

			emailValues.put("ernNo", (delegate.getErn() == null ? "----" : delegate.getErn()));

			emailValues.put("accomodationName", (delegate.getAccommodation() == null ? "None"
					: delegate.getAccommodation().getHotel() + " " + delegate.getAccommodation().getNights()));
			DocumentLine docLine = new DocumentLine("accomadationDetails", emailValues);
			emailDocument.addDetail(docLine);
		}

		return emailValues;
	}

	public InvoiceDto generateInvoice(Booking booking) {
		InvoiceDto invoice = new InvoiceDto();
		invoice.setDescription("Event Booking Invoice");
		double amount = 0.0;

		// THE ANNUAL MANAGEMENT ACCOUNTING CONFERENCE - Conference fees for 2
		// members:JUSTUS MUSASIAH(6061, ERN: 430-0236),EVANS MULERA(4295, ERN:
		// 430-0237)

		List<Delegate> delegates = new ArrayList<>();
		delegates.addAll(booking.getDelegates());
		Collections.sort(delegates, new Comparator<Delegate>() {
			@Override
			public int compare(Delegate o1, Delegate o2) {
				return o1.getMemberRegistrationNo() == null ? -1 : o2.getMemberRegistrationNo() == null ? 1 : 0;
			}
		});

		InvoiceLineDto memberInvoice = new InvoiceLineDto();
		memberInvoice.setQuantity(0);
		memberInvoice.setMemberNames("");

		InvoiceLineDto nonMemberInvoice = new InvoiceLineDto();
		nonMemberInvoice.setQuantity(0);
		nonMemberInvoice.setMemberNames("");

		Map<String, InvoiceLineDto> memberRefLineMap = new HashMap<>();
		Map<String, InvoiceLineDto> nonMemberRefLineMap = new HashMap<>();

		Event event = booking.getEvent();
		for (Delegate delegate : delegates) {
			delegate.setErn(dao.getErn(delegate.getRefId()));

			if (delegate.getMemberRegistrationNo() != null) {
				String description = "%s - %s fees for %d members: %s";
				memberInvoice.setMemberNames(memberInvoice.getMemberNames()
						.concat((memberInvoice.getMemberNames().isEmpty() ? "" : ", ") + delegate.toString()));

				int qty = memberInvoice.getQuantity() + 1;

				memberInvoice.setEventDelegateRefId(delegate.getRefId());
				description = String.format(description, event.getName(), event.getType().getDisplayName(), qty,
						memberInvoice.getMemberNames());
				memberInvoice.setDescription(description);
				memberInvoice.setQuantity(qty);
				memberInvoice.setUnitPrice(delegate.getAmount());
				memberInvoice.setTotalAmount(qty * delegate.getAmount());

				amount += delegate.getAmount();// memberInvoice.getTotalAmount();
			} else {
				String description = "%s - %s fees for %d non-members: %s";
				int qty = nonMemberInvoice.getQuantity() + 1;
				nonMemberInvoice.setEventDelegateRefId(delegate.getRefId());

				nonMemberInvoice.setMemberNames(nonMemberInvoice.getMemberNames()
						.concat((nonMemberInvoice.getMemberNames().isEmpty() ? "" : ", ") + delegate.toString()));

				description = String.format(description, event.getName(), event.getType().getDisplayName(), qty,
						nonMemberInvoice.getMemberNames());
				nonMemberInvoice.setDescription(description);
				nonMemberInvoice.setQuantity(qty);
				nonMemberInvoice.setUnitPrice(delegate.getAmount());
				nonMemberInvoice.setTotalAmount(qty * delegate.getAmount());
				amount += delegate.getAmount();// nonMemberInvoice.getTotalAmount();
			}

			if (delegate.getAccommodation() != null) {
				if (delegate.getMemberRegistrationNo() != null) {
					InvoiceLineDto line = memberRefLineMap.get(delegate.getAccommodation().getRefId());
					if (line == null) {
						line = new InvoiceLineDto();
						line.setMemberNames("");
						memberRefLineMap.put(delegate.getAccommodation().getRefId(), line);
					}

					String description = "%s - Accommodation at %s %d Nights HB " + "for %d members: %s";
					line.setMemberNames(line.getMemberNames().concat(", " + delegate.toString()));
					int qty = line.getQuantity() + 1;

					line.setEventDelegateRefId(delegate.getRefId());
					description = String.format(description, event.getName(), delegate.getAccommodation().getHotel(),
							delegate.getAccommodation().getNights(), qty, line.getMemberNames());
					line.setDescription(description);
					line.setQuantity(qty);
					line.setUnitPrice(delegate.getAccommodation().getFee());
					line.setTotalAmount(qty * delegate.getAccommodation().getFee());
					amount += delegate.getAccommodation().getFee();// line.getTotalAmount();
				} else {

					InvoiceLineDto line = nonMemberRefLineMap.get(delegate.getAccommodation().getRefId());
					if (line == null) {
						line = new InvoiceLineDto();
						line.setMemberNames("");
						nonMemberRefLineMap.put(delegate.getAccommodation().getRefId(), line);
					}

					String description = "%s - Accommodation at %s %d Nights HB " + "for %d members: %s";
					line.setMemberNames(line.getMemberNames().concat(", " + delegate.toString()));
					int qty = line.getQuantity() + 1;

					line.setEventDelegateRefId(delegate.getRefId());
					description = String.format(description, event.getName(), delegate.getAccommodation().getHotel(),
							delegate.getAccommodation().getNights(), qty, line.getMemberNames());
					line.setDescription(description);
					line.setQuantity(qty);
					line.setUnitPrice(delegate.getAccommodation().getFee());
					line.setTotalAmount(qty * delegate.getAccommodation().getFee());
					amount += delegate.getAccommodation().getFee();// line.getTotalAmount();
				}
			}
		}

		if (memberInvoice.getQuantity() != 0) {
			invoice.addLine(memberInvoice);
		}
		if (nonMemberInvoice.getQuantity() != 0) {
			invoice.addLine(nonMemberInvoice);
		}

		invoice.addLines(memberRefLineMap.values());
		invoice.addLines(nonMemberRefLineMap.values());

		invoice.setAmount(amount);
		invoice.setDocumentNo(booking.getId() + "");
		invoice.setCompanyName(booking.getContact().getCompany());
		invoice.setDate(booking.getBookingDate().getTime());
		invoice.setCompanyAddress(booking.getContact().getAddress());
		invoice.setContactName(booking.getContact().getContactName());
		invoice.setPhoneNumber(booking.getContact().getTelephoneNumbers());
		invoice.setBookingRefId(booking.getRefId());
		invoice = invoiceHelper.save(invoice);

		// System.err.println("Invoice RefId>>>" + invoice.getRefId());

		// Create a Charge Record
		trxHelper.charge(booking.getMemberId(), booking.getBookingDate(), event.getName() + " Event Booking",
				event.getStartDate(), invoice.getInvoiceAmount(), "Booking #" + booking.getId(), invoice.getRefId());
		return invoice;
	}

	public BookingDto updateBooking(String eventId, String bookingId, BookingDto dto) {
		logger.error("==== Booking ref Id === " + bookingId);
		dto.setRefId(bookingId);
		return createBooking(eventId, dto);

		// Event eventInDb = eventDao.getByEventId(eventId);
		// Booking poBooking = dao.getByBookingId(bookingId);
		// poBooking.copyFrom(dto);
		// poBooking.setEvent(eventInDb);
		// if (dto.getContact() != null) {
		// Contact poContact = poBooking.getContact();
		// if (poContact == null) {
		// poContact = new Contact();
		// }
		//
		// ContactDto contactDto = dto.getContact();
		// poContact.copyFrom(contactDto);
		// }
		//
		// List<DelegateDto> dtos = dto.getDelegates();
		// Collection<Delegate> delegates = new ArrayList<>();
		// for (DelegateDto delegateDto : dtos) {
		// Delegate delegate = initDelegate(delegateDto, eventInDb);
		// dao.save(delegate);
		// delegates.add(delegate);
		// }
		// poBooking.setDelegates(delegates);
		//
		// dao.save(poBooking);
		// sendProInvoice(poBooking);
		// sendDelegateSMS(poBooking);
		//
		// dao.deleteAllBookingInvoice(bookingId);
		// BookingDto bookingDto = poBooking.toDto();
		// dto.setInvoiceRef(dao.getInvoiceRef(bookingId));
		// return bookingDto;
	}

	public void sendDelegateSMS(String bookingRefId) {

		Booking bookingInDb = dao.findByRefId(bookingRefId, Booking.class);

		Event event = bookingInDb.getEvent();
		Collection<Delegate> delegates = bookingInDb.getDelegates();
		List<Delegate> delegateList = new ArrayList<>();
		delegateList.addAll(delegates);

		for (Delegate delegate : delegateList) {
			String startDate = new SimpleDateFormat("dd/MM/yyyy").format(event.getStartDate());
			String endDate = new SimpleDateFormat("dd/MM/yyyy").format(event.getEndDate());
			String smsMemssage = "Dear" + " " + delegate.getSurname() + "," + "Thank you for booking for the "
					+ event.getName() + ". Your booking status is NOT PAID. Your ERN No. is " + delegate.getErn();

			if (delegate.getMemberRefId() != null) {
				Member member = memberDao.findByRefId(delegate.getMemberRefId(), Member.class);
				System.err.println("Sending SMS to " + member.getUser().getPhoneNumber());

				if (member.getUser().getPhoneNumber() != null) {
					try {
						smsIntergration.send(member.getUser().getPhoneNumber(), smsMemssage);
					} catch (RuntimeException e) {
						System.err.println("Invalid Phone Number...!");
						e.printStackTrace();
					}
				}
			} else {
				System.err.println("Non-member cannot be send sms..");
			}
		}

	}

	private Delegate initDelegate(DelegateDto delegateDto, Event event) {
		Delegate d = new Delegate();
		if (delegateDto.getRefId() != null) {
			d = eventDao.findByRefId(delegateDto.getRefId(), Delegate.class);
		}

		d.copyFrom(delegateDto);

		if (delegateDto.getAccommodation() != null) {
			Accommodation accommodation = dao.findByRefId(delegateDto.getAccommodation().getRefId(),
					Accommodation.class);
			if (accommodation != null) {
				accommodation.setSpaces(accommodation.getSpaces() - 1);
				d.setAccommodation(accommodation);
				dao.save(accommodation);
			}
		}

		// Event Pricing
		Double price = event.getNonMemberPrice();
		if (delegateDto.getMemberNo() != null) {
			price = event.getMemberPrice();
		}

		// Add Accommodation Charge
		if (d.getAccommodation() != null) {
			price += d.getAccommodation().getFee();
		}
		d.setAmount(price); // Charge for delegate

		return d;
	}

	public void deleteBooking(String eventId, String bookingId) {
		Booking booking = dao.getByBookingId(bookingId);
		dao.delete(booking);
	}

	public BookingDto processPayment(String eventId, String bookingId, String paymentMode, String paymentRef) {
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

		return booking.toDto();
	}

	public DelegateDto updateDelegate(String delegateId, DelegateDto delegateDto) {
		logger.error("+++++ <><>>>>>>>>>>>>> UPDATE DELEGATW +++++++++++++++++");

		Delegate delegate = dao.findByRefId(delegateId, Delegate.class);
		Event event = dao.findByRefId(delegate.getBooking().getEvent().getRefId(), Event.class);

		if (delegate.getMemberRefId() != null && delegate.getAttendance() != delegateDto.getAttendance()
				&& event.getType() != EventType.COURSE) {
			// send and SMS
			Member member = dao.findByRefId(delegate.getMemberRefId(), Member.class);
			String smsMessage = "Dear" + " " + delegateDto.getSurname() + ",Thank you for attending the "
					+ event.getName() + "." + "Your ERN No. is " + delegate.getErn();
			smsIntergration.send(member.getUser().getPhoneNumber(), smsMessage);
		}else{
			enrolDelegateToLms(delegate.getRefId() , event);
		}

		logger.error(delegateDto.getReceiptNo() + "== RCPT NO");
		delegate.copyFrom(delegateDto);

		dao.save(delegate);

		cpdDao.updateCPDFromAttendance(delegate, delegate.getAttendance());

		return delegate.toDto();
	}

	public List<MemberBookingDto> getMemberBookings(String memberRefId, int offset, int limit) {
		return dao.getMemberBookings(memberRefId, offset, limit);
	}

	public void updateAccomodationEntry(Event event) {
		Set<Accommodation> accommodations = event.getAccommodation();

		for (Accommodation accommodation : accommodations) {
			Set<Delegate> delegates = accommodation.getDelegates();
			int spacesOccupied = delegates.size();
			accommodation.setSpaces(accommodation.getSpaces() - spacesOccupied);

			accommodationsDaoHelper.update(event.getRefId(), accommodation.getRefId(), accommodation.toDto());
		}

	}

	public Integer getDelegatesCount(String eventId, String searchTerm) {
		logger.error("== Counting delegates ===");
		return dao.getDelegateCount(eventId, searchTerm);
	}

	public void enrolDelegateToLMS(List<DelegateDto> delegates, Event event) throws JSONException, IOException {
		logger.info("Delgates size::" + delegates.size());
		for (DelegateDto delegate : delegates) {
			CourseRegDetailsPojo details = new CourseRegDetailsPojo();
			details.setCourseId(delegate.getCourseId());
			details.setMembershipID(delegate.getMemberNo());
			JSONObject json = new JSONObject(details);
			logger.info("JSON::" + json);
			LMSResponse response = LMSIntegrationUtil.getInstance().executeLMSCall("/Course/EnrollCourse", json,
					String.class);
			logger.info("LMS Response::" + response.getMessage());
			logger.info("LMS Status::" + response.getStatus());
			delegate.setEventRefId(event.getRefId());
			delegate.setLmsResponse(response.getMessage());
			if (response.getStatus().equals("Success")) {
				delegate.setAttendance(AttendanceStatus.ENROLLED);
			}
			String refId = delegate.getRefId();
			logger.info("Delegate RefId::" + refId);
			logger.info("Event RefId::" + event.getRefId());
			Delegate delegateInDb = dao.findByRefId(refId, Delegate.class);
			updateDelegate(delegateInDb.getRefId(), delegate);
		}

	}

	public String enrolDelegateToLms(String delegateRefId , Event event) {
		logger.error("== Enrolling to lms ===");

		String message = null;

		List<DelegateDto> delegates = new ArrayList<>();
		Delegate delegateInDb = dao.findByRefId(delegateRefId, Delegate.class);
		DelegateDto dto = delegateInDb.toDto();
		dto.setCourseId(event.getLmsCourseId()+"");

		delegates.add(dto);

		try {
			enrolDelegateToLMS(delegates , event);
			message = "Success";
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			message = "Failed";
		}

		return message;
	}
}
