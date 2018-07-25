Ext.define('erp.view.crm.marketmgr.marketresearch.ResearchProject',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'crm/marketmgr/saveResearchProject.action',
					deleteUrl: 'crm/marketmgr/deleteResearchProject.action',
					updateUrl: 'crm/marketmgr/updateResearchProject.action',
					auditUrl: 'crm/marketmgr/auditResearchProject.action',
					resAuditUrl: 'crm/marketmgr/resAuditResearchProject.action',
					submitUrl: 'crm/marketmgr/submitResearchProject.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitResearchProject.action',
					getIdUrl: 'common/getId.action?seq=ResearchProject_SEQ',
					keyField: 'pp_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					necessaryField: 'ppd_costname',
					keyField: 'ppd_id',
					detno: 'ppd_detno',
					mainField: 'ppd_ppid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});