Ext.define('erp.view.fa.gla.VoucherDescription',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'VoucherDescriptionViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveVoucherDescription.action',
					deleteUrl: 'fa/ars/deleteVoucherDescription.action',
					updateUrl: 'fa/ars/updateVoucherDescription.action',
					auditUrl: 'fa/ars/auditVoucherDescription.action',
					resAuditUrl: 'fa/ars/resAuditVoucherDescription.action',
					submitUrl: 'fa/ars/submitVoucherDescription.action',
					resSubmitUrl: 'fa/ars/resSubmitVoucherDescription.action',
					getIdUrl: 'common/getId.action?seq=VoucherDescription_SEQ',
					keyField: 'cd_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});