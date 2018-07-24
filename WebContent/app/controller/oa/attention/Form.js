Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.Form', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil:Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.attention.Form','core.form.Panel','core.trigger.MultiDbfindTrigger',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.FileField'
    	],
   init:function(){
      var me = this;
    	this.control({   	
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
		var me = this;
		var params = new Object();
		var form = Ext.getCmp('form');
		if(saveUrl!=null&&saveUrl!=""){
		form.saveUrl=saveUrl;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.getSeqId(form);
		}
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			var param=[];
	   Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(param.toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    			    saveSuccess(function(){	
    			             var grid=parent.Ext.getCmp('knowledgeGridPanel');
                             var tree=parent.Ext.getCmp('KnowledgeTree'); 
                             var findcondtion='';                   
                            if(grid){                    
                                  var keyField=grid.keyField
                                  if(!grid.condition){
                                   findcondtion='1=1';
                                    var gridParam = {caller:  caller, condition: findcondtion};
                                     grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
                                     var form=parent.Ext.getCmp('form');
                                  }else {
                                  findcondtion=grid.condition;
                                  var form=parent.Ext.getCmp('form');
                                     if(form){
                                        var kc= Ext.getCmp('kc_point');
                                        var why=Ext.getCmp('kc_why');
                                        var scantimes=parent.Ext.getCmp('kl_scantimes').getValue();
    	                                var rectimes=parent.Ext.getCmp('kl_recommonedtimes').getValue();
    	                                var commenttimes=parent.Ext.getCmp('kl_commenttimes').getValue();
    	                                var point=parent.Ext.getCmp('kl_point').getValue();
    	                                if(kc){
    	                                   parent.Ext.getCmp('knowledgedetails').setValue('知识阅读'+scantimes+'次     '+'知识推荐'+rectimes+'次    '+'知识评论'+(Number(commenttimes)+1)+'次     '+'知识分数'+(Number(point)+Number(kc.value))+'分');
    	                                }else if(why){
    	                                    parent.Ext.getCmp('knowledgedetails').setValue('知识阅读'+scantimes+'次     '+'知识推荐'+(Number(rectimes)+1)+'次    '+'知识评论'+commenttimes+'次     '+'知识分数'+point+'分');
    	                                    parent.Ext.getCmp('win').close();
    	                                    return;
    	                                }
                                     }
                                  var gridParam = {caller:  caller, condition: findcondtion};
                                  grid.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
                                  } 
                                  }else if(tree){             
                                   tree.getTreeRootNode();
                                        }
    			                parent.Ext.getCmp('win').close();					
							});//@i18n/i
    					
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   					showError(str);
	   				} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	}
     }
});	  	