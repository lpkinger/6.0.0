Ext.define('erp.view.pm.make.Dispatch',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/make/saveDispatch.action',
					deleteUrl: 'pm/make/deleteDispatch.action',
					updateUrl: 'pm/make/updateDispatch.action',
					auditUrl: 'pm/make/auditDispatch.action',
					resAuditUrl: 'pm/make/resAuditDispatch.action',
					submitUrl: 'pm/make/submitDispatch.action',
					resSubmitUrl: 'pm/make/resSubmitDispatch.action',
					getIdUrl: 'common/getId.action?seq=DISPATCH_SEQ',
					keyField: 'di_id',
					codeField: 'di_code',
					statusField: 'di_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'did_detno',
					necessaryField: 'did_prodcode',
					keyField: 'did_id',
					mainField: 'did_diid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});