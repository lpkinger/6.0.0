Ext.define('erp.view.co.cost.CostAccountForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.CostAccount',
	id: 'form', 
	title: ' 成本计算作业 ',
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