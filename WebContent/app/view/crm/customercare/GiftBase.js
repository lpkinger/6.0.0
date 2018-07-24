Ext.define('erp.view.crm.customercare.GiftBase',{ 
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
					anchor: '100% 100%',
					saveUrl: 'crm/customercare/saveGiftBase.action',
					deleteUrl: 'crm/customercare/deleteGiftBase.action',
					updateUrl: 'crm/customercare/updateGiftBase.action',		
					getIdUrl: 'common/getId.action?seq=GiftBase_SEQ',
					keyField: 'gi_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});