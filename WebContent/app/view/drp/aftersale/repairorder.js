Ext.define('erp.view.drp.aftersale.repairorder',{
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
					anchor: '100% 35%',
					saveUrl: 'drp/aftersale/saveRepairOrder.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteRepairOrder.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateRepairOrder.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditRepairOrder.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditRepairOrder.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitRepairOrder.action?caller=' +caller,
					printUrl: 'drp/aftersale/printRepairOrder.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitRepairOrder.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=REPAIRORDER_SEQ',
					keyField: 'ro_id',
					codeField: 'ro_code',
					statusField: 'ro_status',
					statuscodeField: 'ro_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'rod_id',
					detno: 'rod_detno',
					mainField: 'rod_roid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});