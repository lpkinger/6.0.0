Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.updateHrOrg', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.updateHrOrg','core.form.Panel','core.button.Close',
    		'core.button.Update','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var em_id = Ext.getCmp('em_id').value;
    				var em_defaultorid = Ext.getCmp('em_defaultorid').value;
    				var em_defaultorname = Ext.getCmp('em_defaultorname').value;
    				Ext.Ajax.request({
    		        	url : basePath + 'hr/HrOrgStrTree/updateEmployee.action',
    		        	params: {
    		        		em_id:em_id,
    		        		hrOrgid:em_defaultorid,
    		        		hrOrgName:em_defaultorname
    		        	},
    		        	callback : function(options,success,response){
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.success){
    		        			alert("人员组织更新成功！");
    		        		} else if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);
    		        		}
    		        	}
    		        });
    			}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});