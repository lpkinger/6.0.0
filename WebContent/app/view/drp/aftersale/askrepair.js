Ext.define('erp.view.drp.aftersale.askrepair',{
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
					saveUrl: 'drp/aftersale/saveAskRepair.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteAskRepair.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateAskRepair.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditAskRepair.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditAskRepair.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitAskRepair.action?caller=' +caller,
					printUrl: 'drp/aftersale/printAskRepair.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitAskRepair.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=CUSTOMERREPAIR_SEQ',
					keyField: 'cr_id',
					codeField: 'cr_code',
					statusField: 'cr_status',
					statuscodeField: 'cr_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'crd_id',
					detno: 'crd_detno',
					mainField: 'crd_crid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});