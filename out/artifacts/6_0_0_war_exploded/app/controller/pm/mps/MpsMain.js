Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MpsMain', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','pm.mps.MpsMain','core.grid.Panel2','core.toolbar.Toolbar','core.button.DeleteAllDetails','core.button.LoadingSource','core.button.GoMpsDesk',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.Update','core.button.Delete',
	       'core.button.Upload','core.button.ResAudit','core.button.DeleteDetail','core.button.ResSubmit','core.button.MRPLoad','core.button.OdDynamicAnalysis','core.button.ImportExcel',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Flow','core.form.YnField','core.grid.YnColumn','core.button.Refresh','core.button.GetMaster','core.button.MRPAutoLoad',
	       'core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    		   'erpGridPanel2': { 
	    			   itemclick: function(selModel, record){
	    				   if(!Ext.getCmp('grid').readOnly){
	    					   this.onGridItemClick(selModel, record);
	    				   }
	    			   }
	    		   },
	    		   'erpExportDetailButton':{
		    			  afterrender:function(btn){
		    				  var grid=Ext.getCmp('grid');
		    				  btn.ownerCt.add({
		    					  xtype:'tbtext',
		    					  text:'总条数:'+grid.store.prefetchData.items.length
		    				  });  
		    			  }
		    		    },
	    		   'textfield[name=mm_kind]':{
	    			   afterrender:function(field){
	    				   if(field.value==''){
	    					   field.setValue(me.BaseUtil.getUrlParam('kind'));
	    				   }
	    			   }
	    		   },
	    		  'field[name=mm_coplist]':{
		    		  	afterrender:function(t){
		    		  	  if(Ext.getCmp('mm_id') && Ext.getCmp('mm_id').value>0){
		    		  	  	  var grid = Ext.getCmp('grid');
		    		  	  	  var hasitem = false;
		    		  	  	  if(grid){
		    		  	  	  	  var items = grid.store.data.items;
			    		  	  	  Ext.Array.each(items,function(item){
		    					      if(item.data['md_id']>0){
		    					           hasitem = true;
		    					           return false;
		    					      }
			    		  	  	  });
		    		  	  	  }
		    		  	  	  if(hasitem){
						   	   	   t.editable=false;
						   	   	   t.readOnly=true;
						   	   	   if(!t.hideTrigger){
						   	   	      t.hideTrigger=true;
						   	   	   }
		    		  	  	  }
						    }
		    		  	}
	    		  },
	    		  
	    		  'field[name=mm_ifautoload]':{
	    		  	  afterrender:function(t){
		    		  	  if(Ext.getCmp('mm_id') && Ext.getCmp('mm_id').value!=''){
		    		  	  	  var grid = Ext.getCmp('grid');
		    		  	  	  var hasitem = false;
		    		  	  	  if(grid){
		    		  	  	  	  var items = grid.store.data.items;
			    		  	  	  Ext.Array.each(items,function(item){
		    					      if(item.data['md_id']>0){
		    					           hasitem = true;
		    					           return false;
		    					      }
			    		  	  	  });
		    		  	  	  }
		    		  	  	  if(hasitem){
						   	   	   t.editable=false;
						   	   	   t.readOnly=true;
		    		  	  	  }
						    }
		    		  	}
	    		  },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   this.save(this);
	    			   }
	    		   },
	    		   'erpGoMpsDeskButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var form=Ext.getCmp('form');  
	    				   var MainCode=Ext.getCmp(form.codeField).value; 
	    				   me.FormUtil.onAdd('MpsDesk','工作台','/jsps/pm/mps/MpsDesk.jsp?code='+MainCode);
	    			   }
	    		   },
	    		   'dbfindtrigger': {
	    			   change: function(trigger){
	    				   if(trigger.name == 'team_prjid'){
	    					   this.changeGrid(trigger);
	    				   }
	    			   }
	    		   },
	    		   'button[id=deleteallbutton]':{
	    			   click:function(btn){
	    				   var form=me.getForm(btn);
	    				   var id=Ext.getCmp('mm_id').getValue();
	    				   if(!id){
	    					   showError('单据不存在任何明细!');
	    					   return
	    				   }
	    				   Ext.Ajax.request({
	    					   method:'post',
	    					   url:basePath+form.deleteAllDetailsUrl,
	    					   params:{
	    						   id:Ext.getCmp('mm_id').getValue()
	    					   },
	    					   callback : function(options,success,response){
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.success){
	    							   Ext.Msg.alert('提示','清除成功!',function(btn){
	    								   //update成功后刷新页面进入可编辑的页面 
	    								   window.location.reload();
	    							   });
	    						   } else if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);return;
	    						   } 
	    					   }
	    				   });
	    			   },
	    		     afterrender:function(btn){
	    		    	 var statuscode=Ext.getCmp('mm_statuscode').getValue();
	    		    	 if(statuscode&&statuscode!='ENTERING'){
	    		    		 btn.hide();
	    		    	 }
	    		     }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }	    			    
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onDelete(Ext.getCmp('mm_id').value);
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp("mm_id").value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode"); 
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp("mm_id").value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp("mm_id").value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp("mm_id").value);
	    			   }
	    		   },
	    		   'erpRefreshButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var grid=Ext.getCmp('grid');  
	        				var condition=Ext.getCmp('mm_kind')=='MDS'?"mdd_mainid="+Ext.getCmp("mm_id").value:"md_mainid="+Ext.getCmp("mm_id").value;
	        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
	    			   }   		    		
	    		   },
	    		   'erpMRPLoadButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("mm_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },    			   	
	    			 
	    		   },
	    		   'erpGetMasterButton':{
	    			   confirm:function(btn,data){
	    				   Ext.getCmp('mm_coplist').setValue(data);
	    			   }
	    		   },
	    		   'erpAddButton': {
		    			click: function(){
		    				me.FormUtil.onAdd('addMpsMain', '新增MRP计划', 'jsps/pm/mps/mpsMain.jsp?kind=MRP');
		    			}
		    		}, 
	    		   'erpMRPLoadAllButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('mm_statuscode');
	    				   if(status && status.value!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   me.FormUtil.getActiveTab().setLoading(true);
	    				   Ext.Ajax.request({//拿到form的items
	    			        	url : basePath +'pm/mps/autoLoad.action',
	    			        	params:{
	    			        	  id:Ext.getCmp('mm_id').value	
	    			        	},
	    			        	method : 'post',
	    			        	callback : function(options,success,response){
	    			        		me.FormUtil.getActiveTab().setLoading(false);
	    			        		var res = new Ext.decode(response.responseText);
	    			        		if(res.exceptionInfo != null){
	    			        			showError(res.exceptionInfo);return;
	    			        		}else if(res.success){
	    			        			Ext.Msg.alert('提示','装载成功!',function(){
	    			        				var grid=Ext.getCmp('grid');
	    			        				var condition=Ext.getCmp('mm_kind')=='MDS'?"mdd_mainid="+Ext.getCmp("mm_id").value:"md_mainid="+Ext.getCmp("mm_id").value;
	    			        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:condition});
	    			        			});
	    			        		}
	    			        	}
	    				   });
	    			   }
	    		   },
	    		   'erpImportExcelButton':{
	    			   afterrender:function(btn){  
	    				   var statuscode=Ext.getCmp('mm_statuscode').value;
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'filefield[id=excelfile]':{
	    			   change: function(field){
	   					var filename = '';
	   			    	if(contains(field.value, "\\", true)){
	   			    		filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
	   			    	} else {
	   			    		filename = field.value.substring(field.value.lastIndexOf('/') + 1);
	   			    	}
	   			    	var keyValue=Ext.getCmp('mm_id').value;
	   					field.ownerCt.getForm().submit({
	   	            	    url: basePath + 'common/insertByExcel.action?caller=' + caller+'&keyValue='+keyValue,
	   	            		waitMsg: "正在解析文件信息",
	   	            		success: function(fp,o){
	   	            			if(o.result.error){
	   	            				showError(o.result.error);
	   	            			} else {	            				
	   	            				var grid=Ext.getCmp('grid');
           		        			var param={
           		        				caller:'MpsMain',
           		        				condition:'md_mainid='+keyValue
           		        			};
           		        			grid.GridUtil.loadNewStore(grid,param);           				
	   	            			}
	   	            		}	
	   	            	});
	   				}
	    		   }
	    	   });
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   this.GridUtil.onGridItemClick(selModel, record);
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){
	    	   var me = this;
	    	   if(Ext.getCmp('mm_code').value == null || Ext.getCmp('mm_code').value == ''){
	    		   me.BaseUtil.getRandomNumber();
	    	   }
	    	   me.FormUtil.beforeSave(me);
	       }
});