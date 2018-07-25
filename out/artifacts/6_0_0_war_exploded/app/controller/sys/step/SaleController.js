Ext.define('erp.controller.sys.step.SaleController', {
	extend: 'Ext.app.Controller',
	id:'SaleController',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['sys.sale.SalePortal','core.form.YnField'],
	init:function(){
		var me=this;
		this.control({
			'gridcolumn[dataIndex=ck_maxnum]':{
				beforerender:function(column){
					column.editor.minValue=150;
				}				
			}
		});
		var app=erp.getApplication();
		var saleportal = activeItem.child('saleportal');
		if(!saleportal){
			var saleportal =  Ext.widget('saleportal',{desc:'销售管理'});
			activeItem.add(saleportal);
			Ext.getCmp('syspanel').setTitle(saleportal.desc);
		}
	},
	changeInputValue:function(field,value){
		showResult('提示','修改成功!');
		field.originalValue=value;

	}
});