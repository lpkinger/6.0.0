package com.uas.erp.model;

import java.io.Serializable;

public class OracleObject {
	public static class Trigger implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3642000697121908966L;
		private String trigger_name;
		private String trigger_type;
		private String triggering_event;
		private String table_owner;
		private String base_object_type;
		private String table_name;
		private String column_name;
		private String referencing_names;
		private String when_clause;
		private String status;
		private String description;
		private String action_type;
		private String trigger_body;
		private String crossedition;
		private String before_statement;
		private String before_row;
		private String after_row;
		private String after_statement;
		private String instead_of_row;
		private String fire_once;
		private String apply_server_only;

		public String getTrigger_name() {
			return trigger_name;
		}

		public void setTrigger_name(String trigger_name) {
			this.trigger_name = trigger_name;
		}

		public String getTrigger_type() {
			return trigger_type;
		}

		public void setTrigger_type(String trigger_type) {
			this.trigger_type = trigger_type;
		}

		public String getTriggering_event() {
			return triggering_event;
		}

		public void setTriggering_event(String triggering_event) {
			this.triggering_event = triggering_event;
		}

		public String getTable_owner() {
			return table_owner;
		}

		public void setTable_owner(String table_owner) {
			this.table_owner = table_owner;
		}

		public String getBase_object_type() {
			return base_object_type;
		}

		public void setBase_object_type(String base_object_type) {
			this.base_object_type = base_object_type;
		}

		public String getTable_name() {
			return table_name;
		}

		public void setTable_name(String table_name) {
			this.table_name = table_name;
		}

		public String getColumn_name() {
			return column_name;
		}

		public void setColumn_name(String column_name) {
			this.column_name = column_name;
		}

		public String getReferencing_names() {
			return referencing_names;
		}

		public void setReferencing_names(String referencing_names) {
			this.referencing_names = referencing_names;
		}

		public String getWhen_clause() {
			return when_clause;
		}

		public void setWhen_clause(String when_clause) {
			this.when_clause = when_clause;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAction_type() {
			return action_type;
		}

		public void setAction_type(String action_type) {
			this.action_type = action_type;
		}

		public String getTrigger_body() {
			return trigger_body;
		}

		public void setTrigger_body(String trigger_body) {
			this.trigger_body = trigger_body;
		}

		public String getCrossedition() {
			return crossedition;
		}

		public void setCrossedition(String crossedition) {
			this.crossedition = crossedition;
		}

		public String getBefore_statement() {
			return before_statement;
		}

		public void setBefore_statement(String before_statement) {
			this.before_statement = before_statement;
		}

		public String getBefore_row() {
			return before_row;
		}

		public void setBefore_row(String before_row) {
			this.before_row = before_row;
		}

		public String getAfter_row() {
			return after_row;
		}

		public void setAfter_row(String after_row) {
			this.after_row = after_row;
		}

		public String getAfter_statement() {
			return after_statement;
		}

		public void setAfter_statement(String after_statement) {
			this.after_statement = after_statement;
		}

		public String getInstead_of_row() {
			return instead_of_row;
		}

		public void setInstead_of_row(String instead_of_row) {
			this.instead_of_row = instead_of_row;
		}

		public String getFire_once() {
			return fire_once;
		}

		public void setFire_once(String fire_once) {
			this.fire_once = fire_once;
		}

		public String getApply_server_only() {
			return apply_server_only;
		}

		public void setApply_server_only(String apply_server_only) {
			this.apply_server_only = apply_server_only;
		}
	}

	public static class Index implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4021906488159458409L;
		private String index_name;
		private String index_type;
		private String table_owner;
		private String table_name;
		private String table_type;
		private String uniqueness;
		private String compression;
		private String prefix_length;
		private String tablespace_name;
		private int int_tans;
		private int max_tans;
		private long initial_extent;
		private long next_extent;
		private int min_extents;
		private long max_extents;
		private String status;

		public String getIndex_name() {
			return index_name;
		}

		public void setIndex_name(String index_name) {
			this.index_name = index_name;
		}

		public String getIndex_type() {
			return index_type;
		}

		public void setIndex_type(String index_type) {
			this.index_type = index_type;
		}

		public String getTable_owner() {
			return table_owner;
		}

		public void setTable_owner(String table_owner) {
			this.table_owner = table_owner;
		}

		public String getTable_name() {
			return table_name;
		}

		public void setTable_name(String table_name) {
			this.table_name = table_name;
		}

		public String getTable_type() {
			return table_type;
		}

		public void setTable_type(String table_type) {
			this.table_type = table_type;
		}

		public String getUniqueness() {
			return uniqueness;
		}

		public void setUniqueness(String uniqueness) {
			this.uniqueness = uniqueness;
		}

		public String getCompression() {
			return compression;
		}

		public void setCompression(String compression) {
			this.compression = compression;
		}

		public String getPrefix_length() {
			return prefix_length;
		}

		public void setPrefix_length(String prefix_length) {
			this.prefix_length = prefix_length;
		}

		public String getTablespace_name() {
			return tablespace_name;
		}

		public void setTablespace_name(String tablespace_name) {
			this.tablespace_name = tablespace_name;
		}

		public int getInt_tans() {
			return int_tans;
		}

		public void setInt_tans(int int_tans) {
			this.int_tans = int_tans;
		}

		public int getMax_tans() {
			return max_tans;
		}

		public void setMax_tans(int max_tans) {
			this.max_tans = max_tans;
		}

		public long getInitial_extent() {
			return initial_extent;
		}

		public void setInitial_extent(long initial_extent) {
			this.initial_extent = initial_extent;
		}

		public long getNext_extent() {
			return next_extent;
		}

		public void setNext_extent(long next_extent) {
			this.next_extent = next_extent;
		}

		public int getMin_extents() {
			return min_extents;
		}

		public void setMin_extents(int min_extents) {
			this.min_extents = min_extents;
		}

		public long getMax_extents() {
			return max_extents;
		}

		public void setMax_extents(long max_extents) {
			this.max_extents = max_extents;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}
}
