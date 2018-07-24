Ext.define('erp.view.oa.doc.Delete',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpDeleteButton',
	cls:'x-btn-delete',
	formBind: true,
	style: {
		marginLeft: '10px'
	},
	text:null,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});	