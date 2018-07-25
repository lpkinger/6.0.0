Ext.define('erp.view.oa.vehicle.vehicleStop',{ 
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
					saveUrl: 'oa/vehicle/saveVehicleStop.action',
					deleteUrl: 'oa/vehicle/deleteVehicleStop.action',
					updateUrl: 'oa/vehicle/updateVehicleStop.action',
					auditUrl: 'oa/vehicle/auditVehicleStop.action',
					resAuditUrl: 'oa/vehicle/resAuditVehicleStop.action',
					submitUrl: 'oa/vehicle/submitVehicleStop.action',
					resSubmitUrl: 'oa/vehicle/resSubmitVehicleStop.action',
					getIdUrl: 'common/getId.action?seq=VEHICLESTOP_SEQ',
					//keyField: 'vt_id'
					//statusField: 'mr_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});