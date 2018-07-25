Ext.define('erp.view.scm.product.Offerprice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				getIdUrl: 'common/getId.action?seq=Offerprice_SEQ',
				keyField: 'op_id', 
				codeField: 'op_code',
				statusField: 'op_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});