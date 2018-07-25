Ext.define('erp.view.oa.storage.Propertyset',{ 
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
					saveUrl: 'oa/storage/savePropertyset.action',
					deleteUrl: 'oa/storage/deletePropertyset.action',
					updateUrl: 'oa/storage/updatePropertyset.action',
					getIdUrl: 'common/getId.action?seq=Propertyset_SEQ',
					keyField: 'ps_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});