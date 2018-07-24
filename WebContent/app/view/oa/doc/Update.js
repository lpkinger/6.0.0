Ext.define('erp.view.oa.doc.Update',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpUpdateButton',
	id: 'update',
	cls:'x-btn-update',
	style: {
		marginLeft: '10px'
	},
	text:'更新',
	formBind: true,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});	