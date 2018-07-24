Ext.define('erp.view.oa.check.Attendapply',{ 
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
					saveUrl: 'oa/check/saveAttendapply.action',
					deleteUrl: 'oa/check/deleteAttendapply.action',
					updateUrl: 'oa/check/updateAttendapply.action',
					auditUrl: 'oa/check/auditAttendapply.action',
					resAuditUrl: 'oa/check/resAuditAttendapply.action',
					submitUrl: 'oa/check/submitAttendapply.action',
					resSubmitUrl: 'oa/check/resSubmitAttendapply.action',
					getIdUrl: 'common/getId.action?seq=Attendapply_SEQ',
					keyField: 'aa_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});