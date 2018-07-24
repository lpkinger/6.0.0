Ext.define('erp.view.fs.loaded.LoadedPlanSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'fs/loaded/saveLoadedPlanSet.action',
				updateUrl: 'fs/loaded/updateLoadedPlanSet.action',
				deleteUrl: 'fs/loaded/deleteLoadedPlanSet.action',
				getIdUrl: 'common/getId.action?seq=FSLOADEDPLANSET_SEQ',
				keyField: 'ps_id'
			},{
				xtype : 'erpGridPanel2',
				anchor: '100% 75%', 
				keyField : 'psd_id',
				mainField : 'psd_liid'
			}]
		}); 
		this.callParent(arguments); 
	}
});