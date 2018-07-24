package com.uas.erp.service.common.uu.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;

@Service
public class Friends {
	
	@Autowired
	private BaseDao baseDao;
	
	private long ID;
	private String domain = "58.61.153.82";
	
    private static final String LOAD_ID =
            "SELECT id FROM ofID WHERE idType=?";
    
    private static final String LOAD_DOMAIN =
    		"SELECT PROPVALUE FROM OFPROPERTY WHERE NAME = 'xmpp.domain'";

    private static final String UPDATE_ID =
            "UPDATE ofID SET id=? WHERE idType=? AND id=?";
    
    private static final String CREATE_ROSTER_ITEM =
            "INSERT INTO ofRoster (username, rosterID, jid, sub, ask, recv, nick) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String CREATE_ROSTER_ITEM_GROUPS =
            "INSERT INTO ofRosterGroups (rosterID, rank, groupName) VALUES (?, ?, ?)";
    
    public Friends() {
    	
    }
	
    private void connect() {
    	SpObserver.putSp("uu");
    }
    
    public void beFriends(String jid, String jid2) {
    	this.domain = getDomain();
    	this.ID = getAndUpdateRosterID();
    	updateOfRoster(jid, jid2);
    }
    
	public void beFriends(String jid, String jid2, String groupName) {
		this.domain = getDomain();
		this.ID = getAndUpdateRosterID();
		updateOfRoster(jid, jid2);
		updateOfRosterGroups(groupName);
		
	}
	
	public String getDomain() {
		connect();
		String domainStr = null;
		SqlRowList rs = baseDao.queryForRowSet(LOAD_DOMAIN);
		while (rs.next()) {
			domainStr = rs.getString("propvalue");
		}
		if (domainStr == null) {
			domainStr = "www.usoftchina.com";
		}
		return domainStr;
	}
	
	public long getAndUpdateRosterID() {
		connect();
		long currentID = 1;
		SqlRowList rs = baseDao.queryForRowSet(LOAD_ID, 18);
		if (rs.next()) {
			currentID = rs.getLong(1);
		}
		long newID = currentID + 5;
		baseDao.execute(UPDATE_ID, newID, 18, currentID);
		return currentID;
	}
	
	public void updateOfRoster(String jid, String jid2) {
		connect();
		baseDao.execute(CREATE_ROSTER_ITEM, jid, ID, jid2 + "@" + domain, 3, -1, -1, jid2);
		baseDao.execute(CREATE_ROSTER_ITEM, jid2, ID + 1, jid + "@" + domain, 3, -1, -1, jid);
	}
	
	public void updateOfRosterGroups(String groupName) {
		connect();
		baseDao.execute(CREATE_ROSTER_ITEM_GROUPS, ID, 0, groupName);
		baseDao.execute(CREATE_ROSTER_ITEM_GROUPS, ID + 1, 0, groupName);
	}
}