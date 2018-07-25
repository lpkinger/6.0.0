Ext.define('erp.view.oa.appliance.Oainstorage',{ 
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
					saveUrl: 'oa/appliance/saveOainstorage.action',
					deleteUrl: 'oa/appliance/deleteOainstorage.action',
					updateUrl: 'oa/appliance/updateOainstorage.action',		
					getIdUrl: 'common/getId.action?seq=Oainstorage_SEQ',
					keyField: 'os_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});