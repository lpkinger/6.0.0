Ext.define('erp.view.oa.storage.Repairrecord',{ 
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
					deleteUrl: 'oa/storage/deleteRepairrecord.action',
					getIdUrl: 'common/getId.action?seq=Repairrecord_SEQ',
					keyField: 'rr_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});