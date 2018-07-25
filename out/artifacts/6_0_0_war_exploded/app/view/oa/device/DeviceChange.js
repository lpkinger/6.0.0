Ext.define('erp.view.oa.device.DeviceChange', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				region : 'center',
				autoScroll : true,
				saveUrl: 'oa/device/saveDeviceChange.action?caller=' +caller,
				deleteUrl: 'oa/device/deleteDeviceChange.action?caller=' +caller,
				updateUrl: 'oa/device/updateDeviceChangeById.action?caller=' +caller,
				auditUrl: 'oa/device/auditDeviceChange.action?caller=' +caller,
				resAuditUrl: 'oa/device/resAuditDeviceChange.action?caller=' +caller,
				submitUrl: 'oa/device/submitDeviceChange.action?caller=' +caller,
				resSubmitUrl: 'oa/device/resSubmitDeviceChange.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=DeviceChange_SEQ',
				keyField: 'dc_id',
				codeField: 'dc_code',
				statusField: 'dc_status',
				statuscodeField: 'dc_statuscode',
			} ]
		});
		me.callParent(arguments);
	}
});