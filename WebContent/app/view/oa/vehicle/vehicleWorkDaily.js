Ext.define('erp.view.oa.vehicle.vehicleWorkDaily',{ 
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
					saveUrl: 'oa/vehicle/saveVehicleWorkDaily.action',
					deleteUrl: 'oa/vehicle/deleteVehicleWorkDaily.action',
					updateUrl: 'oa/vehicle/updateVehicleWorkDaily.action',
					auditUrl: 'oa/vehicle/auditVehicleWorkDaily.action',
					resAuditUrl: 'oa/vehicle/resAuditVehicleWorkDaily.action',
					submitUrl: 'oa/vehicle/submitVehicleWorkDaily.action',
					resSubmitUrl: 'oa/vehicle/resSubmitVehicleWorkDaily.action',
					getIdUrl: 'common/getId.action?seq=VEHICLEWORKDAILY_SEQ',
					keyField: 'vwd_id'
					//statusField: 'mr_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});