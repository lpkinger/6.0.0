Ext.define('erp.controller.sys.step.PUController', {
	extend: 'Ext.app.Controller',
	id:'PUController',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['sys.pu.PuPortal','core.trigger.AddDbfindTrigger'],
	init:function(){
		var me=this;
		this.control({
		});
		var app=erp.getApplication();
		var puportal = activeItem.child('puportal');
		if(!puportal){
		 puportal =  Ext.widget('puportal',{desc:'采购管理'});
			activeItem.add(puportal);
			Ext.getCmp('syspanel').setTitle(puportal.desc);
		}
	}
});