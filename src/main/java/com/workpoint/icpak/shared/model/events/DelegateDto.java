package com.workpoint.icpak.shared.model.events;

import java.util.Date;

import com.workpoint.icpak.shared.model.PaymentStatus;
import com.workpoint.icpak.shared.model.SerializableObj;
import com.workpoint.icpak.shared.trx.TransactionDto;

public class DelegateDto extends SerializableObj {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date createdDate;
	private String companyName;
	private String contactName;
	private String contactEmail;
	private String memberNo;
	private String memberRefId;
	private String title;
	private String surname;
	private String otherNames;
	private String email;
	private AccommodationDto accommodation;
	private Double amount;
	private AttendanceStatus attendance;
	private TransactionDto transaction;
	private DelegateType delegateType;
	private String bookingId;
	private String bookingRefId;
	private String eventRefId;
	private PaymentStatus paymentStatus;
	public String courseId;
	private String ern;
	private String hotel;
	private String lmsResponse;
	private String clearanceNo;
	private String receiptNo;
	private String lpoNo;
	private int isCredit;

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getLpoNo() {
		return lpoNo;
	}

	public void setLpoNo(String lpoNo) {
		this.lpoNo = lpoNo;
	}

	public int getIsCredit() {
		return isCredit;
	}

	public void setIsCredit(int isCredit) {
		this.isCredit = isCredit;
	}

	public String getHotel() {
		return hotel;
	}

	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public DelegateDto() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getOtherNames() {
		return otherNames;
	}

	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AccommodationDto getAccommodation() {
		return accommodation;
	}

	public void setAccommodation(AccommodationDto accommodation) {
		this.accommodation = accommodation;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public AttendanceStatus getAttendance() {
		return attendance;
	}

	public void setAttendance(AttendanceStatus attendance) {
		this.attendance = attendance;
	}

	public DelegateType getDelegateType() {
		return delegateType;
	}

	public void setDelegateType(DelegateType delegateType) {
		this.delegateType = delegateType;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getEventRefId() {
		return eventRefId;
	}

	public void setEventRefId(String eventRefId) {
		this.eventRefId = eventRefId;
	}

	public String getMemberNo() {
		return memberNo;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}

	public String getMemberRefId() {
		return memberRefId;
	}

	public void setMemberRefId(String memberRefId) {
		this.memberRefId = memberRefId;
	}

	public String getErn() {
		return ern;
	}

	public void setErn(String ern) {
		this.ern = ern;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DelegateDto) || obj == null) {
			return false;
		}

		DelegateDto other = (DelegateDto) obj;
		if (other.getRefId() != null && getRefId() != null) {
			return other.getRefId().equals(getRefId());
		}

		return super.equals(obj);
	}

	public String getLmsResponse() {
		return lmsResponse;
	}

	public void setLmsResponse(String lmsResponse) {
		this.lmsResponse = lmsResponse;
	}

	public String getClearanceNo() {
		return clearanceNo;
	}

	public void setClearanceNo(String clearanceNo) {
		this.clearanceNo = clearanceNo;
	}

	public TransactionDto getTransaction() {
		return transaction;
	}

	public void setTransaction(TransactionDto transaction) {
		this.transaction = transaction;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getBookingRefId() {
		return bookingRefId;
	}

	public void setBookingRefId(String bookingRefId) {
		this.bookingRefId = bookingRefId;
	}

}
