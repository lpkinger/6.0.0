Ext.define('erp.view.crm.marketmgr.resourcemgr.PreResource',{ 
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
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=PreResource_SEQ',
					auditUrl: 'crm/auditPreResource.action?caller='+caller,
					resAuditUrl: 'crm/resAuditPreResource.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'pr_id',
					codeField: 'pr_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});