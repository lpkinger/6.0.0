Ext.define('erp.view.hr.wage.wageStandard',{
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
					saveUrl: 'hr/wage/saveWageStandard.action?caller=' +caller,
					deleteUrl: 'hr/wage/deleteWageStandard.action?caller=' +caller,
					updateUrl: 'hr/wage/updateWageStandard.action?caller=' +caller,
					auditUrl: 'hr/wage/auditWageStandard.action?caller=' +caller,
					resAuditUrl: 'hr/wage/resAuditWageStandard.action?caller=' +caller,
					submitUrl: 'hr/wage/submitWageStandard.action?caller=' +caller,
					printUrl: 'hr/wage/printWageStandard.action?caller=' +caller,
					resSubmitUrl: 'hr/wage/resSubmitWageStandard.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=WAGESTANDARD_SEQ',
					keyField: 'ws_id',
                    codeField: 'ws_code',
					statusField: 'ws_status',
					statuscodeField: 'ws_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'wsd_id',
					detno: 'wsd_detno',
					mainField: 'wsd_wsid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});