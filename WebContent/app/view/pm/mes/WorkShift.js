Ext.define('erp.view.pm.mes.WorkShift',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'WorkShiftViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveWorkShift.action',
					deleteUrl: 'pm/mes/deleteWorkShift.action',
					updateUrl: 'pm/mes/updateWorkShift.action',
					getIdUrl: 'common/getId.action?seq=WorkShift_SEQ',
					submitUrl: 'pm/mes/submitWorkShift.action',
					auditUrl: 'pm/mes/auditWorkShift.action',
					resAuditUrl: 'pm/mes/resAuditWorkShift.action',			
					resSubmitUrl: 'pm/mes/resSubmitWorkShift.action',
					keyField: 'ws_id',
					codeField: 'ws_code', 
					statusField: 'ws_status',
					statuscodeField: 'ws_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});