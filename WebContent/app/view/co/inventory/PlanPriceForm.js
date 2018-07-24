Ext.define('erp.view.co.inventory.PlanPriceForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.PlanPrice',
	id: 'form', 
	title: '物料计划单价批量更新',
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
        	fieldLabel: '日期',
        	allowBlank: false,
        	id: 'date',
        	name: 'date'
	},
	{		
    	xtype: 'ftfindfield',
    	fieldLabel: '物料范围',
    	allowBlank: false,
    	id: 'product',
    	name: 'product'
	},
	{		
    	xtype: 'combobox',
    	fieldLabel: '更新规则',
    	allowBlank: false,
    	id: 'rule',
    	name: 'rule',
    	store : Ext.create('Ext.data.Store', {
            fields: ['display', 'value'],
            data : [
                {"display": '最新单价', "value": '最新单价'},
                {"display": '平均单价', "value": '平均单价'}
            ]
        })
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});