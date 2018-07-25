Ext.define('erp.view.fa.ars.CarriedForward',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'CarriedForwardView',
					confirmUrl:'fa/ars/confirmCarriedForward.action',
					
					xtype: 'CarriedForward',
					anchor: '100% 100%',					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});