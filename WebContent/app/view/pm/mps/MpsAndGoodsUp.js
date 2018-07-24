Ext.define('erp.view.pm.mps.MpsAndGoodsUp',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 45%',
				keyField: 'mm_id',
				codeField:'mm_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});