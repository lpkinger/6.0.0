Ext.define('erp.view.oa.meeting.MeetingDocTemp',{ 
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
					anchor: '100% 100%',
					saveUrl: 'oa/meeting/saveMeetingDocTemp.action',
					deleteUrl: 'oa/meeting/deleteMeetingDocTemp.action',
					updateUrl: 'oa/meeting/updateMeetingDocTemp.action',
					getIdUrl: 'common/getId.action?seq=MeetingDocTemp_SEQ',
					auditUrl: 'oa/meeting/auditMeetingDocTemp.action',
					resAuditUrl: 'oa/meeting/resAuditMeetingDocTemp.action',
					submitUrl: 'oa/meeting/submitMeetingDocTemp.action',
					resSubmitUrl: 'oa/meeting/resSubmitMeetingDocTemp.action',
					keyField: 'mt_id',
					codeField: 'mt_code',
					minMode:true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});