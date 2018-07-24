Ext.define('erp.view.oa.device.DeviceInventory', {
	extend : 'Ext.Viewport',
	layout: 'fit', 
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				layout : 'anchor',
				items : [ {
					xtype : 'erpFormPanel',
					anchor : '100% 35%',
					saveUrl : 'oa/device/saveDeviceInventory.action?caller=' + caller,
					deleteUrl : 'oa/device/deleteDeviceInventory.action?caller=' + caller,
					updateUrl : 'oa/device/updateDeviceInventoryById.action?caller=' + caller,
					submitUrl: 'oa/device/submitDeviceInventory.action',
					auditUrl: 'oa/device/auditDeviceInventory.action',
					resAuditUrl: 'oa/device/resAuditDeviceInventory.action',		
					resSubmitUrl: 'oa/device/resSubmitDeviceInventory.action',
					getIdUrl : 'common/getId.action?seq=DeviceInventory_SEQ',
					keyField : 'db_id',
					statusField : 'db_status',
					statuscodeField : 'db_statuscode'
				}, {
					xtype : 'erpGridPanel2',
					anchor : '100% 65%',
					keyField : 'dc_id',
					mainField : 'dc_dbid',
					allowExtraButtons : true,
					selModel: Ext.create('Ext.selection.CheckboxModel',{
						headerWidth: 0
					}),
					headerCt: Ext.create("Ext.grid.header.Container",{
				 	    forceFit: false,
				        sortable: true,
				        enableColumnMove:true,
				        enableColumnResize:true,
				        enableColumnHide: true
				     }),
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.grid.HeaderFilter'), 
				    Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				} ]
			} ]
		});
		me.callParent(arguments);
	}
});