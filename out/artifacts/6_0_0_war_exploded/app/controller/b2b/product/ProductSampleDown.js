Ext.QuickTips.init();
Ext.define('erp.controller.b2b.product.ProductSampleDown', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),  
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','b2b.product.ProductSampleDown','core.button.Close','core.button.TurnSample','core.form.FileField',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.MonthDateField'
      	],
    init:function(){
    	var me = this;    	
    	this.control({    		 		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpTurnSample': {
    			click: function(btn){
					var form= Ext.getCmp('form');					
					Ext.Ajax.request({
				   		url : basePath + form.turnUrl,
				   		params: {
				   			id: Ext.getCmp('ps_id').value				   			
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){	
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;
				   				showError(str);
				   			}else{				   				
				   				window.location.href = basePath + "jsps/b2b/product/custSendSample.jsp?formCondition=ss_idIS" + localJson.id;
				   			}
				   		 }
					   
					});				
    			}
    		}
    	});
    },   
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});