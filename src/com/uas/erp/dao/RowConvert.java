package com.uas.erp.dao;

import org.springframework.util.NumberUtils;

public class RowConvert<T>{

	private Class<T> requiredType;

	public RowConvert(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	@SuppressWarnings("unchecked")
	protected Object convertValueToRequiredType(Object value, @SuppressWarnings("rawtypes") Class requiredType) {
		if (String.class.equals(requiredType)) {
			return value.toString();
		}
		if (Number.class.isAssignableFrom(requiredType)) {
			if (value instanceof Number) {
				return NumberUtils.convertNumberToTargetClass((Number) value, requiredType);
			}
			return NumberUtils.parseNumber(value.toString(), requiredType);
		}
		throw new IllegalArgumentException(
				"Value [" + value + "] is of type [" + value.getClass().getName() +
						"] and cannot be converted to required type [" + requiredType.getName() + "]");
	}

	@SuppressWarnings("unchecked")
	public T convert(Object value) {
		if (value != null && this.requiredType != null && !this.requiredType.isInstance(value)) {
			return (T) convertValueToRequiredType(value, this.requiredType);
		}
		return (T) value;
	}
}
