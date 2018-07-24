Ext.define('erp.view.WisdomPark.NewsType',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'wisdomPark/newsCenter/deleteNewsType.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=NEWSTYPE_SEQ'
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});