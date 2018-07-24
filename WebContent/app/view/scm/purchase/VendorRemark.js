Ext.define('erp.view.scm.purchase.VendorRemark',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				saveUrl: 'scm/purchase/saveVendorRemark.action',
				deleteUrl: 'scm/purchase/deleteVendorRemark.action',
				updateUrl: 'scm/purchase/updateVendorRemark.action',
				bannedUrl: 'scm/purchase/bannedVendorRemark.action',
				resBannedUrl: 'scm/purchase/resBannedVendorRemark.action',
				getIdUrl: 'common/getId.action?seq=VENDORREMARK_SEQ',
				keyField: 'vr_id',
				codeField: 'vr_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'vrd_detno',
				keyField: 'vrd_id',
				mainField: 'vrd_vrid',
				necessaryField: 'vrd_remark'	
			}]
		}); 
		me.callParent(arguments); 
	} 
});