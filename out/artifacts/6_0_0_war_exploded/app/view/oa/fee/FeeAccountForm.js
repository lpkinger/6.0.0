Ext.define('erp.view.oa.fee.FeeAccountForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FeeAccount',
	id: 'form', 
	title: '关账及重开作业 ',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	layout: {
		type: 'vbox',
		align: 'center'
	},
	defaults:{
		labelWidth: 90,
		margin:'5 0 0 0'
	},
	items: [{		
		margin:'40 0 0 0',
		xtype: 'monthdatefield',
    	fieldLabel: '当前期间',
    	allowBlank: false,
    	readOnly:true,
    	id: 'date',
    	name: 'date'
	}],
	bbar: {
		cls:'singleWindowBar',
		items:['->',{
			xtype: 'erpStartAccountButton',
			height: 26
		},{
			xtype:'erpCloseButton',
			height: 26
		},'->']
	}
});