Ext.QuickTips.init();
Ext.define('erp.controller.plm.record.RecordLog', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.record.RecordLog','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Upload','core.button.DownLoad',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.FileField','core.form.MultiField','core.form.YnField',
	       'core.button.ResSubmit'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.attachcount = 0;
	    	   this.control({ 

	    		   'erpGridPanel2': {
	    			   //itemclick: this.onGridItemClick
	    		   },
	    		   'erpSaveButton': {
	    			   afterrender: function(btn){
	    				   btn.hide();  
	    			   },
	    		   }, 
	    		   'erpUpdateButton':{
	    			   afterrender: function(btn){
	    				   btn.hide();   				
	    			   },   			
	    		   },
	    		   'erpSubmitButton':{
	    		       afterrender:function(btn){
	    		       	   var statuscode = Ext.getCmp('wr_statuscode');
	    		       	   if(statuscode&&statuscode.value!='ENTERING'){
	    		       	   	   btn.hide();
	    		       	   }
	    		       }
	    		   },
	    		   'erpResSubmitButton':{
	    		       afterrender:function(btn){
	    		       	   var statuscode = Ext.getCmp('wr_statuscode');
	    		       	   if(statuscode&& statuscode.value != 'COMMITED'){
	    		       	   	   btn.hide();
	    		       	   }	
	    		       	   
	    		       	   //重写FormUtil的ressubmit方法
	    		       	   me.FormUtil.onResSubmit = function(id){
								var me = this;
								var form = Ext.getCmp('form');
								if(!me.contains(form.resSubmitUrl, '?caller=', true)){
									form.resSubmitUrl = form.resSubmitUrl + "?caller=" + caller;
								}
								me.setLoading(true);//loading...
								Ext.Ajax.request({
									url : basePath + form.resSubmitUrl,
									params: {
										id: id
									},
									method : 'post',
									callback : function(options,success,response){
										me.setLoading(false);
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);
										}
										if(localJson.success){
											showMessage('提示', '反提交成功!', 1000);
											me.getActiveTab().close();
										}
									}
								});	    		       	   	
	    		       	   };
	    		       },
	    		       click:function(btn){
	    		           Ext.Msg.confirm('警告','反提交将会删除该任务日志并清除流程，是否确定?',function(btn){
	    		               if(btn=='yes'){
									me.FormUtil.onResSubmit(Ext.getCmp('wr_id').value);
	    		               }
	    		           });
	    		       }
	    		   },
	    		   'erpCloseButton': {
	    			   afterrender: function(btn){    			 
	    				   var value=Ext.getCmp('wr_taskpercentdone').getValue();
	    				   var unit=Ext.getCmp('wr_assignpercent').getValue();
	    				   var percent=Ext.getCmp('wr_percentdone');
	    				   percent.setReadOnly(true);
	    				   value=value+(percent.value)*unit/100;
	    				   Ext.getCmp('wr_progress').updateProgress(value/100,'当前任务进度:'+Math.round(value)+'%');   			
	    				   Ext.getCmp('wr_redcord').setHeight(320);
	    				   var form=me.getForm(btn);
/*	    				   var attachs=Ext.getCmp("wr_attachs").getValue();
	    				   if(attachs!=null){
	    					   Ext.Ajax.request({//拿到grid的columns
	    						   url : basePath + 'common/getFilePaths.action',
	    						   async: false,
	    						   params: {
	    							   id:attachs
	    						   },
	    						   method : 'post',
	    						   callback : function(options,success,response){
	    							   var res = new Ext.decode(response.responseText);
	    							   if(res.exception || res.exceptionInfo){
	    								   showError(res.exceptionInfo);
	    								   return;
	    							   }
	    							   attach =  res.files != null ?  res.files : [];
	    						   }
	    					   });
	    					   form.add({
	    						   title:'相关文件',
	    						   id:'container',
	    						   style: {borderColor:'green', borderStyle:'solid', borderWidth:'0px'},
	    						   xtype:'container',
	    						   columnWidth:1
	    					   });
	    					   var items = new Array();
	    					   items.push({
	    						   style: 'background:#CDBA96;',
	    						   html: '<h1>相关文件:</h1>',
	    					   });
	    					   Ext.each(attach, function(){
	    						   var path = this.fp_path;
	    						   var name = '';
	    						   if(contains(path, '\\', true)){
	    							   name = path.substring(path.lastIndexOf('\\') + 1);
	    						   } else {
	    							   name = path.substring(path.lastIndexOf('/') + 1);
	    						   }
	    						   items.push({

	    							   style: 'background:#C6E2FF;',
	    							   html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
	    							   '<span>文件:' + name + '<a href="' + basePath + "common/download.action?path=" + path + '">下载</a></span>',
	    						   });
	    					   });
	    					   Ext.getCmp('container').add(items);
	    				   }*/   			
	    			   },	 
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   },    		  
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },	
});