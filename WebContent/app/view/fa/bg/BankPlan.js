Ext.define('erp.view.fa.bg.BankPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'fa/bg/saveBankPlan.action',
				deleteUrl: 'fa/bg/deleteBankPlan.action',
				updateUrl: 'fa/bg/updateBankPlan.action',
				auditUrl: 'fa/bg/auditBankPlan.action',
				resAuditUrl: 'fa/bg/resAuditBankPlan.action',
				submitUrl: 'fa/bg/submitBankPlan.action',
				resSubmitUrl: 'fa/bg/resSubmitBankPlan.action',
				printUrl:'fa/bg/printBankPlan.action',
				getIdUrl: 'common/getId.action?seq=FaBankPlan_SEQ'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});