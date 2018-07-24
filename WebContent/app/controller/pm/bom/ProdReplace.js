Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ProdReplace', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.ProdReplace','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.SetMain'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			},
    			storeloaded:function(){
    				var panelId=getUrlParam('panelId');
    				var bomid =getUrlParam('bomid');
    				var panel=parent.Ext.getCmp(panelId);
    				if(panel){
    					var grid=panel.currentGrid;
    					me.GridUtil.loadNewStore(grid,{
    						caller:'BOM',
    						condition:"bd_bomid="+bomid
    					});
    				}
    			}
    		},
    		'erpSaveButton': {
    			 afterrender:function(btn){
    				 var statuscode=Ext.getCmp('bo_statuscode');
    				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
    		   			btn.hide(); 
    				 }
    			 },
				click: function(btn){					
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['pre_baseqty'] == null || item.data['pre_baseqty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['pre_detno'] + "行未填写单位数量，或需求为0");return;
    						}
    					}
    				});
    				if(bool){
    					me.GridUtil.onSave(Ext.getCmp('grid'));
    				}
				}
    		},
    		'erpSetMainButton':{
     	    	click:function(btn){  
     	    		var record = Ext.getCmp('grid').selModel.lastSelected;
     	    		if(record){
						// confirm box modify
						// zhuth 2018-2-1
						Ext.Msg.confirm('提示', '确定要设置'+record.data.pre_repcode+'为主料?', function(btn) {
							if(btn == 'yes') {
								Ext.Ajax.request({
									url : basePath + 'pm/bom/setMain.action',
									params: {
										pre_id:record.data.pre_id,
										caller:caller
									},
									method : 'post',
									callback : function(options,success,response){ 
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);
										} else {
											if(localJson.success){  
												window.location.reload();
											}
										}
									}
							 	});
							}
						});
     	    		}else{
     	    			Ext.Msg.alert("提示","请先选择明细!");
     	    		}
     	    		
     	    	} 
     	    	 
     	    },
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(this);
    				var activetab = me.BaseUtil.getActiveTab();
    				activetab.on('close',function(){
    					var panelId=getUrlParam('panelId');
    					var bomid =getUrlParam('bomid');
    					var panel=parent.Ext.getCmp(panelId);
    					if(panel){
    						var grid=panel.currentGrid;
    						me.GridUtil.loadNewStore(grid,{
    							caller:'BOM',
    							condition:"bd_bomid="+bomid
    						});
    					}
    				});
    			}
    		},
    		'dbfindtrigger[name=bd_soncode]': {
    			afterrender: function(t){
    				t.dbKey = "bd_bomid";
    				t.mappingKey = "bd_bomid";
    				t.dbMessage = "请先选择BOMID!";
    			},
    			aftertrigger:function(field){  
 	   				 var statuscode=Ext.getCmp('bo_statuscode');
 	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){   
 	   					 Ext.getCmp('updatebutton').hide(); 
 	   					 Ext.getCmp('deletebutton').hide();
 	   				 } else{  
 	   					 Ext.getCmp('updatebutton').show();
 	   					 Ext.getCmp('deletebutton').show(); 
 	   				 }
    			}
    		},
    		'dbfindtrigger[name=bd_bomid]': {
    			aftertrigger:function(field){  
  	   				 var statuscode=Ext.getCmp('bo_statuscode');
  	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){   
  	   					 Ext.getCmp('updatebutton').hide(); 
  	   					 Ext.getCmp('deletebutton').hide();
  	   				 } else{  
  	   					 Ext.getCmp('updatebutton').show();
  	   					 Ext.getCmp('deletebutton').show(); 
  	   				 }
    			}
    		},
    		'field[name=bd_id]': {
				change: function(f){
					if(f.value != null && f.value != ''){
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
							caller: caller,
							condition: 'pre_bdid=' + f.value
						});
						Ext.getCmp('deletebutton').show();
						Ext.getCmp('updatebutton').show();
						//Ext.getCmp('save').hide();
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();			
						//Ext.getCmp('save').show();
					}
				}
			},
    		'erpUpdateButton': {
    			afterrender:function(btn){
	   				 var statuscode=Ext.getCmp('bo_statuscode');
	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
	   		   			btn.hide(); 
	   				 }
   			 	},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true; 
    				var soncode = Ext.getCmp('bd_soncode').value
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['pre_repcode'] == null || item.data['pre_repcode'] == ''  ){
    							bool = false;
    							showError("明细第" + item.data['pre_detno'] + "行未填写替代料编号");return;
    						}
    						if( item.data['pre_repcode'] == soncode  ){
    							bool = false;
    							showError("明细第" + item.data['pre_detno'] + "行替代料号不能和主料重复");return;
    						}
    					}
    				}); 
    				if(bool){
    					me.GridUtil.onUpdate(Ext.getCmp('grid'));
    				}
    			}
    		}, 
   		 	'erpDeleteDetailButton':{
			   afterrender:function(btn){   				   
				 var statuscode=Ext.getCmp('bo_statuscode');
 				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
 		   			btn.hide(); 
 				 }
			   }
		    }
			
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择 
    	this.GridUtil.onGridItemClick(selModel, record);
    	var btn = Ext.getCmp('grid').down('erpDeleteDetailButton');
    	if(btn&&Ext.getCmp('bo_statuscode').value=='AUDITED'){
    		btn.setDisabled(true);
    	}
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});