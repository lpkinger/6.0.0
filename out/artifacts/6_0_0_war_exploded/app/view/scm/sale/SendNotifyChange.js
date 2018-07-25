Ext.define('erp.view.scm.sale.SendNotifyChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/sale/saveSendNotifyChange.action',
				deleteUrl: 'scm/sale/deleteSendNotifyChange.action',
				updateUrl: 'scm/sale/updateSendNotifyChange.action',
				auditUrl: 'scm/sale/auditSendNotifyChange.action',
				printUrl: 'scm/sale/printSendNotifyChange.action',
				resAuditUrl: 'scm/sale/resAuditSendNotifyChange.action',
				submitUrl: 'scm/sale/submitSendNotifyChange.action',
				resSubmitUrl: 'scm/sale/resSubmitSendNotifyChange.action',
				getIdUrl: 'common/getId.action?seq=SendNotifyChange_SEQ',
				keyField: 'sc_id',
				codeField: 'sc_code',
				statusField: 'sc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'scd_detno',
				necessaryField: 'scd_snddetno',
				keyField: 'scd_id',
				mainField: 'scd_scid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});