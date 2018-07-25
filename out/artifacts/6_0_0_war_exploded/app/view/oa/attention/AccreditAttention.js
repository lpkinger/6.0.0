Ext.define('erp.view.oa.attention.AccreditAttention',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpAccreditAttentionGridPanel',  
	    	  anchor: '100% 100%',
	             }] 
		});
		me.callParent(arguments); 
	}
});