Ext.define('erp.view.oa.vehicle.ZVehicleapply',{ 
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
					saveUrl: 'oa/vehicle/saveVehicleapply.action',
					deleteUrl: 'oa/vehicle/deleteVehicleapply.action',
					updateUrl: 'oa/vehicle/updateVehicleapply.action',
					auditUrl: 'oa/vehicle/auditVehicleapply.action',
					resAuditUrl: 'oa/vehicle/resAuditVehicleapply.action',
					submitUrl: 'oa/vehicle/submitVehicleapply.action',
					resSubmitUrl: 'oa/vehicle/resSubmitVehicleapply.action',
					getIdUrl: 'common/getId.action?seq=Vehicleapply_SEQ',
					keyField: 'va_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});