Ext.define('erp.view.oa.vehicle.Vehiclearchives',{ 
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
					saveUrl: 'oa/vehicle/saveVehiclearchives.action',
					deleteUrl: 'oa/vehicle/deleteVehiclearchives.action',
					updateUrl: 'oa/vehicle/updateVehiclearchives.action',
					auditUrl: 'oa/vehicle/auditVehiclearchives.action',					
					resAuditUrl: 'oa/vehicle/resAuditVehiclearchives.action',
					submitUrl: 'oa/vehicle/submitVehiclearchives.action',
					resSubmitUrl: 'oa/vehicle/resSubmitVehiclearchives.action',
					getIdUrl: 'common/getId.action?seq=Vehiclearchives_SEQ',
					keyField: 'va_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});