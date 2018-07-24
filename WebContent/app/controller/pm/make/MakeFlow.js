Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeFlow', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','pm.make.MakeFlow','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.button.Print','core.button.MakeFlows'
	],
    init:function(){
    	var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){    				
    				grid.setReadOnly(true);
    			},
    			itemclick:me.ItemClick
    		},
    		'erpSaveButton' :{
    			click:function(btn){
    				var grid = Ext.getCmp('grid');
    				var form = Ext.getCmp('form');
    				var items = grid.store.data.items;
    				var num=0;
    				var id = Ext.getCmp('mf_id').value;
    				if(Ext.getCmp('mf_qty').value==0||Ext.getCmp('mf_qty').value==null){
    					showError("流程单数量不能为0！");
    					return;
    				}
    				if(id!=0||id !=null||id!=""){
    					Ext.each(items, function(item){
        					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''&&item.data['mf_id']!=id){
        						num =num+item.data['mf_qty'];
        					}
    					});
    				}else{
    					Ext.each(items, function(item){
        					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
        						num =num+item.data['mf_qty'];
        					}
    					});
    				}
    				if(num+Ext.getCmp('mf_qty').value>Ext.getCmp('ma_qty').value){
    					showError("流程单数量总数超过制造单总数！");
    					return;
    				}
    				if(Ext.getCmp('mf_madeqty').value==""||Ext.getCmp('mf_madeqty').value==null){
    					Ext.getCmp('mf_madeqty').setValue('0');
    				}
    				if(Ext.getCmp('mf_madeqty').value>Ext.getCmp('mf_qty').value){
    					showError("数量不能少于已完工数！");
    					return;
    				}
    				//this.FormUtil.beforeSave(this);	
    				//var data = grid.getGridStore();
    				grid.setLoading(true);
    				Ext.getCmp('mf_maid').setValue(Ext.getCmp('ma_id').value);
    				/*if(Ext.getCmp('mf_code').value==""||Ext.getCmp('mf_code').value==null){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}*/
    				var formValue = form.getValues();
    				var formstore = unescape(Ext.JSON.encode(formValue).replace(/\\/g,"%"));
    				Ext.Ajax.request({
    		        	url : basePath + "pm/make/saveMakeFlow.action",
    		        	params: {
    		        		gridStore: formstore
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.success){
    		        			saveSuccess(function(){
    		        				window.location.href = window.location.href;
    		        			});
    		        		};
    		        	}
    		        });
    			}    			
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				Ext.Ajax.request({
    		        	url : basePath + "pm/make/CheckdeleteMakeflow.action",
    		        	params: {
    		        		code: Ext.getCmp('mf_code').value
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.log!=null){
    			   				showMessage('提示', res.log);
    			   				return;
    			   			}
    		        	}
    		        });
    				var grid = Ext.getCmp('grid');
    				Ext.Ajax.request({
    		        	url : basePath + "pm/make/deleteMakeflow.action",
    		        	params: {
    		        		id: Ext.getCmp('mf_id').value
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.success){
    		        			delSuccess(function(){
    		        				window.location.href = window.location.href;
    		        			});
    		        		};
    		        	}
    		        });
    			},
    			afterrender:function(btn){
    			  btn.setDisabled(true);
    			}
    		},
    	    'erpCloseButton':{
    	    	click:function(btn){
    	    		parent.Ext.getCmp('win').close();
    	    	} 
    	    },
    	    'erpMakeFlowsButton':{
    	    	click:function(btn){
    	    		var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var num=0;
    				var id = Ext.getCmp('mf_id').value;
    				if(Ext.getCmp('mf_date').value==''||Ext.getCmp('mf_date').value==null){
    					alert('请填写日期!');
    					return;
    				}
     				if(Ext.getCmp('mf_qty').value==0||Ext.getCmp('mf_qty').value==null){
     					alert("流程单数量不能为0！");
     					return;
     				}
     				if(id!=0||id !=null||id!=""){
     					Ext.each(items, function(item){
         					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
         						num =num+item.data['mf_qty'];
         					}
     					});
     				}else{
     					Ext.each(items, function(item){
         					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
         						num =num+item.data['mf_qty'];
         					}
     					});
     				}
     				if(num+Ext.getCmp('mf_qty').value>Ext.getCmp('ma_qty').value){
     					showError("流程单数量总数超过制造单总数！");
     					return;
     				}
    	    		 warnMsg("确定要拆分流程单吗?", function(btn){
     					if(btn == 'yes'){
 			    			Ext.Ajax.request({
 			     			    url : basePath + 'pm/make/newmakeflows.action',
 			     			    params: {
 			     			        id: Ext.getCmp('ma_id').value,
 			     			        number:Ext.getCmp('ma_qty').value-num,
 			     			        mfqty:Ext.getCmp('mf_qty').value,
 			     			        date:Ext.getCmp('mf_date').value
 			     			    },
 			     			    method : 'post',
 			     			    callback : function(options,success,response){
 			     			   		var localJson = new Ext.decode(response.responseText);
 			     			   		if(localJson.success){
 			     			   			Ext.Msg.alert("提示","分拆成功！");
 			     			   			window.location.reload();
 			     			   		} else {
 			     			   		Ext.Msg.alert("提示","分拆失败！");
 			     			   		}
 			     			   	}
 			    			});	 
     					}
     				});
    	    	}
    	    },
    		'erpAddButton': {
    			click: function(){
    				if(caller=='MakeFlow'){
	    				 Ext.getCmp('deletebutton').setDisabled(true);
	    				 Ext.getCmp('mf_date').setValue("");
	    				 Ext.getCmp('mf_madeqty').setValue("");
	    				 Ext.getCmp('mf_qty').setValue("");
	    				 Ext.getCmp('mf_maid').setValue("");
	    				 Ext.getCmp('mf_code').setValue("");
	    				 Ext.getCmp('mf_id').setValue("");
    				}
    			},
    			afterrender:function(){
    				if(caller=='MakeFlow'){
    					 Ext.getCmp('mf_date').setValue("");
        				 Ext.getCmp('mf_madeqty').setValue("");
        				 Ext.getCmp('mf_qty').setValue("");
        				 Ext.getCmp('mf_maid').setValue("");
        				 Ext.getCmp('mf_code').setValue("");
        				 Ext.getCmp('mf_id').setValue("");
    				}    				
    			}
    		},
    		'erpPrintButton':{
    			click: function(btn){
    				var reportName="byqmakeflow";
					var condition='{makeflow.mf_id}='+Ext.getCmp('mf_id').value+'';
					var id=Ext.getCmp('mf_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);	
    			},
				afterrender:function(btn){
	    			  btn.setDisabled(true);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	ItemClick:function(view ,record){
		if(record.data["mf_code"]!='' &&  record.data["mf_code"]!=null){
			if(caller=='MakeFlow'){
				Ext.getCmp('print').setDisabled(false);
				Ext.getCmp('deletebutton').setDisabled(false);
				var form=view.ownerCt.ownerCt.items.items[0];
				form.getForm().setValues(record.data);
			}
		} 
	}
});