Ext.define('erp.view.ma.data.DataStore',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'ma/saveDataStore.action',
				deleteUrl: 'ma/saveDataStore.action',
				updateUrl: 'ma/updateDataStore.action',
				getIdUrl: 'common/getId.action?seq=DATASTORE_SEQ',
				keyField: 'ds_id'
			},{
				xtype: 'erpGridPanel2', 
				anchor: '100% 60%',
				necessaryField: 'dsd_field',
				keyField: 'dsd_id',
				mainField: 'dsd_mainid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
