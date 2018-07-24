Ext.define('erp.view.pm.mould.ProductSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ProductSetViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/saveProductSet.action',
					deleteUrl: 'pm/mould/deleteProductSet.action',
					updateUrl: 'pm/mould/updateProductSet.action',
					auditUrl: 'pm/mould/auditProductSet.action',
					resAuditUrl: 'pm/mould/resAuditProductSet.action',
					submitUrl: 'pm/mould/submitProductSet.action',
					resSubmitUrl: 'pm/mould/resSubmitProductSet.action',
					bannedUrl: 'pm/mould/bannedProductSet.action',
					resBannedUrl: 'pm/mould/resBannedProductSet.action',
					getIdUrl: 'common/getId.action?seq=ProductSet_SEQ',
					keyField: 'ps_id',
					codeField: 'ps_code',
					statusField: 'ps_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'psd_detno',
					keyField: 'psd_id',
					mainField: 'psd_psid',
					allowExtraButtons: true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});