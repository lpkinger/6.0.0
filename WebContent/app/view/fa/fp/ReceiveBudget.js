Ext.define('erp.view.fa.fp.ReceiveBudget',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 30%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveReceiveBudget.action',
				deleteUrl: 'fa/fp/deleteReceiveBudget.action',
				updateUrl: 'fa/fp/updateReceiveBudget.action',
				auditUrl: 'fa/fp/auditReceiveBudget.action',
				resAuditUrl: 'fa/fp/resAuditReceiveBudget.action',
				submitUrl: 'fa/fp/submitReceiveBudget.action',
				resSubmitUrl: 'fa/fp/resSubmitReceiveBudget.action',
				printUrl: 'fa/fp/printReceiveBudget.action',
				getIdUrl: 'common/getId.action?seq=ReceiveBudget_SEQ',
				keyField: 'rb_id',	
				codeField: 'rb_code',
				statusField: 'rb_statuscode'		
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				keyField: 'rbd_id',
				detno: 'rbd_detno',
				mainField: 'rbd_rbid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});