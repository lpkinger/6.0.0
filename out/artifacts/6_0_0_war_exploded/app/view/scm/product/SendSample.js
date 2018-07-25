Ext.define('erp.view.scm.product.SendSample',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				getIdUrl: 'common/getId.action?seq=SendSample_SEQ',
				saveUrl:'scm/product/saveSendSample.action',
				keyField: 'ss_id', 
				codeField: 'ss_code',
				statusField: 'ss_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});