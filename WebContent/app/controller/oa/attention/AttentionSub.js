Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AttentionSub', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
            'oa.attention.Form','core.form.ColorField','core.button.Save','core.button.Close',
    		'core.form.ScopeField','oa.attention.AttentionSubGrid','core.trigger.MultiDbfindTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
      var me=this;
    	this.control({ 
    	  'erpSaveButton':{
    	   click:function(btn){ 
    	      var form=btn.ownerCt.ownerCt.form;
    	      var grid=Ext.getCmp('AttentionSubGrid');
    	      var multiselected=grid.multiselected;
    	      if(multiselected.lenght<1){
    	       showError('未选中任何关注项!');
    	       return;
    	      }
		var params = new Object();
		if(form.isValid()){
			//form里面数据
			Ext.each(form.owner.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			var param=grid.GridUtil.getAllGridStore(grid);
	    Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(param.toString().replace(/\\/g,"%"));
		params.mutiselected=grid.getMultiSelected().data;
		params.caller=caller;
		console.log(grid.getMultiSelected());
		Ext.Ajax.request({
	   		url : basePath + 'oa/attention/saveAttentionSub.action',
	   		params : params,
	   		method : 'post',
	   		async: false,
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			 if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   					showError(str);
	   				}else saveSuccess(); 
	   		}
		   }); 
    	    }  
    	    }
    	    }  	  
    	});
    }     


});