package com.workpoint.icpak.shared.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.icpak.rest.models.membership.Category;
import com.wordnik.swagger.annotations.ApiModel;

@ApiModel(description="Application Forms Filled by a member")

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(Category.class)
public class ApplicationFormHeaderDto extends SerializableObj{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String applicationNo;
	private Date date;
	private String applicationDate;
	private String surname;
	private String otherNames;
	private Date dob;
	private Gender gender;
	private int maritalStatus;
	private String nationality;
	private String country;
	private String address1;
	private String address2;
	private String address3;
	private String telephone1;
	private String telephone2;
	private ApplicationType applicationType;
	private String receiptNo;
	private Date receiptDate;
	private String userId;
	private String region;
	private String series;
	private String hodUserId;
	private Date hodDate;
	private Date hodTime;
	private String hodRecommendations;
	private String deanUserId;
	private Date deanDate;
	private Date deanTime;
	private String deanRecommendations;
	private int status;
	private int select;
	private String batchNo;
	private Date batchDate;
	private Date batchTime;
	private String boardRecommendation;
	private Date boardDate;
	private Date boardTime;
	private Date meetingDate;
	private Date receiptSlipDate;
	private String transactionNo;
	private String academicYear;
	private String intakeCode;
	private String settlementType;
	private String idNumber;
	private Date dateSentForApproval;
	private String issuedDate;
	private String campus;
	private String email;
	private int honorific;
	private int conviction;
	private String offence;
	private String convictionDateAndPlace;
	private String sentence;
	private String contactName;
	private String contactAddress;
	private String contactTelephone;
	private String contactEmail;
	//private String KASNEBFAQNo;
	//private String CPAFAQ;
	private String examinationBody;
	private String postCode;
	private int paymentMode;
	private int applicantType;
	private String responsibilityCenter;
	private int documentType;
	private String customerType;
	private String faxNo;
	private Date age;
	private String city1;
	private String designation;
	private String position;
	private String employerCode;
	private String employer;
	private String qualification;
	private String contactPerson;
	private String district;
	private String genBusPostingGroup;
	private String vatBusPostingGroup;
	private int applicationMethod;
	private CategoryDto category;
	
	public ApplicationFormHeaderDto() {
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
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

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(int maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		nationality = nationality;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getHodUserId() {
		return hodUserId;
	}

	public void setHodUserId(String hodUserId) {
		this.hodUserId = hodUserId;
	}

	public Date getHodDate() {
		return hodDate;
	}

	public void setHodDate(Date hodDate) {
		this.hodDate = hodDate;
	}

	public Date getHodTime() {
		return hodTime;
	}

	public void setHodTime(Date hodTime) {
		this.hodTime = hodTime;
	}

	public String getHodRecommendations() {
		return hodRecommendations;
	}

	public void setHodRecommendations(String hodRecommendations) {
		this.hodRecommendations = hodRecommendations;
	}

	public String getDeanUserId() {
		return deanUserId;
	}

	public void setDeanUserId(String deanUserId) {
		this.deanUserId = deanUserId;
	}

	public Date getDeanDate() {
		return deanDate;
	}

	public void setDeanDate(Date deanDate) {
		this.deanDate = deanDate;
	}

	public Date getDeanTime() {
		return deanTime;
	}

	public void setDeanTime(Date deanTime) {
		this.deanTime = deanTime;
	}

	public String getDeanRecommendations() {
		return deanRecommendations;
	}

	public void setDeanRecommendations(String deanRecommendations) {
		this.deanRecommendations = deanRecommendations;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSelect() {
		return select;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Date getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Date batchDate) {
		this.batchDate = batchDate;
	}

	public Date getBatchTime() {
		return batchTime;
	}

	public void setBatchTime(Date batchTime) {
		this.batchTime = batchTime;
	}

	public String getBoardRecommendation() {
		return boardRecommendation;
	}

	public void setBoardRecommendation(String boardRecommendation) {
		this.boardRecommendation = boardRecommendation;
	}

	public Date getBoardDate() {
		return boardDate;
	}

	public void setBoardDate(Date boardDate) {
		this.boardDate = boardDate;
	}

	public Date getBoardTime() {
		return boardTime;
	}

	public void setBoardTime(Date boardTime) {
		this.boardTime = boardTime;
	}

	public Date getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(Date meetingDate) {
		this.meetingDate = meetingDate;
	}

	public Date getReceiptSlipDate() {
		return receiptSlipDate;
	}

	public void setReceiptSlipDate(Date receiptSlipDate) {
		this.receiptSlipDate = receiptSlipDate;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public String getIntakeCode() {
		return intakeCode;
	}

	public void setIntakeCode(String intakeCode) {
		this.intakeCode = intakeCode;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public Date getDateSentForApproval() {
		return dateSentForApproval;
	}

	public void setDateSentForApproval(Date dateSentForApproval) {
		this.dateSentForApproval = dateSentForApproval;
	}

	public String getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}

	public String getCampus() {
		return campus;
	}

	public void setCampus(String campus) {
		this.campus = campus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getHonorific() {
		return honorific;
	}

	public void setHonorific(int honorific) {
		this.honorific = honorific;
	}

	public int getConviction() {
		return conviction;
	}

	public void setConviction(int conviction) {
		this.conviction = conviction;
	}

	public String getOffence() {
		return offence;
	}

	public void setOffence(String offence) {
		this.offence = offence;
	}

	public String getConvictionDateAndPlace() {
		return convictionDateAndPlace;
	}

	public void setConvictionDateAndPlace(String convictionDateAndPlace) {
		this.convictionDateAndPlace = convictionDateAndPlace;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getContactTelephone() {
		return contactTelephone;
	}

	public void setContactTelephone(String contactTelephone) {
		this.contactTelephone = contactTelephone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

//	public String getKASNEBFAQNo() {
//		return KASNEBFAQNo;
//	}
//
//	public void setKASNEBFAQNo(String kASNEBFAQNo) {
//		KASNEBFAQNo = kASNEBFAQNo;
//	}

//	public String getCPAFAQ() {
//		return CPAFAQ;
//	}
//
//	public void setCPAFAQ(String cPAFAQ) {
//		CPAFAQ = cPAFAQ;
//	}

	public String getExaminationBody() {
		return examinationBody;
	}

	public void setExaminationBody(String examinationBody) {
		this.examinationBody = examinationBody;
	}

	public int getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(int paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public int getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(int applicantType) {
		this.applicantType = applicantType;
	}

	public String getResponsibilityCenter() {
		return responsibilityCenter;
	}

	public void setResponsibilityCenter(String responsibilityCenter) {
		this.responsibilityCenter = responsibilityCenter;
	}

	public int getDocumentType() {
		return documentType;
	}

	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public Date getAge() {
		return age;
	}

	public void setAge(Date age) {
		this.age = age;
	}

	public String getCity1() {
		return city1;
	}

	public void setCity1(String city1) {
		this.city1 = city1;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEmployerCode() {
		return employerCode;
	}

	public void setEmployerCode(String employerCode) {
		this.employerCode = employerCode;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getGenBusPostingGroup() {
		return genBusPostingGroup;
	}

	public void setGenBusPostingGroup(String genBusPostingGroup) {
		this.genBusPostingGroup = genBusPostingGroup;
	}

	public String getVatBusPostingGroup() {
		return vatBusPostingGroup;
	}

	public void setVatBusPostingGroup(String vatBusPostingGroup) {
		this.vatBusPostingGroup = vatBusPostingGroup;
	}

	public int getApplicationMethod() {
		return applicationMethod;
	}

	public void setApplicationMethod(int applicationMethod) {
		this.applicationMethod = applicationMethod;
	}

	public CategoryDto getCategory() {
		return category;
	}

	public void setCategory(CategoryDto category) {
		this.category = category;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}
	
	@Override
	public String toString() {
		
		return "{email:"+email+",address1:"+address1+"}";
	}
	
}
