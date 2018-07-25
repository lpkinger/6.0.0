Ext.define('erp.view.fa.gs.AccountRegisterPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				anchor: '100% 100%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/gs/saveAccountRegisterPlan.action',
				deleteUrl: 'fa/gs/deleteAccountRegisterPlan.action',
				updateUrl: 'fa/gs/updateAccountRegisterPlan.action',
				submitUrl: 'fa/gs/submitAccountRegisterPlan.action',
				auditUrl: 'fa/gs/auditAccountRegisterPlan.action',
				resAuditUrl: 'fa/gs/resAuditAccountRegisterPlan.action',					
				resSubmitUrl: 'fa/gs/resSubmitAccountRegisterPlan.action',
				getIdUrl: 'common/getId.action?seq=ACCOUNTREGISTERPLAN_SEQ',
				keyField: 'arp_id',
				statusField: 'arp_statuscode',
				codeField: 'arp_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});