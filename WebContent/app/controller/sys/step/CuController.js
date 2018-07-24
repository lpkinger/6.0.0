Ext.define('erp.controller.sys.step.CuController', {
	extend: 'Ext.app.Controller',
	id:'CuController',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['sys.sale.CurrencyForm','sys.sale.CurrencyPortal'],
	init:function(){
		/*var me=this;*/
		var me=this;
		this.control({});
		var app=erp.getApplication();
		var cuportal = activeItem.child('currencyportal');
		if(!cuportal){
			var cuportal =  Ext.widget('currencyportal',{desc:'基础资料(币别)'});
			activeItem.add(cuportal);
		}
		/*var app=erp.getApplication();
		var puportal = activeItem.child('puportal');
		if(!puportal){
		 puportal =  Ext.widget('puportal',{desc:'采购管理'});
			activeItem.add(puportal);
			Ext.getCmp('syspanel').setTitle(puportal.desc);
		}*/
	}
});