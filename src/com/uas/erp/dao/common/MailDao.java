package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.Mail;

public interface MailDao {
	void saveNewReceMail(List<Mail> mails);
	List<Mail> getUnReadMail(String email, int page, int pageSize);
	List<Mail> getHaveReadMail(String email, int page, int pageSize);
	List<Mail> getAllReceMail(String email, int page, int pageSize);
	List<Mail> getDeletedReceMail(String email, int page, int pageSize);
	List<Mail> getDeletedPostMail(String email, int page, int pageSize);
	List<Mail> getPostedMail(String email, int page, int pageSize);
	List<Mail> getDraftMail(String email, int page, int pageSize);
	Mail getMailDetail(int id);
	void updateStatus(int id, int status);
}
