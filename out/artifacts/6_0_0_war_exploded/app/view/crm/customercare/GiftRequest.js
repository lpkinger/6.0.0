Ext.define('erp.view.crm.customercare.GiftRequest',{ 
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
					anchor: '100% 50%',
					saveUrl: 'crm/customercare/saveGiftRequest.action',
					deleteUrl: 'crm/customercare/deleteGiftRequest.action',
					updateUrl: 'crm/customercare/updateGiftRequest.action',		
					getIdUrl: 'common/getId.action?seq=GiftRequest_SEQ',
					auditUrl: 'crm/customercare/auditGiftRequest.action',
					resAuditUrl: 'crm/customercare/resAuditGiftRequest.action',
					submitUrl: 'crm/customercare/submitGiftRequest.action',
					resSubmitUrl: 'crm/customercare/resSubmitGiftRequest.action',
					keyField: 'gr_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'gqd_gicode',
					keyField: 'gqd_id',
					detno: 'gqd_id',
					mainField: 'gqd_grid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});