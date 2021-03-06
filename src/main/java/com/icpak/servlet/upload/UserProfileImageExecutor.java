package com.icpak.servlet.upload;

import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.icpak.rest.IDUtils;
import com.icpak.rest.dao.AttachmentsDao;
import com.icpak.rest.dao.helper.AttachmentDaoHelper;
import com.icpak.rest.models.util.Attachment;
import com.icpak.rest.util.ApplicationSettings;

@Transactional
public class UserProfileImageExecutor extends FileExecutor {

	@Inject
	AttachmentDaoHelper helper;
	@Inject
	AttachmentsDao attachmentsDao;
	@Inject
	ApplicationSettings settings;

	@Override
	String execute(HttpServletRequest request, List<FileItem> sessionFiles)
			throws UploadActionException {
		String err = null;
		String userRefId = request.getParameter("userRefId");
		assert userRefId != null;

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
					String fileName = getFilePath() + File.separator
							+ IDUtils.generateId() + "-PROFILE-" + name;
					attachment.setFileName(fileName);
					attachment.setProfilePicUserId(userRefId);

					attachmentsDao.deleteUserImage(userRefId);
					attachmentsDao.save(attachment);

					IOUtils.write(item.get(), new FileOutputStream(new File(
							fileName)));

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

		Attachment attachment = null;
		File file = null;

		if (fileId != null)
			attachment = attachmentsDao.getById(Attachment.class, fileId);
		file = new File(attachment.getFileName());
		file.delete();
		attachmentsDao.delete(attachment);
	}

	private String getFilePath() {
		return settings.getProperty("upload_path");
	}

}
