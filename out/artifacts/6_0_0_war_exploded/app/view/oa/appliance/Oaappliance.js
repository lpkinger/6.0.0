Ext.define('erp.view.oa.appliance.Oaappliance',{ 
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
					saveUrl: 'oa/appliance/saveOaappliance.action',
					deleteUrl: 'oa/appliance/deleteOaappliance.action',
					updateUrl: 'oa/appliance/updateOaappliance.action',		
					getIdUrl: 'common/getId.action?seq=Oaappliance_SEQ',
					keyField: 'oa_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});