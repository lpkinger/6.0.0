/**
 * 应付对账单个客户差异
 */
Ext.define('erp.view.fa.arp.ShowAPDiffer',{ 
	extend: 'Ext.Viewport', 
	alias: 'widget.showapdiffer',
	layout: 'anchor', 
	hideBorders: true, 
	id: 'showapdiffer',
	LinkUtil: Ext.create('erp.util.LinkUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	requires: ['erp.util.LinkUtil'],
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',
				anchor: '100% 13%',
				bodyStyle: 'background:#f1f1f1',
				layout: 'column',
				defaults: {
					xtype: 'displayfield'
				},
				items: [{
					fieldLabel: '供应商名称',
					margin: '5 0 0 0',
					labelAlign : "right",
					columnWidth: .5,
					value: vendname
				},{
					fieldLabel: '科目',
					margin: '5 0 0 0',
					labelAlign : "right",
					id: 'am_catecode',
					columnWidth: .25,
					value: catecode
				},{
					fieldLabel: '币别',
					margin: '5 0 0 0',
					labelAlign : "right",
					id: 'am_currency',
					columnWidth: .25,
					value: currency
				}],
				buttonAlign: 'center',
				buttons: [{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0'
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					id:'close',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]
			},{
				xtype: 'grid',
				id:'apdiffergrid',
				anchor: '100% 87%',
				columnLines: true,
				columns: me.defaultColumns,
				plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				store: me.store
			}]
		});
		me.callParent(arguments);
    },
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'tb_code',
        	type: 'string'
        },{
        	name: 'tb_kind',
        	type: 'string'
        },{
        	name: 'tb_vouchercode',
        	type: 'string'
        },{
        	name: 'tb_vonumber',
        	type: 'string'
        },{
        	name: 'tb_apamount',
        	type: 'string'
        },{
        	name: 'tb_debitorcredit',
        	type: 'string'
        },{
        	name: 'tb_glamount',
        	type: 'string'
        },{
        	name: 'tb_balance',
        	type: 'string'
        },{
        	name: 'tb_index',
        	type: 'number'
        },{
        	name: 'tb_void',
        	type:'number'
        }],
        data: []
    }),
    defaultColumns: [{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 150,
		renderer: function(val, meta, record) {
			if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(val) > -1) {
				meta.style = 'font-weight: 700';
			}
			return val;
		}
	},{
		dataIndex: 'tb_code',
		xtype: 'linkcolumn',
		cls: 'x-grid-header-1',
		text: '单据编号',
		width: 130,
		align: 'center',
		renderer: function(val, meta, record) {
			if (['合并制作', '小计', '无'].indexOf(val) > -1) {
				return val;
			}
			return this.defaultRenderer(val, meta, record);
		},
		handler: function(view, cell, rowIdx, cellIdx) {
			var record = view.getStore().getAt(rowIdx);
			var kind = record.get('tb_kind');
			if(!this.LinkUtil){
				this.LinkUtil = Ext.create('erp.util.LinkUtil');
			}
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: 'voucherbill',
		   			field: 'vb_vscode',
		   			condition: 'vb_void=' + record.get('tb_void')
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else if(r.success && r.data){
		   				if (['AccountRegiste','BillAP','BillAPChange','BillAR','BillARChange'].indexOf(r.data) > -1) {
		   					kind = r.data;
		   				}
		   			}
		   		}
			});
			this.openBillPage(this.LinkUtil.getLinkByKind(kind), record);
		},
		openBillPage : function(cfg, record) {
			if (!cfg) {
				return;
			}
			var me = this, k = cfg.keyfield, m = cfg.mainfield, code = record.get('tb_code'),
				condition = (cfg.codefield + '=\'' + code + '\'' + (cfg.kindfield ? (' and ' + cfg.kindfield + '=\'' + cfg.kind + '\'') : ''));
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: cfg.table,
		   			field: k,
		   			condition: condition
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else if(r.success && r.data){
		   				cfg.url += cfg.url.indexOf('?') > 0 ? '&' : '?';
		   				openUrl2(cfg.url + 'formCondition=' + k + 'IS' + r.data + '&gridCondition=' + m + 'IS' + r.data, cfg.kind + '(' + code + ')');
		   			}
		   		}
			});
		}
	},{
		dataIndex: 'tb_apamount',
		cls: 'x-grid-header-1',
		text: '应付系统金额',
		width: 110,
		align:'right', 
		xtype:'numbercolumn',
		format: '0,000.00'
	},{
		dataIndex: 'tb_vonumber',
		xtype: 'linkcolumn',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 80,
		align: 'center',
		linkUrl: 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS{tb_void}&gridCondition=vd_voidIS{tb_void}',
		linkTabTitle: '凭证({tb_vouchercode})'
	},{
		dataIndex: 'tb_debitorcredit',
		cls: 'x-grid-header-1',
		text: '方向',
		width: 0
	},{
		dataIndex: 'tb_glamount',
		cls: 'x-grid-header-1',
		text: '总账系统金额',
		width: 110,
		align:'right',
		xtype:'numbercolumn',
		format: '0,000.00'
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1 isCount',
		text: '差额',
		width: 110,
		align:'right',
		format: '0,000.00',
		xtype:'numbercolumn',
		renderer: function(val, meta, record) {
			if(val!=0){
				if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(record.get('tb_kind')) > -1) {
					meta.style = 'font-weight: 700';
				}
				return Ext.util.Format.number(val, '0,000.00');
			}
		}
	}]
});