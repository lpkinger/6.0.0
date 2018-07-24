Ext.define('erp.view.fa.ars.AccountReceWorkForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AccountReceWork',
	id: 'form', 
	title: '应收汇兑损益作业',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{		
        	xtype: 'textfield',
        	fieldLabel: '',
        	allowBlank: false,
        	id: 'text',
        	name: 'text',
        	readOnly :true,
        	value:'应收汇兑损益作业'
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});