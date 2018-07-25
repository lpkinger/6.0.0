Ext.define('erp.view.oa.persontask.workDaily.Query',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	enableTools : true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',         
				xtype: "erpBatchDealFormPanel",  
		    	anchor: '100% 25%',
		    },{
				region: 'center',         
				xtype: "erpBatchDealGridPanel",  
		    	anchor: '100% 75%',
		        selModel: Ext.create('Ext.selection.Model',{		        	
		        }),
		    }]
		}); 
		me.callParent(arguments); 
	} ,
 
});