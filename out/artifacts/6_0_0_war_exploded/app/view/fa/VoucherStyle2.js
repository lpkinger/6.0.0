Ext.define('erp.view.fa.VoucherStyle2', { 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				anchor: '100% 40%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/vc/saveVoucherStyle.action?caller=' + caller,
				updateUrl: 'fa/vc/updateVoucherStyle.action?caller=' + caller,
				keyField:'vs_id',
				getIdUrl: 'common/getId.action?seq=VOUCHERSTYLE_SEQ',
			},{
				anchor: '100% 60%',
				xtype: 'erpGridPanel2'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});