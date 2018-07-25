Ext.define('erp.view.crm.marketmgr.marketchange.ChangeProject',{ 
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
					saveUrl: 'crm/marketmgr/saveChangeProject.action',
					deleteUrl: 'crm/marketmgr/deleteChangeProject.action',
					updateUrl: 'crm/marketmgr/updateChangeProject.action',
					getIdUrl: 'common/getId.action?seq=ChangeProject_SEQ',
					auditUrl: 'crm/marketmgr/auditChangeProject.action',
					resAuditUrl: 'crm/marketmgr/resAuditChangeProject.action',
					submitUrl: 'crm/marketmgr/submitChangeProject.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitChangeProject.action',
					keyField: 'cp_id',
					codeField: 'cp_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});