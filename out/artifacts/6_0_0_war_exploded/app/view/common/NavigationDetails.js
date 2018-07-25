Ext.define('erp.view.common.NavigationDetails',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'tabpanel',
				anchor: '100% 100%',
				tabPosition: 'top',
				layout:'fit',
				border:0,
				tabBar: {  
			        height: 25,     //tab bar高度  
			        defaults: {  
			            height: 23  //tab 里的title的高度  
			        }  
			    },
				items:[]
			}]
		}); 
		me.callParent(arguments); 
	} 
});