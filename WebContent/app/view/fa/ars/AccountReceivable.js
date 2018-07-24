Ext.define('erp.view.fa.ars.AccountReceivable',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'AccountReceivableView',
					confirmUrl:'fa/ars/confirmAccountReceivable.action',
					
					xtype: 'AccountReceivable',
					anchor: '100% 100%',					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});