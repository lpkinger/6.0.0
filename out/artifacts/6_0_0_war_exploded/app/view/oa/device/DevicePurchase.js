Ext.define('erp.view.oa.device.DevicePurchase', {
	extend : 'Ext.Viewport',
	layout: 'fit', 
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				id:'devicePurchaseViewport', 
				layout: 'anchor', 
				items:[{
					xtype : 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl : 'oa/device/saveDevicePurchase.action',
					deleteUrl : 'oa/device/deleteDevicePurchase.action',
					updateUrl : 'oa/device/updateDevicePurchaseById.action',
					auditUrl : 'oa/device/auditDevicePurchase.action',
					resAuditUrl : 'oa/device/resAuditDevicePurchase.action',
					submitUrl : 'oa/device/submitDevicePurchase.action',
					resSubmitUrl : 'oa/device/resSubmitDevicePurchase.action',
					getIdUrl : 'common/getId.action?seq=DEVICEPURCHASE_SEQ',
					keyField : 'dp_id',
					codeField : 'dp_code',
					statusField : 'dp_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'dpd_detno',
					keyField: 'dpd_id',
					mainField: 'dpd_dpid'
				}]
			} ]
		});
		me.callParent(arguments);
	}
});