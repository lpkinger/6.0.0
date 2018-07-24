Ext.define('erp.view.crm.marketmgr.resourcemgr.ResourceDistr',{ 
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
					anchor: '100% 30%',
					saveUrl: 'crm/resource/saveResourceDistr.action',
					deleteUrl: 'crm/resource/deleteResourceDistr.action',
					updateUrl: 'crm/resource/updateResourceDistr.action',
					getIdUrl: 'common/getId.action?seq=ResourceDistr_SEQ',
					keyField: 'pr_id',
					codeField: 'pr_code',
//					statusField: 'as_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'rd_detno',
					necessaryField: 'rd_sellercode',
					keyField: 'rd_id',
					mainField: 'rd_prid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});