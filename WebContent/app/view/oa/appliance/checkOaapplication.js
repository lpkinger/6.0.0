Ext.define('erp.view.oa.appliance.checkOaapplication',{ 
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
					_noc: _noc,
					//saveUrl: 'common/saveCommon.action',
					//deleteUrl: 'oa/appliance/deleteOaappliance.action',
					updateUrl: 'common/updateCommon.action?caller=' + caller,		
					getIdUrl: 'common/getId.action?seq=Oaappliance_SEQ',
					keyField: 'oa_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});