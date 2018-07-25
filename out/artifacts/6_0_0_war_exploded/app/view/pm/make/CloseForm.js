Ext.define('erp.view.pm.make.CloseForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.Close',
	id: 'form', 
	title: '制造单整批自动结案',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	layout: 'column',
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{		
    	xtype: 'condatefield',
    	fieldLabel: '日期',
    	allowBlank: false,
    	id: 'date',
    	name: 'date'
},{		   
	   	xtype: 'ftfindfield',
	   	fieldLabel: '制造单号',
    	id: 'ma_code',
    	name: 'ma_code',
    	labelAlign : "right",
    	allowBlank: true,
}],
buttons: [{
	xtype: 'erpConfirmButton'
},
{
	xtype: 'erpCloseButton'
}]
});