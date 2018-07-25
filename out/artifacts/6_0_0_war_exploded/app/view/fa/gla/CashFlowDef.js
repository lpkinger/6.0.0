Ext.define('erp.view.fa.gla.CashFlowDef',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'CashFlowDefViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/gla/saveCashFlowDef.action',
					deleteUrl: 'fa/gla/deleteCashFlowDef.action',
					updateUrl: 'fa/gla/updateCashFlowDef.action',
					getIdUrl: 'common/getId.action?seq=CashFlowDef_SEQ',
					keyField: 'cfd_id',
					codeField: 'cfd_code'
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});