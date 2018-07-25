Ext.define('erp.view.fa.bg.Budget',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'fa/bg/saveBudget.action',
				deleteUrl: 'fa/bg/deleteBudget.action',
				updateUrl: 'fa/bg/updateBudget.action',
				printUrl:'fa/bg/printBudget.action',
				getIdUrl: 'common/getId.action?seq=FaBudget_SEQ'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});