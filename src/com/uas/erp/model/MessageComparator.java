package com.uas.erp.model;

import java.util.Comparator;
import javax.mail.Message;
import javax.mail.MessagingException;

@SuppressWarnings("rawtypes")
public class MessageComparator implements Comparator{
	public int compare(Object a, Object b){  
		try {
			Message ma = (Message)a;
			Message mb = (Message)b;
			long aTime = ma.getSentDate().getTime();
			long bTime = mb.getSentDate().getTime();  
			long diff = aTime - bTime;  
			if (diff < 0)  
			    return 1;  
			if (diff > 0)  
			    return -1;  
			else  
			    return 1;  
		} catch (MessagingException e) {
			e.printStackTrace();
			return 1;
		}  
	}
}
