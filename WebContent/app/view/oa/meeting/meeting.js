Ext.define('erp.view.oa.meeting.meeting',{ 
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
					anchor: '100% 70%',
					saveUrl: 'oa/meeting/saveMeeting.action',
					deleteUrl: 'oa/meeting/deleteMeeting.action',
					updateUrl: 'oa/meeting/updateMeeting.action',
					auditUrl: 'oa/meeting/auditMeeting.action',
					resAuditUrl: 'oa/meeting/resAuditMeeting.action',
					submitUrl: 'oa/meeting/submitMeeting.action',
					resSubmitUrl: 'oa/meeting/resSubmitMeeting.action',
					getIdUrl: 'common/getId.action?seq=MEETING_SEQ',
					keyField: 'me_id',
					codeField: 'me_code',
					statusField: 'me_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'md_detno',
					necessaryField: 'md_participants',
					keyField: 'md_id',
					mainField: 'md_meid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});