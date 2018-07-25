Ext.define('erp.view.fa.fix.InsuBill',{ 
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
					anchor: '100% 50%',
					saveUrl: 'fa/fix/saveInsuBill.action?caller=' +caller,
					deleteUrl: 'fa/fix/deleteInsuBill.action?caller=' +caller,
					updateUrl: 'fa/fix/updateInsuBill.action?caller=' +caller,
					auditUrl: 'fa/fix/auditInsuBill.action?caller=' +caller,
					resAuditUrl: 'fa/fix/resAuditInsuBill.action?caller=' +caller,
					submitUrl: 'fa/fix/submitInsuBill.action?caller=' +caller,
					resSubmitUrl: 'fa/fix/resSubmitInsuBill.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=InsuBill_SEQ',
					keyField: 'ib_id',
					codeField: 'ib_code',
					statusField: 'ib_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ibd_detno',
					necessaryField: 'ibd_object',
					keyField: 'ibd_id',
					mainField: 'ibd_ibid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});