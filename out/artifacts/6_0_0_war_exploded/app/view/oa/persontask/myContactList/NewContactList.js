Ext.define('erp.view.oa.persontask.myContactList.NewContactList',{ 
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
					saveUrl: 'oa/persontask/myContactList/saveContactList.action',
					getIdUrl: 'common/getId.action?seq=CONTACTLIST_SEQ',
					keyField: 'cl_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});