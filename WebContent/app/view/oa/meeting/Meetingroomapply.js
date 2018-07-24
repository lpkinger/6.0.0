Ext.define('erp.view.oa.meeting.Meetingroomapply',{ 
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
					anchor: '100% 65%',
					saveUrl: 'oa/meeting/saveMeetingroomapply.action',
					deleteUrl: 'oa/meeting/deleteMeetingroomapply.action',
					updateUrl: 'oa/meeting/updateMeetingroomapply.action',
					getIdUrl: 'common/getId.action?seq=Meetingroomapply_SEQ',
					auditUrl: 'oa/meeting/auditMeetingroomapply.action',
					resAuditUrl: 'oa/meeting/resAuditMeetingroomapply.action',
					submitUrl: 'oa/meeting/submitMeetingroomapply.action',
					resSubmitUrl: 'oa/meeting/resSubmitMeetingroomapply.action',
					printUrl: 'common/printCommon.action',
					keyField: 'ma_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%', 
					detno: 'md_detno',
					//necessaryField: 'md_participants',
					keyField: 'md_id',
					mainField: 'md_maid'
					/*xtype:'tabpanel',
					anchor: '100% 40%',
					items:[{
						title:'会议申请阶段',
						xtype: 'erpGridPanel2',
						anchor: '100% 30%', 
						detno: 'mad_detno',
						//necessaryField: 'md_participants',
						keyField: 'mad_id',
						mainField: 'mad_maid'
					},{
						//id: 'recordDetailDet',
						xtype: 'MeetingDetail', 
						title:'会议参与人',
						necessaryField: 'md_participants',
						//keyField: 'pl_id',
						//detno: 'pl_detno',
						//mainField: 'pl_vrid' 							
					}]*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});