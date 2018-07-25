Ext.define('erp.view.oa.device.Device',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'DeviceViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'oa/device/saveDevice.action',
					deleteUrl: 'oa/device/deleteDevice.action',
					updateUrl: 'oa/device/updateDevice.action',
					getIdUrl: 'common/getId.action?seq=Device_SEQ',
					submitUrl: 'oa/device/submitDevice.action',
					auditUrl: 'oa/device/auditDevice.action',
					resAuditUrl: 'oa/device/resAuditDevice.action',			
					resSubmitUrl: 'oa/device/resSubmitDevice.action',
					keyField: 'de_id',
					codeField: 'de_code', 
					statusField: 'de_status',
					statuscodeField: 'de_statuscode'
				},{
					xtype:'tabpanel',
					id :'tab',
					anchor: '100% 50%',
					items:[{
						xtype : 'erpGridPanel2',
						detno : 'da_detno',
						keyField : 'de_id',
						id:'grid',
						title:'配置清单',
						caller:'Device',
						mainField : 'da_deid',
						allowExtraButtons : true,
						/*selModel: Ext.create('Ext.selection.CheckboxModel',{
							headerWidth: 0
						}),*/
						headerCt: Ext.create("Ext.grid.header.Container",{
					 	    forceFit: false,
					        sortable: true,
					        enableColumnMove:true,
					        enableColumnResize:true,
					        enableColumnHide: true
					     }),
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					        clicksToEdit: 1
					    }), 
					    Ext.create('erp.view.core.grid.HeaderFilter'), 
					    Ext.create('erp.view.core.plugin.CopyPasteMenu')]
					},{
						xtype : 'erpGridPanel2',
						id:'dc',
						keyField : 'dc_id',
						caller : 'DeviceResume',
						mainField : 'dc_deid',
						title:'设备履历',
						bbar:null,
						condition:condition!=null?condition.replace(/IS/g, "=").replace('da_deid','dc_deid')+' order by dc_id asc':''
					}]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});