Ext.define('erp.view.oa.fee.feePleaseFYBX',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 65%',
				saveUrl: 'oa/fee/saveFeePlease.action',
				deleteUrl: 'oa/fee/deleteFeePlease.action',
				updateUrl: 'oa/fee/updateFeePlease.action',
				auditUrl: 'oa/fee/auditFeePlease.action',
				printUrl: 'oa/fee/printFeePlease.action',
				confirmUrl:'oa/fee/confirmFeePlease.action',
				resAuditUrl: 'oa/fee/resAuditFeePlease.action',
				submitUrl: 'oa/fee/submitFeePlease.action',
				resSubmitUrl: 'oa/fee/resSubmitFeePlease.action',
				endUrl: 'oa/fee/endFeePlease.action',
				resEndUrl: 'oa/fee/resEndFeePlease.action',
				getIdUrl: 'common/getId.action?seq=FEEPLEASE_SEQ',
				keyField: 'fp_id',
				codeField: 'fp_code',
				statusField: 'fp_status',
				statuscodeField: 'fp_statuscode'
			},{
				xtype:'tabpanel',
				anchor: '100% 35%', 
				items:[{
					xtype: 'erpGridPanel2',
					detno: 'fpd_detno',
					keyField: 'fpd_id',
					mainField: 'fpd_fpid',
					allowExtraButtons: true,
					title:'费用报销明细'
				},{
					id: 'FeeBackGrid',
					title:'还款明细',
					_noc:1,
					xtype: 'FeeBackGrid',
				    keyField: 'fb_id',
				    mainField: 'fb_fpid',
					necessaryField: 'fb_jksqcode',
					detno: 'fb_detno'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});