package com.icpak.rest.models.cpd;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.icpak.rest.models.base.PO;
import com.icpak.rest.models.util.Attachment;
import com.wordnik.swagger.annotations.ApiModel;
import com.workpoint.icpak.shared.model.CPDCategory;
import com.workpoint.icpak.shared.model.CPDDto;
import com.workpoint.icpak.shared.model.CPDStatus;

/**
 * Simple class that represents any User domain entity in any application.
 */

@ApiModel(value = "CPD Model", description = "This is a CPD instance model")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "cpd")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CPD extends PO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date startDate; // Copied from Event details;
	private Date endDate;
	private String title;
	private String organizer;
	@Enumerated(EnumType.STRING)
	private CPDCategory category;
	private Double cpdHours;
	@Enumerated(EnumType.STRING)
	private CPDStatus status = CPDStatus.Unconfirmed;
	@Column(length = 20)
	private String memberRefId;
	@Column(length = 20)
	private String memberRegistrationNo;
	@Type(type = "text")
	private String managementComment;
	private String eventId;
	@OneToMany(mappedBy = "cpd", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
	private Set<Attachment> attachments = new HashSet<>();
	@Transient
	private String fullnames;
	private String eventLocation;

	public String getManagementComment() {
		return managementComment;
	}

	public void setManagementComment(String managementComment) {
		this.managementComment = managementComment;
	}

	public CPD() {
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public CPDStatus getStatus() {
		return status;
	}

	public void setStatus(CPDStatus status) {
		this.status = status;
	}

	public void copyFrom(CPDDto dto) {
		setCpdHours(dto.getCpdHours());
		setEndDate(dto.getEndDate());
		setStartDate(dto.getStartDate());
		if (dto.getStatus() != null)
			setStatus(dto.getStatus());
		setCategory(dto.getCategory());
		setMemberRefId(dto.getMemberRefId());
		if (dto.getOrganizer() != null) {
			setOrganizer(dto.getOrganizer());
		}
		setTitle(dto.getTitle());
		setEventId(dto.getEventId());
		setEventLocation(dto.getEventLocation());
		setMemberRegistrationNo(dto.getMemberRegistrationNo());
		setCpdHours(dto.getCpdHours());
		setManagementComment(dto.getManagementComment());
	}

	public CPDDto toDTO() {
		CPDDto dto = new CPDDto();
		dto.setCreated(getCreated());
		dto.setMemberRefId(memberRefId);
		dto.setRefId(getRefId());
		dto.setCategory(category);
		dto.setCpdHours(cpdHours);
		dto.setEndDate(endDate);
		dto.setOrganizer(organizer);
		dto.setStartDate(startDate);
		dto.setStatus(status);
		dto.setTitle(title);
		dto.setEventId(eventId);
		dto.setEventLocation(eventLocation);
		dto.setMemberRegistrationNo(memberRegistrationNo);
		dto.setUpdatedBy(getUpdatedBy());
		if (fullnames != null) {
			dto.setFullNames(fullnames);
		}
		return dto;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public CPDCategory getCategory() {
		return category;
	}

	public void setCategory(CPDCategory category) {
		this.category = category;
	}

	public String getMemberRefId() {
		return memberRefId;
	}

	public void setMemberRefId(String memberRefId) {
		this.memberRefId = memberRefId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getMemberRegistrationNo() {
		return memberRegistrationNo;
	}

	public void setMemberRegistrationNo(String memberRegistrationNo) {
		this.memberRegistrationNo = memberRegistrationNo;
	}

	public void setCpdHours(double cpdHours) {
		this.cpdHours = cpdHours;
	}

	public double getCpdHours() {
		return cpdHours;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventName) {
		this.eventLocation = eventName;
	}

	public Set<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getFullnames() {
		return fullnames;
	}

	public void setFullnames(String fullnames) {
		this.fullnames = fullnames;
	}

	public void setCpdHours(Double cpdHours) {
		this.cpdHours = cpdHours;
	}

}
