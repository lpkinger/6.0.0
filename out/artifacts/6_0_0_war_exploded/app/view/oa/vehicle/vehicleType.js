Ext.define('erp.view.oa.vehicle.vehicleType',{ 
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
					saveUrl: 'oa/vehicle/saveVehicleType.action',
					deleteUrl: 'oa/vehicle/deleteVehicleType.action',
					updateUrl: 'oa/vehicle/updateVehicleType.action',
					auditUrl: 'oa/vehicle/auditVehicleType.action',
					resAuditUrl: 'oa/vehicle/resAuditVehicleType.action',
					submitUrl: 'oa/vehicle/submitVehicleType.action',
					resSubmitUrl: 'oa/vehicle/resSubmitVehicleType.action',
					getIdUrl: 'common/getId.action?seq=VEHICLETYPE_SEQ',
					keyField: 'vt_id'
					//statusField: 'mr_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});