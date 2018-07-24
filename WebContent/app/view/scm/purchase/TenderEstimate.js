Ext.define('erp.view.scm.purchase.TenderEstimate',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpTenderEstimateFormPanel',
				anchor:'100% 35%'
			},{
				anchor:'100% 65%',
				id:'content',
				autoScroll:true,
				xtype:'panel',
				title:'投标信息',
				frame : true
			}]
		}); 
		me.callParent(arguments); 
	} 
});