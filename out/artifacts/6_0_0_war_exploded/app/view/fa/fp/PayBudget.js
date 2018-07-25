Ext.define('erp.view.fa.fp.PayBudget',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 30%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/savePayBudget.action',
				deleteUrl: 'fa/fp/deletePayBudget.action',
				updateUrl: 'fa/fp/updatePayBudget.action',
				auditUrl: 'fa/fp/auditPayBudget.action',
				resAuditUrl: 'fa/fp/resAuditPayBudget.action',
				submitUrl: 'fa/fp/submitPayBudget.action',
				resSubmitUrl: 'fa/fp/resSubmitPayBudget.action',
				printUrl: 'fa/fp/printPayBudget.action',
				getIdUrl: 'common/getId.action?seq=PayBudget_SEQ',
				keyField: 'pb_id',	
				codeField: 'pb_code',
				statusField: 'pb_statuscode'		
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				keyField: 'pbd_id',
				detno: 'pbd_detno',
				mainField: 'pbd_pbid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});