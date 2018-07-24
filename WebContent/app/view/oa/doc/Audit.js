Ext.define('erp.view.oa.doc.Audit',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpAuditButton',
	cls:'x-btn-audit',
	style: {
		marginLeft: '10px'
	},
	text:null,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});	