Ext.define('erp.view.oa.info.PagingDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype: 'erpPagingDetailFormPanel',
					saveUrl: 'oa/info/sendPagingRelease.action',
					deleteUrl: 'common/deleteCommon.action?caller=' +caller
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});