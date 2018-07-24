Ext.define('erp.view.crm.marketmgr.marketresearch.MProjectPlan',{ 
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
					saveUrl: 'crm/marketmgr/saveMProjectPlan.action',
					deleteUrl: 'crm/marketmgr/deleteMProjectPlan.action',
					updateUrl: 'crm/marketmgr/updateMProjectPlan.action',
					auditUrl: 'crm/marketmgr/auditMProjectPlan.action',
					resAuditUrl: 'crm/marketmgr/resAuditMProjectPlan.action',
					submitUrl: 'crm/marketmgr/submitMProjectPlan.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitMProjectPlan.action',
					getIdUrl: 'common/getId.action?seq=MProjectPlan_SEQ',
					keyField: 'prjplan_id'
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