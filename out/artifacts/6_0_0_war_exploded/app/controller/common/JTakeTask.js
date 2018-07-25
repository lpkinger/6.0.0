Ext.QuickTips.init();
Ext.define('erp.controller.common.JTakeTask', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.JProcess.JTakeTask','core.button.Add',
    		'core.button.Save','core.button.Close','core.button.Delete',
    		
    	],
    init:function(){
    	var me = this;
    	/*formCondition = this.BaseUtil.getUrlParam('formCondition');
    	console.log(formCondition);*/
    	
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.saveEmployee(btn);
    			}
    		},
    		
    		
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addEmployee', '新增员工', 'jsps/hr/employee/employee.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    		
    			}
    		}
    	});
    }
    
                                    
});