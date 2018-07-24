Ext.QuickTips.init();
Ext.define('erp.controller.b2b.product.CuProductSample', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','b2b.product.CuProductSample','core.button.TurnSample',
  			'core.button.Close'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
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
				   		url : basePath + form.turnSampleUrl,
				   		params: {
				   			id: Ext.getCmp('cps_id').value				   			
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){	
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;
				   				showError(str);
				   			}else{				   				
				   				window.location.href = basePath + "jsps/b2b/product/SendSample.jsp?formCondition=ss_idIS" + localJson.id;
				   			}
				   		 }
					   
					});				
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
});