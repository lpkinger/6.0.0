Ext.define('erp.view.pm.make.Ration',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'pm/make/saveRation.action',
				updateUrl: 'pm/make/updateRation.action',
				statusField: 'ra_status',
				statuscodeField: 'ra_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'ra_detno',
				keyField: 'ra_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});