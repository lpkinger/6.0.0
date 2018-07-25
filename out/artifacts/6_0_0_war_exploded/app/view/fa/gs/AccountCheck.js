Ext.define('erp.view.fa.gs.AccountCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'fa/gs/saveAccountCheck.action',
				deleteUrl: 'fa/gs/deleteAccountCheck.action',
				updateUrl: 'fa/gs/updateAccountCheck.action',
				auditUrl: 'fa/gs/auditAccountCheck.action',
				resAuditUrl: 'fa/gs/resAuditAccountCheck.action',
				submitUrl: 'fa/gs/submitAccountCheck.action',
				resSubmitUrl: 'fa/gs/resSubmitAccountCheck.action',
				accountedUrl: 'fa/gs/accountAccountCheck.action',
				resAccountedUrl: 'fa/gs/resAccountAccountCheck.action',	
				getIdUrl: 'common/getId.action?seq=ACCOUNTCHECK_SEQ',
				codeField: 'acc_code',
				keyField: 'acc_id',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'acd_detno',
				necessaryField: 'acd_catecode',
				keyField: 'acd_id',
				mainField: 'acd_acid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});