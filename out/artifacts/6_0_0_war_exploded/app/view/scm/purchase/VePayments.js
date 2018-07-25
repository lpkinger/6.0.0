Ext.define('erp.view.scm.purchase.VePayments',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 28%',
				saveUrl: 'scm/purchase/saveVePayments.action',
				deleteUrl: 'scm/purchase/deleteVePayments.action',
				updateUrl: 'scm/purchase/updateVePayments.action',
				getIdUrl: 'common/getId.action?seq=VEPAYMENTS_SEQ',
				keyField: 'vp_id',
				codeField: 'vp_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 72%', 
				detno: 'vpd_detno',
				keyField: 'vpd_id',
				mainField: 'vpd_vpid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});