Ext.define('erp.view.co.cost.StandardCostForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.StandardCost',
	id: 'form', 
	title: '标准成本计算作业',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
	},
	items: [{		
        	xtype: 'condatefield',
        	fieldLabel: '日期期间',
        	allowBlank: false,
        	id: 'date',
        	name: 'date'
	},
	{		
    	xtype: 'dbfindtrigger',
    	fieldLabel: '物料编号',
    	allowBlank: false,
    	id: 'pr_code',
    	name: 'pr_code'
	},
	{		
		xtype: 'textfield',
    	fieldLabel: '物料名称',
    	allowBlank: false,
    	id: 'pr_name',
    	name: 'pr_name'
    	
	},
	{		
		xtype: 'textfield',
    	fieldLabel: '规格',
    	allowBlank: false,
    	id: 'pr_spec',
    	name: 'pr_spec'
    	
	},
	{		
		xtype: 'textfield',
    	fieldLabel: '物料类型',
    	allowBlank: false,
    	id: 'pr_type',
    	name: 'pr_type'
    	
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});