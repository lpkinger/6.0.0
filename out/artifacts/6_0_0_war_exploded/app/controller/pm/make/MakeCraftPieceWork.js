Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeCraftPieceWork', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.button.Add',
      		'core.button.Save','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.trigger.MultiDbfindTrigger2',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.CleanDetail','core.button.LoadPeople'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpLoadPeopleButton':{
    			click:function(){
    				 var grid = Ext.getCmp('grid');
    				 Ext.Ajax.request({
    			   		url :basePath+ 'pm/make/loadPeopleMakeCraftPieceWork.action',
    			   		params: {
    			   			makecode:Ext.getCmp('ma_code').value,
    			   			prodcode:Ext.getCmp('ma_prodcode').value
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){ 
    			   			var localJson = new Ext.decode(response.responseText);
    			   			var id = Ext.getCmp('ma_id').value;
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}else{
							    grid.GridUtil.loadNewStore(grid, {
                                    caller: caller,
                                    condition: "mcp_maid="+id
                                });
						    }
    			   		}
    				});	
    			}
    		},
    		'erpUpdateButton':{
    			click:function(){    
    			//	me.checkOrder(Ext.getCmp('grid'));
    				this.FormUtil.onUpdate(this);    							
    			}
    		},
    		'erpCleanDetailButton':{
    			click:function(){   	
    				var grid=Ext.getCmp('grid'); 
    				grid.setLoading(true);
    				 Ext.Ajax.request({
    			   		url :basePath+ 'pm/make/deleteMakeCraftPieceWork.action',
    			   		params: {
    			   			id:Ext.getCmp('ma_id').value
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){ 
    			   			grid.setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    		    			if(localJson.success){
    		    				showError("删除成功");
    		    				window.location.reload();
    			   			} else if(localJson.exceptionInfo){   			   			
    								showError(localJson.exceptionInfo);return;   			   		
    		        		}
    			   		}
    				});
    			}
    		},
    		'erpGridPanel2':{
    			itemclick:function(selMod,record){
    				//点击最后一行添加空数据
    				var grid = Ext.getCmp('grid');
    				var index = grid.store.indexOf(record);
    				if(index == grid.store.indexOf(grid.store.last())){
    					this.GridUtil.add10EmptyItems(grid);
    		    	}
    			},
    			afterrender:function(){
    				var grid = Ext.getCmp('grid');
    				var btn = grid.down('erpDeleteDetailButton');
    				btn.setDisabled(false);
    			}
    		}
    	});
    },
    checkOrder:function(grid){
    	//检查执行顺序不能重复，通过双重循环判断
    	var data=grid.getStore().data;
    	var length=data.length;
    	if(length>1){
    		var stepcode="";
    		for(var i=0;i<length;i++){
    			if(data.items[i].data.mcp_stepcode!=""){
    				var stepno=data.items[i].data.mcp_stepno;
            		for(var j=i+1;j<length;j++){
            			var stepno1=data.items[j].data.mcp_stepno;
            			if(stepno==stepno1){
            				stepcode+="请检查"+data.items[i].data.mcp_stepcode+'和'+data.items[i].data.mcp_stepcode+'的执行顺序<br>';
            			}
            		}
    			}
        	}
    		if(stepcode!=""){
    			showError(stepcode);
    			return;
    		}
    	}
    }
});