Ext.define('erp.view.scm.reserve.SplitProdIODetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				saveUrl: 'scm/reserve/splitProdIODetail.action', 
				anchor: '100% 40%',
				keyField: 'pd_id',
				codeField: 'pd_inoutno'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'pd_pdno',
				keyField: 'pd_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});