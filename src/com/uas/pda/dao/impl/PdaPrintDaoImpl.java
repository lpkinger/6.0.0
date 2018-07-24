package com.uas.pda.dao.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.print.PrintException;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.Fnthex;
import com.uas.pda.dao.PdaPrintDao;

@Repository("pdaPrintDaoImpl")
public class PdaPrintDaoImpl implements PdaPrintDao {

	byte[] dotfont;
	String s_prt = "^XA", s_prt_buffer = "";
	int DPI ;

    /**
	 * PDA打印服务器，采用斑马打印自带的编程语言ZPL，传送指令到打印机进行打印
     * @return 
	 */
	@Override
	public String pdaPrint(List<Map<String,Object>> data,String printIp,String port,String dpi) {						
		// TODO Auto-generated method stub
		String [] pageSize= data.get(0).get("LA_PAGESIZE").toString().split("\\*");	
		int OrD = Integer.valueOf(dpi);
		if(OrD == 152){
			DPI = 6;
		}else if(OrD == 203){
			DPI = 8;
		}else if(OrD == 300){
			DPI = 12;
		}else if(OrD == 600){
			DPI = 24;
		}
		s_prt += "^PW"+Integer.valueOf(pageSize[0])*DPI+"^LL"+Integer.valueOf(pageSize[1])*DPI;
		setCommand(data);
		String str = getCommand();
		try {
			return print(str,printIp,Integer.valueOf(port));
		} catch (PrintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	  }
	
	public String print(String str,String printIp,int port) throws PrintException {
		Socket clientSocket = null;
		try {
			try {
				s_prt = "^XA";
				s_prt_buffer = "";
				clientSocket = new Socket(printIp, port);
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				outToServer.writeBytes(str);
				clientSocket.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "无法连接打印机,请确保打印的地址端口正确，可以使用!";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "无法连接打印机,请确保打印的地址端口正确，可以使用!";
			}finally {
				if (clientSocket != null) {
					clientSocket.close();
				}
			}
		} catch (IOException e1) {
		      e1.printStackTrace();
		}
		   return null;
		}
	
	protected void setCommand(List<Map<String,Object>> list) {
		for(Map<String, Object> s: list){//打印数据
			int left = 0, top = 0,height = 0,width = 0, size;
			char ifshownote ;
			String encode ,font;
			String extend0 = "",extend1 = "" ;			
			if(s.get("new") != null){
				s_prt_buffer +="^XZ ^XA";
			}else{
				  left = Math.round((Float.valueOf(s.get("LP_LEFTRATE").toString())*DPI));
				  top = Math.round((Float.valueOf(s.get("LP_TOPRATE").toString())*DPI)); 
				  height = Math.round((Float.valueOf(s.get("LP_HEIGHT").toString())*DPI));
				  width = Math.round((Float.valueOf(s.get("LP_WIDTH").toString())*DPI));
				font = s.get("LP_FONT").toString();//字体类型
				float mul = (float) (25.4*DPI/72);
				size =  Math.round(Integer.valueOf(s.get("LP_SIZE").toString()) * mul);	//字体大小	//字体大小
				String value = null;
				if(s.get("value")!= null){
					value = s.get("value").toString();				
				}			
				if(s.get("LP_VALUETYPE").equals("text")){//打印字母数字中文			
					printCN(value, left, top, height, width,1,size,font);						
				}else {//条码	
					Object en_code = s.get("LP_ENCODE");
					encode = "BCN";
					if("128A".equals(en_code)){
						extend1 = ">9";				
						;//128A[FD>9],128B[FD>:],128C[FD>;],128Auto[BCN,,Y,N,A],Code39[B3N,,Y,N],EAN8[B8N,,Y,N]
					}else if("128B".equals(en_code)){				  
						extend1 = ">:";
					}else if ("128C".equals(en_code)){
						extend1 = ">;";
					}else if("128Auto".equals(en_code)){
						extend0 = ",N,A";
					}else if ("Code39".equals(en_code)){
						encode = "B3N";
					}else {
						encode = "B8N";
					}
					if("-1".equals(s.get("LP_IFSHOWNOTE"))){
						ifshownote = 'Y';//true,false
					}else{
						ifshownote ='N';
					}
					//s.get("lp_notealignjustify");//0,1,2,3
					printBarcode(value, encode, ifshownote, extend0,extend1, left, top, height-size, width,size);
				}
			}
		}				
	}
	protected String getCommand() {
		if(s_prt_buffer.length()<3){
			BaseUtil.showError("没有需要打印的条码!");
		}
		return s_prt + s_prt_buffer.substring(0, s_prt_buffer.length()-3) ;
	}
	
	/**
	 * 打印英文字符，数字
	 * 
	 * @param str
	 * @param x
	 * @param y
	 */
	protected void printChar(String str, int x, int y, int h, int w) {
		s_prt_buffer += "^FO" + x + "," + y + "^A0," + h + "," + w + "^FD"
				+ str + "^FS";
	}

	protected void printCharR(String str, int x, int y, int h, int w) {
		s_prt_buffer += "^FO" + x + "," + y + "^A0R," + h + "," + w + "^FD"
				+ str + "^FS";
	}
	
	/**
	 * 
	 * @param barcode  //条码内容
	 * @param encode  //编码方式
	 * @param x
	 * @param y
	 * @param h
	 * @param w
	 */
	private void printBarcode(String barcode,String encode,char ifshownote,String extend0,String extend1,int x, int y, int h,int w,int size) {
		s_prt_buffer += "^FO" + x + "," + y + "^BY1,2.5,"+h+"^"+encode+",,Y,N"+extend0+"^A0,"+size+","+size+"^FD"+extend1 + barcode+"^FS"+"\n";
	}
	
	/**
	 *  打印中文字符串
	 * @param strCN
	 * @param x  位于条码x轴的位置左边距
	 * @param y  位于条码y轴的位置顶部边距
	 * @param h  高度字符
	 * @param w  宽度
	 * @param b  字体放大的倍数，可以X,Y轴分开设置
	 * @param size 字体大小
	 * @param font 字体
	 */
	protected void printCN(String strCN, int x, int y, int h, int w, int b,int size,String font) {
		s_prt_buffer = s_prt_buffer+Fnthex.getFontHex(strCN, x, y,size, font);	
	}

	@Override
	public synchronized String printZpl(List<Map<String, Object>>list,String dpi,int width,int height) {
		int OrD = Integer.valueOf(dpi);
		if(OrD == 152){
			DPI = 6;
		}else if(OrD == 203){
			DPI = 8;
		}else if(OrD == 300){
			DPI = 12;
		}else if(OrD == 600){
			DPI = 24;
		}
		s_prt = "^XA";
		s_prt_buffer = "";
		s_prt += "^PW"+width*DPI+"^LL"+height*DPI;
		setCommand(list);
		return(getCommand());
	}
	
	protected void testSetCommand(List<Map<String,Object>> list) {
		for(Map<String, Object> s: list){//打印数据
			int left = 0, top = 0,height = 0,width = 0, size;
			char ifshownote ;
			String encode ,font;
			String extend0 = "",extend1 = "" ;			
			if(s.get("new") != null){
				s_prt_buffer +="^XZ ^XA";
			}else{
				  left = Math.round((Float.valueOf(s.get("LP_LEFTRATE").toString())*DPI));
				  top = Math.round((Float.valueOf(s.get("LP_TOPRATE").toString())*DPI)); 
				  height = Math.round((Float.valueOf(s.get("LP_HEIGHT").toString())*DPI));
				  width = Math.round((Float.valueOf(s.get("LP_WIDTH").toString())*DPI));
				font = s.get("LP_FONT").toString();//字体类型
				float mul = (float) (25.4*DPI/72);
				size =  Math.round(Integer.valueOf(s.get("LP_SIZE").toString()) * mul);	//字体大小	//字体大小
				String value = null;
				if(s.get("value")!= null){
					value = s.get("value").toString().substring(1, s.get("value").toString().length());				
				}			
				if(s.get("LP_VALUETYPE").equals("text")){//打印字母数字中文			
					printCN(value, left, top, height, width,1,size,font);						
				}else {//条码	
					if(s.get("LP_ENCODE").toString().equals("128A")){
						encode = "BCN";
						extend1 = ">9";				
						;//128A[FD>9],128B[FD>:],128C[FD>;],128Auto[BCN,,Y,N,A],Code39[B3N,,Y,N],EAN8[B8N,,Y,N]
					}else if (s.get("LP_ENCODE").toString().equals("128B")){				  
						encode = "BCN";
						extend1 = ">:";
					}else if (s.get("LP_ENCODE").toString().equals("128C")){
						encode = "BCN";
						extend1 = ">;";
					}else if(s.get("LP_ENCODE").toString().equals("128Auto")){
						encode = "BCN";
						extend0 = ",N,A";
					}else if (s.get("LP_ENCODE").toString().equals("Code39")){
						encode = "B3N";
					}else {
						encode = "B8N";
					}
					if(s.get("LP_IFSHOWNOTE").toString().equals("true")){
						ifshownote = 'Y';//true,false
					}else{
						ifshownote ='N';
					}
					//s.get("lp_notealignjustify");//0,1,2,3
					printBarcode(value, encode, ifshownote, extend0,extend1, left, top, height-size, width,size);
				}
			}
		}				
	}

}
