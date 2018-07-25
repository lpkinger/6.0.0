Ext.define('erp.view.oa.vehicle.Vehicleuse',{ 
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
					saveUrl: 'oa/vehicle/saveVehicleuse.action',
					deleteUrl: 'oa/vehicle/deleteVehicleuse.action',
					updateUrl: 'oa/vehicle/updateVehicleuse.action',
					/*auditUrl: 'oa/vehicle/auditVehicleuse.action',
					resAuditUrl: 'oa/vehicle/resAuditVehicleuse.action',
					submitUrl: 'oa/vehicle/submitVehicleuse.action',
					resSubmitUrl: 'oa/vehicle/resSubmitVehicleuse.action',*/
					getIdUrl: 'common/getId.action?seq=Vehicleuse_SEQ',
					keyField: 'vu_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});