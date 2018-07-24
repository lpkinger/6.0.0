Ext.define('erp.view.fa.arp.payplease.PayPleaseDetailGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.paypleasedetailGrid',
	requires : [ 'erp.view.fa.arp.payplease.PPDtoolbar' ],
	layout : 'fit',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true,
	detno : 'ppd_detno',
	keyField : 'ppd_id',
	mainField : 'ppd_ppid',
	columns : [],
	bodyStyle : 'bachgroud-color:#f1f1f1;',
	plugins : [ Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	initComponent : function() {
		this.callParent(arguments);
		// 得到页面上显示的formCondition属性
		var gridCondition = this.BaseUtil.getUrlParam('gridCondition');
		var condition = gridCondition ? gridCondition.replace(/IS/g, "=") : 'ppd_ppid=0';
		this.getGridColumnsAndStore('common/singleGridPanel.action', {
			caller: this.caller || caller, 
			condition: condition, 
			_m: 0
		});
	},
	getGridColumnsAndStore : function(url, param) {
		var me = this;
		me.setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + url,
			params : param,
			method : 'post',
			callback : function(options, success, response) {
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (me.columns && me.columns.length > 2) {
					var data = res.data != null ? Ext.decode(res.data.replace(
							/,}/g, '}').replace(/,]/g, ']')) : [];
					me.store.loadData(data);
					// 解决固定列左右不对齐的情况
					var lockedView = me.view.lockedView;
					if (lockedView) {
						var tableEl = lockedView.el.child('.x-grid-table');
						if (tableEl) {
							tableEl.dom.style.marginBottom = '7px';
						}
					}
					me.view.refresh();
					me.initRecords();
					me.fireEvent('storeloaded', me);
				} else if (res.columns) {
					var limits = res.limits, limitArr = new Array();
					if (limits != null && limits.length > 0) {// 权限外字段
						limitArr = Ext.Array.pluck(limits, 'lf_field');
					}
					res.fields.push({
						name : 'turned',
						type : "string"
					});
					Ext.each(res.columns, function(column, y) {
						if (column.xtype == 'textareatrigger') {
							column.xtype = '';
							column.renderer = 'texttrigger';
						}
						// column有取别名
						if (column.dataIndex.indexOf(' ') > -1) {
							column.dataIndex = column.dataIndex.split(' ')[1];
						}
						// power
						if (limitArr.length > 0
								&& Ext.Array.contains(limitArr,
										column.dataIndex)) {
							column.hidden = true;
						}
						// renderer
						me.GridUtil.setRenderer(me, column);
						// logictype
						me.GridUtil.setLogicType(me, column, column.logic, {
							headerColor : res.necessaryFieldColor
						});
					});
					// data
					var data = [];
					if (!res.data || res.data == '[]') {
						var d = {};
						d[me.detno] = 1;
						data.push(d);
					} else {
						data = Ext.decode(res.data.replace(/,}/g, '}').replace(
								/,]/g, ']'));
					}
					// store
					var store = me.GridUtil.setStore(me, res.fields, data,
							me.groupField, me.necessaryField);
					// dbfind
					if (res.dbfinds && res.dbfinds.length > 0) {
						me.dbfinds = res.dbfinds;
					}
					me.reconfigure(store, res.columns);
					var form = Ext.ComponentQuery.query('form');
					if (form && form.length > 0) {
						me.readOnly = form[0].readOnly;// grid不可编辑
					}
				}
			}
		});
	}
});