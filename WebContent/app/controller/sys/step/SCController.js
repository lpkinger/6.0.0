Ext.define('erp.controller.sys.step.SCController', {
	extend: 'Ext.app.Controller',
	id:'SCController',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['sys.sc.ScPortal'],
	init:function(){
		var me=this;
		this.control({

		});
		var app=erp.getApplication();
		var portal = activeItem.child('scportal');
		if(!portal){			
			portal =  Ext.widget('scportal',{desc:'库存管理'});
			activeItem.add(portal);
			Ext.getCmp('syspanel').setTitle(portal.desc);
		}
	}
});