package com.uas.erp.model;

import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 获取新浪天气
 */
public class Weather implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String weather; // 保存天气情况
	private String code; // 天气编号
	private String high; // 保存当天最高温度
	private String low; // 保存当天最低温度

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String weather) {
		if (weather != null) {
			if (weather.equals("多云")) {
				this.code = "duoyun";
			} else if (weather.equals("小雨")) {
				this.code = "xiaoyu";
			} else if (weather.equals("中雨")) {
				this.code = "zhongyu";
			} else if (weather.equals("大雨")) {
				this.code = "dayu";
			} else if (weather.equals("暴雨")) {
				this.code = "baoyu";
			} else if (weather.equals("阴")) {
				this.code = "yin";
			} else if (weather.equals("小雪")) {
				this.code = "xiaoxue";
			} else if (weather.equals("中雪")) {
				this.code = "daxue";
			} else if (weather.equals("大雪")) {
				this.code = "daxue";
			} else {
				this.code = "qing";
			}
		} else {
			this.code = "qing";
		}
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	/**
	 * 获取天气 解析xml
	 * 
	 * @param city
	 *            城市
	 * @param day
	 *            天 {0-今天、1-明天}{day<5}
	 */
	public static Weather getweather(String city, int day) {
		URL ur;
		Weather w = new Weather();
		try {
			DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dombuilder = domfac.newDocumentBuilder();
			Document doc;
			Element root;
			NodeList books;
			ur = new URL("http://php.weather.sina.com.cn/xml.php?city=" + URLEncoder.encode(city, "gb2312")
					+ "&password=DJOYnieT8234jlsK&day=" + day);
			doc = dombuilder.parse(ur.openStream());
			root = doc.getDocumentElement();
			books = root.getChildNodes();
			for (Node node = books.item(1).getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.getNodeName().equals("status1")) {
						w.setWeather(node.getTextContent()); // 获取到天气情况
						w.setCode(w.getWeather());
					} else if (node.getNodeName().equals("temperature1")) {
						w.setHigh(node.getTextContent()); // 获取到最高温度
					} else if (node.getNodeName().equals("temperature2")) {
						w.setLow(node.getTextContent()); // 获取到最低温度
					}
				}
			}
			return w;
		} catch (Exception e) {
			return w;
		}
	}

	/**
	 * 获取天气
	 * 
	 * @param city
	 *            城市
	 * @param day
	 *            天
	 */
	public static Weather getweather(String city, String day) {
		return getweather(city, getDay(day));
	}

	public Weather() {

	}

	@Override
	public String toString() {
		return weather + " " + low + "℃~" + high + "℃";
	}

	public static int getDay(String day) {
		if (day != null) {
			if (day.equals("今天")) {
				return 0;
			} else if (day.equals("明天")) {
				return 1;
			} else if (day.equals("后天")) {
				return 2;
			}
		}
		return 0;
	}
}
