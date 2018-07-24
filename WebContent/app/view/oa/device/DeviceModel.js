Ext.define('erp.view.oa.device.DeviceModel', {
	extend : 'Ext.Viewport',
	layout: 'fit', 
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				id:'deviceModelViewport', 
				layout: 'anchor', 
				items:[{
					xtype : 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl : 'oa/device/saveDeviceModel.action',
					deleteUrl : 'oa/device/deleteDeviceModel.action',
					updateUrl : 'oa/device/updateDeviceModelById.action',
					auditUrl : 'oa/device/auditDeviceModel.action',
					resAuditUrl : 'oa/device/resAuditDeviceModel.action',
					submitUrl : 'oa/device/submitDeviceModel.action',
					resSubmitUrl : 'oa/device/resSubmitDeviceModel.action',
					getIdUrl : 'common/getId.action?seq=DeviceModel_SEQ',
					keyField : 'dm_id',
					codeField : 'dm_code',
					statusField : 'dm_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'dmd_detno',
					keyField: 'dmd_id',
					mainField: 'dmd_dmid'
				}]
			} ]
		});
		me.callParent(arguments);
	}
});