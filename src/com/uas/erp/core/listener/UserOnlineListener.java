package com.uas.erp.core.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.uas.erp.model.Employee;
import com.uas.erp.model.UserSession;

/**
 * 监听器<br>
 * 统计用户在线状态
 * 
 * @author yingp
 */
public class UserOnlineListener implements HttpSessionListener, HttpSessionAttributeListener {

	/**
	 * 存放在线用户列表
	 */
	public static Map<String, UserSession> onlineUsers = new ConcurrentHashMap<String, UserSession>();

	/**
	 * 最大延迟时间(毫秒)
	 */
	private final static Integer MAX_DELAY_MSEC = 15000;

	@Override
	public void attributeAdded(HttpSessionBindingEvent key) {
		if ("employee".equals(key.getName())) {
			/**
			 * 用户上线的话，把用户的ID，添加到onLineUserList里
			 */
			Employee employee = (Employee) key.getValue();
			addUser(employee, key.getSession().getId());
		}
	}

	public static void addUser(Employee employee, String sid) {
		onlineUsers.put(sid, new UserSession(employee.getEm_master(), employee.getEm_id(), employee.getEm_code(), employee.getEm_name(),
				employee.getEm_defaultorid(), sid, employee.getEm_lastip(), new Date(), employee.getEm_pdamobilelogin()));
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent key) {
		if ("employee".equals(key.getName())) {
			/**
			 * 用户下线的话，把用户从onlineUsers中移除
			 */
			onlineUsers.remove(key.getSession().getId());
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {

	}

	/**
	 * @param em_id
	 *            用户ID
	 * @param sob
	 *            账套
	 */
	public static UserSession getUserById(int em_id, String sob) {
		// TODO
		return null;
	}

	/**
	 * @param sid
	 *            sessionID
	 */
	public static UserSession getUserBySId(String sid) {
		return onlineUsers.get(sid);
	}
	
	private static UserSession getUserByEmployee(int em_id, String sob) {
		for (String key : onlineUsers.keySet()) {
			UserSession u = onlineUsers.get(key);
			if (em_id == u.getEm_id() && sob.equals(u.getSob())) {
				return u;
			}
		}
		return null;
	}

	public static List<UserSession> getUsersByEmployee(int em_id, String sob) {
		List<UserSession> userSessions = new ArrayList<UserSession>();
		for (String key : onlineUsers.keySet()) {
			UserSession u = onlineUsers.get(key);
			if (em_id == u.getEm_id() && sob.equals(u.getSob())) {
				userSessions.add(u);
			}
		}		
		return userSessions;
	}
	
	
	/**
	 * 根据用户em_id，查询用户是否在线 <br>
	 * 除了判断user是否在onlineUsers,还要判断user的连接时间是否超时
	 * 
	 * @param em_id
	 *            用户ID
	 * @return UserSession:表示用户在线 <br>
	 *         null:表示用户离线
	 */
	public static UserSession isOnLine(int em_id, String sob) {
		UserSession u = getUserByEmployee(em_id, sob);
		if (u != null && !u.isLocked()) {
			if (u.getDate().getTime() + MAX_DELAY_MSEC >= new Date().getTime())
				return u;
			else
				onlineUsers.remove(u.getSid());
		}
		return null;
	}

	/**
	 * 根据用户sessionID，查询用户是否正常在线 <br>
	 * 除了判断user是否在onLineUserList,还要判断user的连接时间是否超时<br>
	 * 主要是关闭窗口，未关闭浏览器，session保持，出现伪离线状态.<br>
	 * 而此时该账号可能在其他IP或其他浏览器登录，<br>
	 * 为避免重复，该session用户需重新登录.
	 * 
	 * @param em_id
	 *            用户ID
	 * @return true:表示用户在线 <br>
	 *         false:表示用户离线
	 */
	public static boolean isOnLine(String sid) {
		UserSession u = getUserBySId(sid);
		if (u != null && !u.isLocked()) {
			if (u.getDate().getTime() + MAX_DELAY_MSEC >= new Date().getTime())
				return true;
			else
				onlineUsers.remove(sid);
		}
		return false;
	}

	/**
	 * 刷新最近访问时间<br>
	 * 
	 * @param em_id
	 *            用户ID
	 */
	public static void refresh(String sid) {
		UserSession u = getUserBySId(sid);
		if (u != null) {
			u.setDate(new Date());
		}
	}

	/**
	 * 刷新在线用户列表<br>
	 * 去除超时用户
	 */
	public static void refresh() {
		try {
			long time = new Date().getTime();
			for (String key : onlineUsers.keySet()) {
				UserSession u = onlineUsers.get(key);
				if (u != null && u.getDate().getTime() + MAX_DELAY_MSEC < time)
					onlineUsers.remove(key);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
	}

	/**
	 * 在线用户列表
	 * 
	 */
	public static Set<UserSession> getOnLineList() {
		refresh();
		Set<UserSession> users = new HashSet<UserSession>();
		for (String key : onlineUsers.keySet())
			users.add(onlineUsers.get(key));
		return users;
	}

	/**
	 * 切换账套
	 * 
	 * @param sid
	 * @param sob
	 */
	public static void changeMaster(String sid, String sob) {
		UserSession session = getUserBySId(sid);
		if (session != null) {
			session.setSob(sob);
		}
	}

	/**
	 * 账号session被锁定
	 * 
	 * @param sid
	 *            sessionId
	 * @return 1==YES<br>
	 *         0==NO<br>
	 *         -1==NO_SESSION_FOUND
	 */
	public static int isLocked(String sid) {
		UserSession u = getUserBySId(sid);
		if (u != null) {
			return u.isLocked() ? 1 : 0;
		}
		return -1;
	}

	public static void lock(String sid) {
		UserSession u = getUserBySId(sid);
		if (u != null) {
			u.setLocked(true);
		}
	}
	
	/**
	 * 账号session被踢
	 * @param sid
	 * @return
	 */
	public static Boolean isKicked(String sid) {
		UserSession u = getUserBySId(sid);
		if (u!=null) {
			return u.isKicked();
		}
		return false;
	}
	
	public static void Kick(UserSession u){
		if (u!=null) {
			u.setKicked(true);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	}
}
