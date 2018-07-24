package com.uas.erp.model.mapper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnType;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;

public abstract class SqlParameterMapper<UserObject, JdbcType> implements SqlReturnType {

	/**
	 * {@inheritDoc}
	 * 
	 * @see SqlReturnType#getTypeValue(CallableStatement, int, int, String)
	 */
	@Override
	public final Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName) throws SQLException {

		@SuppressWarnings("unchecked")
		final JdbcType jdbcType = (JdbcType) cs.getObject(paramIndex);

		if (jdbcType == null) {
			return null;
		}

		return createObject(jdbcType);
	}

	/**
	 * Konvertiere das JDBC-Datenbankobjekt in ein Userobjekt.
	 * 
	 * @param jdbcType
	 *            JDBC-Datenbankobjekt
	 * @return das neue Userobjekt
	 * @throws SQLException
	 *             falls ein Fehler bei den Datenbankzugriffen auftritt
	 */
	protected abstract UserObject createObject(JdbcType jdbcType) throws SQLException;

	/**
	 * Erzeuge einen {@link SqlTypeValue} Objekt aus dem Userobjekt.
	 * 
	 * @param userObject
	 *            das Userobjekt
	 * @return das {@link SqlTypeValue} Objekt
	 */
	public final SqlTypeValue createSqlTypeValue(final UserObject userObject) {
		return new AbstractSqlTypeValue() {

			/**
			 * @see AbstractSqlTypeValue#createTypeValue(Connection, int,
			 *      String)
			 */
			@Override
			protected final Object createTypeValue(Connection con, int sqlType, String typeName) throws SQLException {

				return createSqlValue(con, userObject);
			}
		};
	}

	/**
	 * Konvertiere das Userobjekt in ein JDBC-Datenbankobjekt.
	 * 
	 * @param con
	 *            die Datenbankverbindung
	 * @param userObject
	 *            das Userobjekt
	 * @return das neue JDBC-Datenbankobjekt
	 * @throws SQLException
	 *             falls ein Fehler bei den Datenbankzugriffen auftritt
	 */
	protected abstract JdbcType createSqlValue(Connection con, UserObject userObject) throws SQLException;

	/**
	 * Erzeuge einen {@link SqlParameter} für diesen Mapper.
	 * 
	 * @param paramaterName
	 *            der Name des Parameters
	 * @param outParameter
	 *            Ist der Parameter ein Ausgabe?
	 * @return der Parameter.
	 */
	public abstract SqlParameter createSqlParameter(String paramaterName, boolean outParameter);

}
