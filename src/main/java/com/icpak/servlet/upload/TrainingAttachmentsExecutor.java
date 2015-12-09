package com.icpak.servlet.upload;

import gwtupload.server.exceptions.UploadActionException;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.icpak.rest.dao.ApplicationFormDao;
import com.icpak.rest.dao.AttachmentsDao;
import com.icpak.rest.models.membership.ApplicationFormAccountancy;
import com.icpak.rest.models.membership.ApplicationFormTraining;
import com.icpak.rest.models.util.Attachment;

@Transactional
public class TrainingAttachmentsExecutor extends FileExecutor {

	@Inject
	ApplicationFormDao dao;

	@Inject
	AttachmentsDao attachmentsDao;

	@Override
	String execute(HttpServletRequest request, List<FileItem> sessionFiles)
			throws UploadActionException {
		String err = null;
		String trainingRefId = request.getParameter("trainingRefId");
		String accountancyRefId = request.getParameter("accountancyRefId");

		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				try {
					String fieldName = item.getFieldName();
					String contentType = item.getContentType();
					String name = item.getName();
					long size = item.getSize();

					Attachment attachment = new Attachment();
					attachment.setCreated(new Date());
					attachment.setContentType(contentType);
					attachment.setId(null);
					attachment.setName(name);
					attachment.setSize(size);
					attachment.setAttachment(item.get());
					if (trainingRefId != null) {
						ApplicationFormTraining trainingDetails = dao
								.findByRefId(trainingRefId,
										ApplicationFormTraining.class);
						attachment.setApplicationTraining(trainingDetails);
					} else if (accountancyRefId != null) {
						ApplicationFormAccountancy accountancyDetails = dao
								.findByRefId(accountancyRefId,
										ApplicationFormAccountancy.class);
						attachment
								.setApplicationAccountancy(accountancyDetails);
					}
					attachmentsDao.save(attachment);
					registerFile(request, fieldName, attachment.getId());
				} catch (Exception e) {
					e.printStackTrace();
					err = e.getMessage();
				}
			}
		}
		return err;
	}

	public void removeItem(HttpServletRequest request, String fieldName) {
		Hashtable<String, Long> receivedFiles = getSessionFiles(request, false);
		Long fileId = receivedFiles.get(fieldName);

		if (fileId != null)
			attachmentsDao.delete(attachmentsDao.getById(Attachment.class,
					fileId));
	}

}
