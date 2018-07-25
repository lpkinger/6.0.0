Ext.define('erp.view.oa.device.DeviceKind', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				layout : 'anchor',
				items : [ {
					xtype : 'erpFormPanel',
					anchor : '100% 35%',
					saveUrl : 'oa/device/saveDeviceKind.action?caller=' + caller,
					deleteUrl : 'oa/device/deleteDeviceKind.action?caller=' + caller,
					updateUrl : 'oa/device/updateDeviceKindById.action?caller=' + caller,
					getIdUrl : 'common/getId.action?seq=DeviceKind_SEQ',
					keyField : 'mk_id',
				}, {
					xtype : 'erpGridPanel2',
					anchor : '100% 65%',
					/*detno : 'mm_detno',*/
					keyField : 'dka_id',
					mainField : 'dka_dkid',
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