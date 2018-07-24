Ext.define('erp.view.crm.marketmgr.marketresearch.ResearchPlan',{ 
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
					saveUrl: 'crm/marketmgr/saveResearchPlan.action',
					deleteUrl: 'crm/marketmgr/deleteResearchPlan.action',
					updateUrl: 'crm/marketmgr/updateResearchPlan.action',
					auditUrl: 'crm/marketmgr/auditResearchPlan.action',
					resAuditUrl: 'crm/marketmgr/resAuditResearchPlan.action',
					submitUrl: 'crm/marketmgr/submitResearchPlan.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitResearchPlan.action',
					getIdUrl: 'common/getId.action?seq=ResearchPlan_SEQ',
					keyField: 'rp_id',
					codeField: 'rp_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});