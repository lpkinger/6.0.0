Ext.define('erp.view.scm.reserve.ProdIODetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				printUrl: 'scm/reserve/PrintBarDetail.action', 
				anchor: '100% 50%',
				keyField: 'pd_id',
				codeField: 'pd_inoutno'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'pdb_detno',
				necessaryField: 'pdb_qty',
				keyField: 'pdb_id',
				mainField: 'pdb_pdid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});