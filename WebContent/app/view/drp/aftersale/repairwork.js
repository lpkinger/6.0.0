Ext.define('erp.view.drp.aftersale.repairwork',{
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
					anchor: '100% 30%',
					saveUrl: 'drp/aftersale/saveRepairWork.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteRepairWork.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateRepairWork.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditRepairWork.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditRepairWork.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitRepairWork.action?caller=' +caller,
					printUrl: 'drp/aftersale/printRepairWork.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitRepairWork.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=REPAIRWORK_SEQ',
					keyField: 'rw_id',
					codeField: 'rw_code',
					statusField: 'rw_status',
					statuscodeField: 'rw_statuscode',
					v:me
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 40%',
                    necessaryField: '',
					keyField: 'rwd_id',
					detno: 'rwd_detno',
					mainField: 'rwd_rwid'
				},{
					xtype: 'erprepairworkDet',
					anchor: '100% 30%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});