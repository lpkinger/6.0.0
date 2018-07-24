package com.uas.erp.core.bind;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;

//sheet电子表格对应poi解析的常量
public class ExcelConstant {
	//水平布局
	public final static Map<Short, String> TextAlign = new HashMap<Short, String>() {{    
	    put(CellStyle.ALIGN_CENTER, "center");    
	    put(CellStyle.ALIGN_LEFT, "left");
	    put(CellStyle.ALIGN_RIGHT, "right");
	    put(CellStyle.ALIGN_JUSTIFY, "justify");
	}};
	//垂直布局
	public final static Map<Short, String> VerticalAlign = new HashMap<Short, String>() {{    
	    put(CellStyle.VERTICAL_TOP, "top");    
	    put(CellStyle.VERTICAL_BOTTOM, "bottom");
	    put(CellStyle.VERTICAL_CENTER, "middle");
	}};
	//边框样式
	public final static Map<Short, String> Border = new HashMap<Short, String>() {{    
	    put(CellStyle.BORDER_THIN, "thin");    
	    put(CellStyle.BORDER_HAIR, "hair");
	    put(CellStyle.BORDER_DASHED, "dashed");
	    put(CellStyle.BORDER_DOTTED, "dotted");
	    put(CellStyle.BORDER_MEDIUM_DASHED, "mediumDashed");
	    put(CellStyle.BORDER_MEDIUM, "medium");
	    put(CellStyle.BORDER_DOUBLE, "double");
	    put(CellStyle.BORDER_THICK, "thick");
	}};
	//格式
	public final static Map<Short, String> Format = new HashMap<Short, String>() {{    
	    put(CellStyle.BORDER_THIN, "thin");    
	}};
	//数据格式
	public final static Map<String, String> DataFormat = new HashMap<String, String>() {{    
	    put("yyyy\"年\"m\"月\"d\"日\";@", "Y年m月d日");
	    put("yyyy/m/d;@", "Y/m/d"); 
	    put("m\"月\"d\"日\";@", "m月d日");
	    put("0.00%", "0.00%");
	}};
	
	
	
	
	
	//根据map的value获取map的key  
    private static short getKey(Map<Short,String> map,String value){  
        Short key=null;  
        for (Map.Entry<Short, String> entry : map.entrySet()) {  
            if(value.equals(entry.getValue())){  
                key=entry.getKey();  
            }  
        }  
        return key;  
    }  
	
	//获得水平布局
	public static String getTextAlign(Short s) {
		return TextAlign.get(s);
	}
	//获得垂直布局
	public static String getVerticalAlign(Short s) {
		return VerticalAlign.get(s);
	}
	//获得边框样式
	public static String getBorder(Short s){
		return Border.get(s);
	}
	//获得数据格式化公式
	public static String getDataFormat(String s){
		return DataFormat.get(s);
	}
	
}
