Ext.define('erp.view.drp.aftersale.PartCheck',{
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
					saveUrl: 'drp/aftersale/savePartCheck.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deletePartCheck.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updatePartCheck.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditPartCheck.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditPartCheck.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitPartCheck.action?caller=' +caller,
					printUrl: 'drp/aftersale/printPartCheck.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitPartCheck.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PartCheck_SEQ',
					keyField: 'pc_id',
					codeField: 'pc_code',
					statusField: 'pc_status',
					statuscodeField: 'pc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'pcd_id',
					detno: 'pcd_detno',
					mainField: 'pcd_pcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});