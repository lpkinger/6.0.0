package com.uas.erp.ac.service.common;

import java.util.Map;

public interface InvitationRecordService {

	public Map<String, Object> invitations(String keyword, Integer start, Integer pageNumber, Integer pageSize, int value)
			throws Exception;

	public Map<String, Object> getInvitationsRecord(Integer start, Integer page, Integer limit, String _state,String keyword);

	public Map<String, Object> getInvitationCount();

}
