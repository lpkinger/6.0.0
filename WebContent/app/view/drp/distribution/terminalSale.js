Ext.define('erp.view.drp.distribution.terminalSale',{
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
					saveUrl: 'drp/aftersale/saveTerminalSale.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteTerminalSale.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateTerminalSale.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditTerminalSale.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditTerminalSale.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitTerminalSale.action?caller=' +caller,
					printUrl: 'drp/aftersale/printTerminalSale.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitTerminalSale.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=TERMINALSALE_SEQ',
					keyField: 'ts_id',
					codeField: 'ts_code',
					statusField: 'ts_status',
                    statuscodeField: 'ts_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'tsd_id',
					mainField: 'tsd_tsid' ,
                    detno : 'tsd_detno'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});