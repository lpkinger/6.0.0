Ext.define('erp.view.oa.vehicle.EndVehiclereturn',{ 
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
					anchor: '100% 50%',
					saveUrl: 'oa/vehicle/saveVehiclereturn.action',
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Vehiclereturn_SEQ',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					confirmUrl:'oa/vehicle/confirmVehiclereturn.action',
					resConfirmUrl:'oa/vehicle/resConfirmVehiclereturn.action',
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'vr_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 									
					keyField: 'va_id',
					mainField: 'va_vrid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});