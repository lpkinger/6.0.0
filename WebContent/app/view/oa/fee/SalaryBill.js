Ext.define('erp.view.oa.fee.SalaryBill',{ 
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
					anchor: '100% 45%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=SalaryBill_SEQ',
					auditUrl: 'oa/fee/auditSalaryBill.action',
					resAuditUrl: 'oa/fee/resAuditSalaryBill.action',
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					keyField: 'sb_id',
					codeField: 'sb_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 55%', 
					keyField: 'sbd_id',
					detno: 'sbd_detno',
					mainField: 'sbd_sbid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});