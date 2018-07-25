Ext.define('erp.view.drp.aftersale.repairreserve',{
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
					saveUrl: 'drp/aftersale/saveRepairreserve.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteRepairreserve.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateRepairreserve.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditRepairreserve.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditRepairreserve.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitRepairreserve.action?caller=' +caller,
					printUrl: 'drp/aftersale/printRepairreserve.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitRepairreserve.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
					keyField: 'ma_id',
					codeField: 'ma_code',
					statusField: 'ma_status',
					statuscodeField: 'ma_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'mm_id',
					detno: '',
					mainField: 'mm_maid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});