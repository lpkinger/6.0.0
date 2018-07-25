Ext.define('erp.controller.sys.step.JpController', {
	extend: 'Ext.app.Controller',
	id:'JpController',
	views:['sys.hr.JpPortal','sys.hr.JpPanel','sys.hr.JprocessTab','sys.hr.SimpleJprocess','sys.hr.SimpleJpForm','sys.hr.SimpleJprocessPanel'],
	init:function(){
		var me=this;
		this.control({});
		var app=erp.getApplication();
		var jpportal = activeItem.child('jpportal');
		if(!jpportal){
			var jpportal =  Ext.widget('jpportal',{desc:'审批流'});
			activeItem.add(jpportal);
		}
	}
});