Ext.QuickTips.init();
Ext.define('erp.controller.excel.SingleForm', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil:Ext.create('erp.util.GridUtil'),
    views:[
         'excel.SingleForm','core.form.DyConditionField','excel.ExcelPanel','core.button.Save','core.button.Close'
    	
    	],
   init:function(){
      var me = this;
    	this.control({
    	 'erpSaveButton':{
             'click':function(btn){
              me.save(this);
          
             } 
    	 },
    	 'erpCloseButton':{
    	   'click':function(){   	   
    	      parent.Ext.getCmp('win').close();
    	   }
    	  
    	 }
    	});
    },
    getSeqId: function(form){
		if(!form){
			form = Ext.getCmp('form');
		}
		Ext.Ajax.request({
	   		url : basePath + form.getIdUrl,
	   		method : 'get',
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
	   				Ext.getCmp(form.keyField).setValue(rs.id);
	   			}
	   		}
		});
	},
     save:function(){
	}
});	  	