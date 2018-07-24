Ext.define('erp.view.scm.purchase.MakeMaterial',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				keyField: 'ma_id',
				codeField: 'macode',
				statusField: 'ma_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'mm_detno',
				necessaryField: 'mm_prodcode',
				keyField: 'mm_id',
				mainField: 'mm_maid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});