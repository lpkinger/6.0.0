Ext.define('erp.view.oa.doc.Save',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpSaveButton',
	id: 'save',
	cls:'x-btn-save',
	formBind: true,
	style: {
		marginLeft: '10px'
	},
	text:'保存',
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});	