Ext.QuickTips.init();
Ext.define('erp.controller.plm.record.SpecialRecord', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.record.RecordLog','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Upload','core.button.DownLoad',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','common.datalist.GridPanel','plm.record.SpecialTreePanel',
    	],
    init:function(){
       var me = this;
    	me.attachcount = 0;
    	this.control({     	
    	    'erpSpecialTreePanel': {
    			itemmousedown: function(selModel, record){
    				if(record.get('leaf')){	
    				   var id=record.data.id;
    				   Ext.get('grid').hide();
    			       Ext.getCmp('form').show();
    				   Ext.Ajax.request({//拿到grid的columns
	        	       url : basePath + 'common/loadNewFormStore.action',
	        	       async: false,
	        	       params: {
	        		     caller: caller,
	        		     condition:"wr_id="+id
	        	       },
	        	       method : 'post',
	        	       callback : function(options,success,response){
	        		   var res = new Ext.decode(response.responseText);
	        		   var data=new Ext.decode(res.data);
	        		   Ext.getCmp('form').getForm().setValues(data)
	        		   Ext.getCmp('wr_progress').updateProgress(data.wr_taskpercentdone/100,'当前任务进度:'+Math.round(data.wr_taskpercentdone)+'%');   			
	        	      }
	                });  
    		 if(id!=''){
    			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'common/getFormAttachs.action',
	        	async: false,
	        	params: {
	        		caller: caller,
	        		keyvalue: id
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			return;
	        		}
	        		attach = res.attach != null ? res.attach : [];
	        	}
	        });
	        if(!Ext.getCmp('container')){
	        Ext.getCmp('form').add({
	         title:'相关文件',
	         id:'container',
	         style: {borderColor:'green', borderStyle:'solid', borderWidth:'0px'},
	         xtype:'container',
	         columnWidth:1
	        });
	        }
	        Ext.getCmp('container').removeAll();
        		var items = new Array();
        		items.push({
			    style: 'background:#CDBA96;',
				html: '<h1>相关附件:</h1>',
				});
    	    Ext.each(attach, function(){
				var path = this.fa_path;
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
    			 }    		
					 }
					
    			},
    			afterrender:function(panel){
	    				   var item=new Object();
	    				   var button=new Object();
	    				   button.xtype='button';
	    				   button.cls='btn-cls';
	    				   button.text='查看列表';
	    				   button.iconCls='x-button-icon-addgroup';
	    				   button.style='margin-left:20px;';
	    				   button.handler=function open(){
	    					 Ext.get('grid').show();
    			            Ext.getCmp('form').hide();
	    				   };
	    				   panel.add(button);
	    			   },
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
    		'erpCloseButton': {
    		   afterrender: function(btn){    			 
    		        Ext.getCmp('wr_redcord').setHeight(280);	
    				btn.hide(); 
    			},		 
    		},
    		 
    		'erpUploadButton':{
    		   afterrender: function(btn){
    			btn.hide();  
    			},
    		}
    	});
    },
       getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},	
});