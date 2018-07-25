Ext.define('erp.view.oa.info.PagingRelease',{ 
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
					region: 'center',
					xtype: 'icqform',
					saveUrl: 'oa/info/sendPagingRelease.action',
					getIdUrl: 'common/getId.action?seq=PAGINGRELEASE_SEQ'
				}, {
					region: 'east',
					width: '20%',
					xtype: 'erpMailTreePanel',
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});