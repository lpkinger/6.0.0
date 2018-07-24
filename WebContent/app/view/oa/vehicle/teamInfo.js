Ext.define('erp.view.oa.vehicle.teamInfo',{ 
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
					saveUrl: 'oa/vehicle/saveTeamInfo.action',
					deleteUrl: 'oa/vehicle/deleteTeamInfo.action',
					updateUrl: 'oa/vehicle/updateTeamInfo.action',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=TEAMINFO_SEQ',
					keyField: 'ti_id'
					//statusField: 'mr_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});