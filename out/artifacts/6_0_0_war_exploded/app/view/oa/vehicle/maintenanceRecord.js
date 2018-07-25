Ext.define('erp.view.oa.vehicle.maintenanceRecord',{ 
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
					saveUrl: 'oa/vehicle/saveMaintenanceRecord.action',
					deleteUrl: 'oa/vehicle/deleteMaintenanceRecord.action',
					updateUrl: 'oa/vehicle/updateMaintenanceRecord.action',
					auditUrl: 'oa/vehicle/auditMaintenanceRecord.action',
					resAuditUrl: 'oa/vehicle/resAuditMaintenanceRecord.action',
					submitUrl: 'oa/vehicle/submitMaintenanceRecord.action',
					resSubmitUrl: 'oa/vehicle/resSubmitMaintenanceRecord.action',
					getIdUrl: 'common/getId.action?seq=MAINTENANCERECORD_SEQ',
					keyField: 'mr_id'
					//statusField: 'bd_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});