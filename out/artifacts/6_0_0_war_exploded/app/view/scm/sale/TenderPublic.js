Ext.define('erp.view.scm.sale.TenderPublic',{ 
	extend: 'Ext.Viewport', 
	layout:'anchor',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpTenderPublicFormPanel',
				anchor:'100% 45%'
			},{
				xtype:'erpTenderPublicGridPanel',
				anchor:'100% 55%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});