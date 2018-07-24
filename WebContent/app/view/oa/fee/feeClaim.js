Ext.define('erp.view.oa.fee.feeClaim',{ 
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
					saveUrl: 'oa/fee/saveFeeClaim.action',
					deleteUrl: 'oa/fee/deleteFeeClaim.action',
					updateUrl: 'oa/fee/updateFeeClaim.action',
					auditUrl: 'oa/fee/auditFeeClaim.action',
					resAuditUrl: 'oa/fee/resAuditFeeClaim.action',
					submitUrl: 'oa/fee/submitFeeClaim.action',
					resSubmitUrl: 'oa/fee/resSubmitFeeClaim.action',
					getIdUrl: 'common/getId.action?seq=FEECLAIM_SEQ',
					keyField: 'fc_id',
					codeField: 'fc_code',
					statusField: 'fc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'fcd_detno',
//					necessaryField: 'fcd_code',
					keyField: 'fcd_id',
					mainField: 'fcd_fcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});