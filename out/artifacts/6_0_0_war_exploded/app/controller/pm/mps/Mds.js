Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.Mds', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.Mds','core.grid.Panel2','core.toolbar.Toolbar','core.button.DeleteAllDetails','core.button.LoadingSource',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Refresh'
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
    	   'erpSaveButton': {
    			click: function(btn){
    				this.save(this);
    			},
    			afterrender:function(){
    				
    			}
    		},
    	  'dbfindtrigger': {
    			change: function(trigger){
    				if(trigger.name == 'team_prjid'){
    					this.changeGrid(trigger);
    				}
    			}
    		},
    	  'erpRefreshButton':{   	
    		   click:function(btn){
    		   var grid=Ext.getCmp('grid');
    		   var value=Ext.getCmp('mds_id').value;
    		   var gridCondition=grid.mainField+'='+value;
    		   gridParam = {caller: 'MDS', condition: gridCondition};
    		   me.GridUtil.loadNewStore(grid,gridParam);
    		   }   		    		
    		},
    	 'erpLoadingSourceButton':{
    	 	afterrender: function(btn){
    	 	 var statuscode=Ext.getCmp('mds_statuscode').getValue();
	    		  if(statuscode && statuscode != 'ENTERING'){
	    		     btn.hide();
	    		    }
    	 	},
    		  click:function(btn){
    		  var form=Ext.getCmp('form');
    		  var keyField=form.keyField;
    		  var KeyValue=Ext.getCmp(keyField).value;
    		  if(KeyValue==null||KeyValue==''){
    		    showError('请先保存记录');
    		  }
    		  var me = this; 
              var url=basePath+"jsps/pm/source/Source.jsp";                        
    	      var main = parent.Ext.getCmp("content-panel");
    		  var panel = Ext.getCmp("sourceid=" +KeyValue);                       
    	      var main = parent.Ext.getCmp("content-panel");
    	       var kind= 'MDS';
    	      var panelId= main.getActiveTab().id;
    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
    	        if(!panel){ 
    		          var title = "";
	    	     if (KeyValue.toString().length>4) {
	    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
	    	          } else {
	    		           title = KeyValue;
	    	           }
	    	      panel = { 
	    			title:'来源查询('+KeyValue+')',
	    			tag : 'iframe',
	    			tabConfig:{tooltip:'来源查询('+title+')'},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?keyValue='+KeyValue+'&kind='+kind+'&panelId='+panelId+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	           };
	    	           me.FormUtil.openTab(panel,"sourceid=" + KeyValue); 
    	                 }else{ 
	    	           main.setActiveTab(panel); 
    	            } 
       } 
    		  },
    		    'button[id=deleteallbutton]':{
	    			   click:function(btn){
	    				   var form=me.getForm(btn);
	    				   var id=Ext.getCmp('mds_id').getValue();
	    				   if(!id){
	    					   showError('单据不存在任何明细!');
	    					   return
	    				   }
	    				   Ext.Ajax.request({
	    					   method:'post',
	    					   url:basePath+form.deleteAllDetailsUrl,
	    					   params:{
	    						   id:Ext.getCmp('mds_id').getValue()
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
	    		    	 var statuscode=Ext.getCmp('mds_statuscode').getValue();
	    		    	 if(statuscode && statuscode != 'ENTERING'){
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
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('mds_id').value);
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMDS', 'MDS计划维护', 'jsps/pm/mps/Mds.jsp');
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mds_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mds_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mds_statuscode');
    				if((Ext.getCmp('mds_id').value == null || Ext.getCmp('mds_id').value == '') 
    						|| (status && status.value != 'ENTERING')){
    					btn.hide();
    				}
    			},
    			click: function(btn){    				
    				me.FormUtil.onSubmit(Ext.getCmp('mds_id').value);			
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mds_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mds_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mds_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mds_id').value);
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
		if(Ext.getCmp('mds_code').value == null || Ext.getCmp('mds_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	changeGrid: function(trigger){
		var grid = Ext.getCmp('grid');
		Ext.Array.each(grid.store.data.items, function(item){
		
		});
	}
	
});