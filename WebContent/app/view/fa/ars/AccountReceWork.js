Ext.define('erp.view.fa.ars.AccountReceWork',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'AccountReceWorkView',
					confirmUrl:'fa/ars/confirmAccountReceWork.action',
					
					xtype: 'AccountReceWork',
					anchor: '100% 100%',					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});