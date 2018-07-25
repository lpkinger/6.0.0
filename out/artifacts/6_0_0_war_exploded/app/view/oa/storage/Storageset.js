Ext.define('erp.view.oa.storage.Storageset',{ 
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
					saveUrl: 'oa/storage/saveStorageset.action',
					deleteUrl: 'oa/storage/deleteStorageset.action',
					updateUrl: 'oa/storage/updateStorageset.action',
					getIdUrl: 'common/getId.action?seq=Storageset_SEQ',
					keyField: 'ss_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});