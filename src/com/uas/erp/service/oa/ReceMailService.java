package com.uas.erp.service.oa;

import java.util.List;
import com.uas.erp.model.Mail;

public interface ReceMailService {
	List<Mail> getUnReadMails( int page, int pageSize);
	void getNewMails();
	List<Mail> getHaveReadMail( int page, int pageSize);
	List<Mail> getAllReceMail( int page, int pageSize);
	List<Mail> getDeletedReceMail( int page, int pageSize);
	List<Mail> getDeletedPostMail( int page, int pageSize);
	List<Mail> getPostedMail( int page, int pageSize);
	List<Mail> getDraftMail( int page, int pageSize);
	Mail getMailDetail(int id);
	void changeMailStatus(int id, int status);
	void updateMailStatus(int[] id, int status);
}
