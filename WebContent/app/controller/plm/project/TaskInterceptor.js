Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.TaskInterceptor', {
	extend: 'Ext.app.Controller',
	requires: [ 'erp.util.GridUtil','erp.util.FormUtil'],
    GridUtil: Ext.create('erp.util.GridUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
	views:[
	       'plm.project.TaskPanel',
	       'core.trigger.DbfindTrigger',
	       'core.grid.YnColumn'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'button[id=save-btn]':{
	    			   click:function(btn){
	    		     		var grid = Ext.getCmp('taskPanel');
	    		    		var items = grid.selModel.getSelection(),arr=new Array();
	    		    		if(items.length>0){
	    		    			Ext.each(items, function(item, index){
		    		     			if(item.dirty){
		    		     				if(item.data["tt_name"]&&item.data["tt_sql"]&&item.data["tt_type"]){
		    		     					arr.push(Ext.JSON.encode(item.data));
		    		     					}
		    		     			}
		    		     		});
	    		    		}else{
	    		    			showMessage("提示","请先勾选数据!");	
	    		    			return;
	    		    		}
	    		     		if(arr.length>0){
	    		     			me.FormUtil.setLoading(true);
	    		     			Ext.Ajax.request({
	    		     				url:basePath+'plm/task/saveTaskInterceptor.action',
	    		     				params:{
	    		     					data:arr.toString()
	    		     				},
	    		     				method:'post',
	    		     				callback:function(opts,suc,res){
	    		     					me.FormUtil.setLoading(false);
	    		     					var msg= new Ext.decode(res.responseText);
	    		     					if(msg.exceptionInfo){
	    		     						showMessage("提示",msg.exceptionInfo);
	    		     						return;
	    		     					}
	    		     					if(msg.success){
	    		     						  showMessage("提示", "保存成功");
	    		     						  window.location.reload();
	    		     					}
	    		     				}
	    		     			});
	    		     		}
	    			   }
	    		   },
	    		   'button[id=delete-btn]':{
	    			   click:function(btn){
	    				  var grid=btn.ownerCt.ownerCt.down("erpTaskPanel");
	    				  var items = grid.selModel.getSelection();
	    				  if(items.length>0){
	    					var arr=new Array();
	    					Ext.each(items,function(item,index){
	    						if(item.data["tt_id"]){
	    							arr.push(item.data["tt_id"]);
	    						}
	    					});
	    					me.FormUtil.setLoading(true);
	    					Ext.Ajax.request({
	    	     				url:basePath+'plm/task/deleteTaskInterceptor.action',
	    	     				params:{
	    	     					data:arr.join()
	    	     				},
	    	     				method:'post',
	    	     				callback:function(opts,suc,res){
	    	     					me.FormUtil.setLoading(false);
	    	     					var msg= new Ext.decode(res.responseText);
	    	     					if(msg.exceptionInfo){
	    	     						showMessage("提示",msg.exceptionInfo);
	    	     						return;
	    	     					}
	    	     					if(msg.success){
	    	     						  showMessage("提示", "删除成功");
	    	     						  window.location.reload();
	    	     					}
	    	     				}
	    	     			});
	    				}
	    			   }
	    		   },
	    		   "button[id=checkRuleSql]":{
	    			   click:function(btn){
	    				   var grid = Ext.getCmp('taskPanel');
	    		    		var items = grid.selModel.getSelection(),arr=new Array();
	    		    		if(items.length>0){
	    		    			Ext.each(items, function(item, index){
	    		     				if(item.data["tt_id"]&&item.data["tt_checked"]==0){
	    		     					arr.push(item.data["tt_id"]);
	    		     					}
		    		     		});
	    		    		}else{
	    		    			showMessage("提示","请先勾选数据!");	
	    		    			return;
	    		    		}
	    		    		if (arr.length>0) {
	    		    			me.FormUtil.setLoading(true);
	    		     			Ext.Ajax.request({
	    		     				url:basePath+'plm/task/checkTaskInterceptor.action',
	    		     				params:{
	    		     					data:arr.join(",")
	    		     				},
	    		     				method:'post',
	    		     				callback:function(opts,suc,res){
	    		     					me.FormUtil.setLoading(false);
	    		     					var msg= new Ext.decode(res.responseText);
	    		     					if(msg.exceptionInfo){
	    		     						showMessage("提示",msg.exceptionInfo);
	    		     						return;
	    		     					}
	    		     					if(msg.success){
	    		     						  showMessage("提示", "已更新检测结果");
	    		     						  window.location.reload();
	    		     					}
	    		     				}
	    		     			});
							}
	    			   }
	    		   },
	    			'erpTaskPanel': { 
	        			itemclick: function(selModel, record){	
	    					this.onGridItemClick(selModel, record);	
	        			}
	        		},
	    	  });
	       },
	    onGridItemClick: function(selModel, record){//grid行选择
        	this.GridUtil.onGridItemClick(selModel, record);
        },   
});
