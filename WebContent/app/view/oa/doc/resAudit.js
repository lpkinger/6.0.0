Ext.define('erp.view.oa.doc.resAudit',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpResAuditButton',
	cls:'x-btn-resAudit',
	formBind: true,
	style: {
		marginLeft: '10px'
	},
	text:null,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});	