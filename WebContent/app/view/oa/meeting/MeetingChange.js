Ext.define('erp.view.oa.meeting.MeetingChange',{ 
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
					saveUrl: 'oa/meeting/saveMeetingChange.action',
					deleteUrl: 'oa/meeting/deleteMeetingChange.action',
					updateUrl: 'oa/meeting/updateMeetingChange.action',
					getIdUrl: 'common/getId.action?seq=MeetingChange_SEQ',
					auditUrl: 'oa/meeting/auditMeetingChange.action',
					resAuditUrl: 'oa/meeting/resAuditMeetingChange.action',
					submitUrl: 'oa/meeting/submitMeetingChange.action',
					resSubmitUrl: 'oa/meeting/resSubmitMeetingChange.action',
					keyField: 'mc_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});