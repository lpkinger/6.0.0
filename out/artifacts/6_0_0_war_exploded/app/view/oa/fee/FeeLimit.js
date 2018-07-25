Ext.define('erp.view.oa.fee.FeeLimit',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'oa/fee/saveFeeLimit.action',
					deleteUrl: 'oa/fee/deleteFeeLimit.action',
					updateUrl: 'oa/fee/updateFeeLimit.action',
					getIdUrl: 'common/getId.action?seq=FeeLimit_SEQ',
					auditUrl: 'oa/fee/auditFeeLimit.action',
					resAuditUrl: 'oa/fee/resAuditFeeLimit.action',
					submitUrl: 'oa/fee/submitFeeLimit.action',
					resSubmitUrl: 'oa/fee/resSubmitFeeLimit.action',
					keyField: 'fl_id',
					codeField: 'fl_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					keyField: 'fld_id',
					detno: 'fld_detno',
					mainField: 'fld_flid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});