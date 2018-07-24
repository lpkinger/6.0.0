Ext.define('erp.controller.sys.step.HrController', {
	extend: 'Ext.app.Controller',
	id:'HrController',
	views:['sys.hr.HrPortal','sys.plugin.Spotlight','sys.hr.OrgTreePanel','sys.hr.EmployeeGrid','sys.hr.JprocessGrid','sys.hr.StandardJobGrid','sys.hr.PowerJobGrid'],
	init:function(){
		var me=this;
		this.control({});
		var app=erp.getApplication();
		var hrportal = activeItem.child('hrportal');
		if(!hrportal){
			var hrportal =  Ext.widget('hrportal',{desc:'组织人员'});
			activeItem.add(hrportal);
		}
	}
});