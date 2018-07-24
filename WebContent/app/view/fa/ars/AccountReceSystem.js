Ext.define('erp.view.fa.ars.AccountReceSystem',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'AccountReceSystemView',
					confirmUrl:'fa/ars/confirmAccountReceSystem.action',
					
					xtype: 'AccountReceSystem',
					anchor: '100% 100%',					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});