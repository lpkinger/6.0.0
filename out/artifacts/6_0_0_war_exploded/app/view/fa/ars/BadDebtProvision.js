Ext.define('erp.view.fa.ars.BadDebtProvision',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					id:'BadDebtProvisionView',
					confirmUrl:'fa/ars/confirmBadDebtProvision.action',
					
					xtype: 'BadDebtProvision',
					anchor: '100% 100%',					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});