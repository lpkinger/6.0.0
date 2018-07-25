Ext.define('erp.view.crm.marketmgr.resourcemgr.ResourceDistrApply',{ 
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
					anchor: '100% 30%',
					saveUrl: 'crm/marketmgr/saveResourceDistrApply.action',
					deleteUrl: 'crm/marketmgr/deleteResourceDistrApply.action',
					updateUrl: 'crm/marketmgr/updateResourceDistrApply.action',
					getIdUrl: 'common/getId.action?seq=ResourceDistrApply_SEQ',
					auditUrl: 'crm/marketmgr/auditResourceDistrApply.action',
					resAuditUrl: 'crm/marketmgr/resAuditResourceDistrApply.action',
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'ra_id',
					codeField: 'ra_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'rad_detno',
					necessaryField: 'rad_sellercode',
					keyField: 'rad_id',
					mainField: 'rad_raid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});