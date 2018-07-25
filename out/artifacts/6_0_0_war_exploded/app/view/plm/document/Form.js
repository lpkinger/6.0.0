Ext.define('erp.view.plm.document.Form',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{   
	    	  xtype:'erpFormPanel',  
	    	  anchor: '100% 100%',
	    	  saveUrl: 'common/saveCommon.action?caller=' +caller,
			  getIdUrl: 'common/getCommonId.action?caller=' +caller
	    }]
		});
		me.callParent(arguments); 
	}
});