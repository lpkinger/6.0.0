Ext.define('erp.view.oa.fee.PreFeePlease',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'oa/fee/savePreFeePlease.action',
					deleteUrl: 'oa/fee/deletePreFeePlease.action',
					updateUrl: 'oa/fee/updatePreFeePlease.action',
					auditUrl: 'oa/fee/auditPreFeePlease.action',
					resAuditUrl: 'oa/fee/resAuditPreFeePlease.action',
					submitUrl: 'oa/fee/submitPreFeePlease.action',
					resSubmitUrl: 'oa/fee/resSubmitPreFeePlease.action',
					getIdUrl: 'common/getId.action?seq=PreFeePlease_SEQ',
					keyField: 'fp_id',
					codeField: 'fp_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'fpd_detno',
//					necessaryField: 'fcd_code',
					keyField: 'fpd_id',
					mainField: 'fpd_fpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});