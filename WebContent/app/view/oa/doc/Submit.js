Ext.define('erp.view.oa.doc.Submit',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpSubmitButton',
	cls:'x-btn-submit',
	formBind: true,
	style: {
		marginLeft: '10px'
	},
	text:null,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});	