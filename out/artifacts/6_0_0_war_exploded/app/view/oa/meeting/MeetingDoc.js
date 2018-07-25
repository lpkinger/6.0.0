Ext.define('erp.view.oa.meeting.MeetingDoc',{ 
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
					saveUrl: 'oa/meeting/saveMeetingDoc.action',
					deleteUrl: 'oa/meeting/deleteMeetingDoc.action',
					updateUrl: 'oa/meeting/updateMeetingDoc.action',
					auditUrl: 'oa/meeting/auditMeetingDoc.action',
					resAuditUrl: 'oa/meeting/resAuditMeetingDoc.action',
					submitUrl: 'oa/meeting/submitMeetingDoc.action',
					resSubmitUrl: 'oa/meeting/resSubmitMeetingDoc.action',
					getIdUrl: 'common/getId.action?seq=MEETINGDOC_SEQ',
					printUrl: 'common/printCommon.action',
					keyField: 'md_id',
					statusField: 'md_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});