Ext.define('erp.view.oa.storage.Propert',{ 
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
					saveUrl: 'oa/storage/savePropert.action',
					deleteUrl: 'oa/storage/deletePropert.action',
					updateUrl: 'oa/storage/updatePropert.action',
					getIdUrl: 'common/getId.action?seq=Propert_SEQ',
					keyField: 'pr_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});