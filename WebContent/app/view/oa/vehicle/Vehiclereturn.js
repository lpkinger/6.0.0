Ext.define('erp.view.oa.vehicle.Vehiclereturn',{ 
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
					anchor: '100% 40%',
					saveUrl: 'oa/vehicle/saveVehiclereturn.action',
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Vehiclereturn_SEQ',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					printUrl: 'oa/vehicle/printVehiclereturn.action',
					keyField: 'vr_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 									
					keyField: 'va_id',
					mainField: 'va_vrid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});