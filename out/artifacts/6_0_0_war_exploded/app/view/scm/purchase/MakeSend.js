Ext.define('erp.view.scm.purchase.MakeSend',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/MakeSend.action',
				deleteUrl: 'scm/purchase/MakeSend.action',
				updateUrl: 'scm/purchase/MakeSend.action',
				getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
				keyField: 'ma_id',
				codeField: 'ma_code',
				statusField: 'ma_status'
			}]
		}); 
		me.callParent(arguments); 
	} 
});