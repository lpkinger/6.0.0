Ext.define('erp.view.hr.attendance.SignCard',{ 
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
					anchor: '100% 57%',
					saveUrl: 'hr/attendance/saveSignCard.action',
					deleteUrl: 'hr/attendance/deleteSignCard.action',
					updateUrl: 'hr/attendance/updateSignCard.action',
					getIdUrl: 'common/getId.action?seq=Meetingroomapply_SEQ',
					auditUrl: 'hr/attendance/auditSignCard.action',
					resAuditUrl: 'hr/attendance/resAuditSignCard.action',
					submitUrl: '/hr/attendance/submitSignCard.action',
					resSubmitUrl: 'hr/attendance/resSubmitSignCard.action',
					endUrl: 'hr/attendance/endSignCard.action',
					resEndUrl: 'hr/attendance/resEndSignCard.action?caller=SignCard',
					keyField: 'sc_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 43%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});