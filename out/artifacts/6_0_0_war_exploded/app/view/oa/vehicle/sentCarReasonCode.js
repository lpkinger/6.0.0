Ext.define('erp.view.oa.vehicle.sentCarReasonCode',{ 
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
					anchor: '100% 100%',
					saveUrl: 'oa/vehicle/saveSentCarReasonCode.action',
					deleteUrl: 'oa/vehicle/deleteSentCarReasonCode.action',
					updateUrl: 'oa/vehicle/updateSentCarReasonCode.action',
					auditUrl: 'oa/vehicle/auditSentCarReasonCode.action',
					resAuditUrl: 'oa/vehicle/resAuditSentCarReasonCode.action',
					submitUrl: 'oa/vehicle/submitSentCarReasonCode.action',
					resSubmitUrl: 'oa/vehicle/resSubmitSentCarReasonCode.action',
					getIdUrl: 'common/getId.action?seq=SENTCARREASONCODE_SEQ',
					keyField: 'scr_id'
					//statusField: 'mr_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});