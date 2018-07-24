package com.uas.erp.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class IMBaseUtil {
	
	public static Connection getConnection(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		String url = "jdbc:oracle:thin:@192.168.253.6:1521:ORCL";
//		String user = "uaserp600";
//		String password = "tx2x9saq";
		String user = "dbusoft";
		String password = "dbusoft";
		try {
			Connection conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Statement createStatement(Connection conn){
		if (conn != null) {
			try {
				return conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static void close(Connection conn){
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void insert(String sql){
		Connection conn = getConnection();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);			
		}		
	}
//	public static void main(String[] args){
//		Connection conn = getConnection();
//		System.out.println(conn==null);
//		Statement sm = createStatement(conn);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		if (sm != null) {
//			try {
//				sm.executeQuery("select JP_LAUNCHTIME from jprocess where jp_id=44542 ");
//				ResultSet rs = sm.getResultSet();
//				while (rs.next()) {
//					try {
//						Date d = sdf.parse(rs.getString(1));
//						d.setHours(d.getHours()+10);
//						System.out.println(d.toLocaleString());
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
////					long t = rs.getDate(1).getTime();
////					System.out.println(new Date(t));
//					
//				}
//				close(conn);
//				System.out.println(conn==null);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
