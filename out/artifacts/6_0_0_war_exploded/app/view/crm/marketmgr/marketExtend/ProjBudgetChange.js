Ext.define('erp.view.crm.marketmgr.marketExtend.ProjBudgetChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'crm/marketmgr/saveProjBudgetChange.action',
					deleteUrl: 'crm/marketmgr/deleteProjBudgetChange.action',
					updateUrl: 'crm/marketmgr/updateProjBudgetChange.action',
					auditUrl: 'crm/marketmgr/auditProjBudgetChange.action',
					resAuditUrl: 'crm/marketmgr/resAuditProjBudgetChange.action',
					submitUrl: 'crm/marketmgr/submitProjBudgetChange.action',
					resSubmitUrl:  'crm/marketmgr/resSubmitProjBudgetChange.action',
					getIdUrl: 'common/getId.action?seq=PROJBUDGETCHANGE_SEQ',
					keyField: 'pbc_id',
					codeField: 'pbc_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});