Ext.define('erp.view.fa.gla.AutoPrePaidForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AutoPrePaid',
	id: 'form', 
	title: '自动生产摊销单据',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	layout : {
		type : 'vbox',
		align : 'center'
	},
	items: [{
		margin:'40 0 0 0',
    	xtype: 'monthdatefield',
    	fieldLabel: '日期',
    	allowBlank: false,
    	id: 'date',
    	name: 'date'
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});