Ext.define('erp.view.fs.cust.RecBalanceAssign',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				updateUrl: 'fs/cust/updateRecBalanceAssign.action',
				deleteUrl: 'fs/cust/deleteRecBalanceAssign.action',
				submitUrl: 'fs/cust/submitRecBalanceAssign.action',
				resSubmitUrl: 'fs/cust/resSubmitRecBalanceAssign.action',
				auditUrl: 'fs/cust/auditRecBalanceAssign.action',
				keyField: 'ra_id',
				codeField: 'ra_code',
				statusField: 'ra_status',
				statuscodeField: 'ra_statuscode'
			},{
			 	anchor : '100% 70%',
				xtype : 'erpGridPanel2',
				keyField : 'rad_id',
				mainField : 'rad_raid'
			}]
		}); 
		this.callParent(arguments); 
	}
});