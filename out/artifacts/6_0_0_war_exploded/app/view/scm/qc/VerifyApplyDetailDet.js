Ext.define('erp.view.scm.qc.VerifyApplyDetailDet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpGridPanel4',
				anchor: '100% 100%', 
				keyField: 'vadd_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});