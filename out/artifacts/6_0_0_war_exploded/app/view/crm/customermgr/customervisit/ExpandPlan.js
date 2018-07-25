Ext.define('erp.view.crm.customermgr.customervisit.ExpandPlan',{ 
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
					anchor: '100% 50%',
					saveUrl: 'crm/customermgr/saveExpandPlan.action?caller='+caller,
					deleteUrl: 'crm/customermgr/deleteExpandPlan.action?caller='+caller,
					updateUrl: 'crm/customermgr/updateExpandPlan.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=ExpandPlan_SEQ',
					auditUrl: 'crm/customermgr/auditExpandPlan.action?caller='+caller,
					resAuditUrl: 'crm/customermgr/resAuditExpandPlan.action?caller='+caller,
					submitUrl: 'crm/customermgr/submitExpandPlan.action?caller='+caller,
					resSubmitUrl: 'crm/customermgr/resSubmitExpandPlan.action?caller='+caller,
					keyField: 'ep_id',
					codeField: 'ep_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					keyField: 'epd_id',
					detno: 'epd_detno',
					mainField: 'epd_epid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});