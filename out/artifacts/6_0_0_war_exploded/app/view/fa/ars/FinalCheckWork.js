Ext.define('erp.view.fa.ars.FinalCheckWork',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'FinalCheckWorkView',
					confirmUrl:'fa/ars/confirmFinalCheckWork.action',
					
					xtype: 'FinalCheckWork',
					anchor: '100% 100%',					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});