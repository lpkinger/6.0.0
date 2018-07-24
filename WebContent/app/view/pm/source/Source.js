Ext.define('erp.view.pm.source.Source',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "SourceFormPanel",  
	    	anchor: '100% 20%',
	    },{
			region: 'center',  
			title:'来源明细',
			xtype:'panel',
			height:height,
			layout:'fit',
			anchor: '100% 80%',
			//外面加层panel 防止布局 gridheader 会影藏
			items:[{
				xtype: "SourceGridPanel",  
		    	anchor: '100% 100%'		    
			}]
	
			
	    }
	 /* {
	    	region: 'center',
	    	xtype: "SourceGridPanel",  
	    	anchor: '100% 75%'
	  }*/]
		});
		me.callParent(arguments); 
	}
});