Ext.define('erp.view.oa.vehicle.fuelDiary',{ 
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
					saveUrl: 'oa/vehicle/saveFuelDiary.action',
					deleteUrl: 'oa/vehicle/deleteFuelDiary.action',
					updateUrl: 'oa/vehicle/updateFuelDiary.action',
					auditUrl: 'oa/vehicle/auditFuelDiary.action',
					resAuditUrl: 'oa/vehicle/resAuditFuelDiary.action',
					submitUrl: 'oa/vehicle/submitFuelDiary.action',
					resSubmitUrl: 'oa/vehicle/resSubmitFuelDiary.action',
					getIdUrl: 'common/getId.action?seq=FUELDIARY_SEQ',
					keyField: 'fd_id'
					//statusField: 'mr_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});