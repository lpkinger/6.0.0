package com.uas.erp.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class ContextUtil {
	private static ApplicationContext applicationContext;
	public static void setApplicationContext(ApplicationContext applicationContext) {
		synchronized (ContextUtil.class) {
			ContextUtil.applicationContext = applicationContext;
			ContextUtil.class.notifyAll();
		}
	}

	public static ApplicationContext getApplicationContext() {
		synchronized (ContextUtil.class) {
			while (applicationContext == null) {
				try {
					ContextUtil.class.wait(6000);
				} catch (InterruptedException ex) {
				}
			}
			return applicationContext;
		}
	}

	/**
	 * 获取bean
	 * 
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		try {
			return getApplicationContext().getBean(name);
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * 获取bean
	 * 
	 * @param cls
	 * @return
	 */
	public static <T> T getBean(Class<T> cls) {
		return getApplicationContext().getBean(cls);
	}

	/**
	 * 动态注册bean
	 * 
	 * @param beanName
	 *            bean组件名称
	 * @param className
	 *            类名
	 */
	public static void registerBean(String beanName, String className) {
		try {
			getApplicationContext().getAutowireCapableBeanFactory().createBean(Class.forName(className));
		} catch (BeansException e) {
			BaseUtil.showError("组件注册失败");
		} catch (IllegalStateException e) {
			BaseUtil.showError("组件注册失败");
		} catch (ClassNotFoundException e) {
			BaseUtil.showError("组件注册失败");
		}
	}

	/**
	 * 触发事件
	 * 
	 * @param event
	 */
	public static void publishEvent(ApplicationEvent event) {
		getApplicationContext().publishEvent(event);
	}

}
