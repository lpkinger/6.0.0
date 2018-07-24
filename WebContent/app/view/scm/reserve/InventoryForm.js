Ext.define('erp.view.scm.reserve.InventoryForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.Inventory',
	id: 'form', 
	title: '盘点作业',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	layout: {
		type: 'vbox',
		align: 'center'
	},
	defaults:{
		labelWidth: 90,
		margin:'5 0 0 0'
	},
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{
		margin:'35 0 0 0',
    	xtype: 'combo',
    	fieldLabel: '盘点方式',
    	allowBlank: false,
    	id: 'method',
    	name: 'method',
		queryMode: 'local',
		displayField: 'display',
		valueField: 'value',
		value:'全部物料盘点',
    	store: new Ext.data.Store({
    		fields: ['display', 'value'],
    		data: [
    	            {"display": '全部物料盘点', "value": '全部物料盘点'},
    	            {"display": '循环盘点', "value": '循环盘点'},
    	            {"display": 'ABC分类盘点', "value": 'ABC分类盘点'}
    	    ]
    	})
	},{
		xtype: 'multidbfindtrigger',
		fieldLabel: '仓库',
		height: 23,
		id: 'pr_whcode',
		name:'pr_whcode'
	}],
	bbar: {
		cls:'singleWindowBar',
		items:['->',{
			xtype: 'erpConfirmButton',
			height: 26
		},{
			xtype:'erpCloseButton',
			height: 26
		},'->']
	}
});