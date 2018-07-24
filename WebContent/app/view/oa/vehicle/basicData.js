Ext.define('erp.view.oa.vehicle.basicData',{ 
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
					saveUrl: 'oa/vehicle/saveBasicData.action',
					deleteUrl: 'oa/vehicle/deleteBasicData.action',
					updateUrl: 'oa/vehicle/updateBasicData.action',
					auditUrl: 'oa/vehicle/auditBasicData.action',
					resAuditUrl: 'oa/vehicle/resAuditBasicData.action',
					submitUrl: 'oa/vehicle/submitBasicData.action',
					resSubmitUrl: 'oa/vehicle/resSubmitBasicData.action',
					getIdUrl: 'common/getId.action?seq=BASICDATA_SEQ',
					keyField: 'bd_id',
					statusField: 'bd_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});