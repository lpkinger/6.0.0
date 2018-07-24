Ext.define('erp.view.oa.vehicle.sentCarApplication',{ 
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
					saveUrl: 'oa/vehicle/saveSentCarApplication.action',
					deleteUrl: 'oa/vehicle/deleteSentCarApplication.action',
					updateUrl: 'oa/vehicle/updateSentCarApplication.action',
					auditUrl: 'oa/vehicle/auditSentCarApplication.action',
					resAuditUrl: 'oa/vehicle/resAuditSentCarApplication.action',
					submitUrl: 'oa/vehicle/submitSentCarApplication.action',
					resSubmitUrl: 'oa/vehicle/resSubmitSentCarApplication.action',
					getIdUrl: 'common/getId.action?seq=SENTCARAPPLICATION_SEQ',
					keyField: 'sca_id',
					statusField: 'sca_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});