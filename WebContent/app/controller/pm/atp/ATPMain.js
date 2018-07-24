Ext.QuickTips.init();
Ext.define('erp.controller.pm.atp.ATPMain', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.atp.ATPMain','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.trigger.MultiDbfindTrigger','core.button.Post','core.button.ResPost',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ExecuteOperation','core.button.SupplyScan',
			'core.button.ATPOperateDetail','core.button.LoadSale','core.button.ATPSetAnalysis'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('am_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addATPMain', '新增应付开票记录', 'jsps/pm/atp/ATPMain.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('am_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('am_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('am_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('am_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('am_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('am_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('am_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('am_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('am_id').value);
    			}
    		},
    		'erpExecuteOperationButton':{
    			afterrender : function(btn){
    			var status = Ext.getCmp('am_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/atp/executeOperation.action',
    			   		params: {
    			   			id: Ext.getCmp('am_id').value
    			   		},
    			   		method : 'post',
    			   		timeout: 600000,
    			   		callback : function(options,success,response){
    			   			me.FormUtil.setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				//resAudit成功后刷新页面进入可编辑的页面 
    		    				showMessage('提示', '执行运算成功!', 2000);
    		    				window.location.reload();
    			   			}
    			   		}
    				});
    			}
    		},
    		'erpSupplyScanButton':{
    			click: function(btn){
 				   var form=Ext.getCmp('form');
		    		  var keyField=form.keyField;
		    		  var KeyValue=Ext.getCmp(keyField).value;
		    		  if(KeyValue==null||KeyValue==''){
		    		    showError('请先保存记录');
		    		  }
		    		  var me = this; 
		              var url=basePath+"jsps/common/queryDetail.jsp";  
		    		  var panel = Ext.getCmp("lackResultid=" +KeyValue);                       
		    	      var main = parent.Ext.getCmp("content-panel");
		    	      var urlCondition='ad_atpid='+KeyValue;
		    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
		    	        if(!panel){ 
		    		          var title = "";
			    	     if (KeyValue.toString().length>4) {
			    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
			    	          } else {
			    		           title = KeyValue;
			    	           }
			    	      panel = { 
			    			title:'供需查询('+KeyValue+')',
			    			tag : 'iframe',
			    			tabConfig:{tooltip:'供需查询('+title+')'},
			    			frame : true,
			    			border : false,
			    			layout : 'fit',
			    			iconCls : 'x-tree-icon-tab-tab',
			    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?whoami=ATPDATA&_noc=1&urlcondition='+urlCondition+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
			    			closable : true,
			    			listeners : {
			    				close : function(){
			    			    	main.setActiveTab(main.getActiveTab().id); 
			    				}
			    			} 
			    	           };
			    	           me.FormUtil.openTab(panel,"lackResultid=" + KeyValue); 
		    	                 }else{ 
			    	           main.setActiveTab(panel); 
		    	            } 
		                 }
    		},
    		'erpATPOperateDetailButton':{
    			click :function(btn){//运算明细
    				 var id=Ext.getCmp('am_id').getValue();
    	    	     var condition="ad_atpid='"+id+"'";
    		         me.FormUtil.onAdd('ATPOperateDetail','运算明细','jsps/pm/atp/ATPOperateDetail.jsp?whoami=ATPOperateDetail&_noc=1&urlcondition='+condition);
    			}
    		},
			'erpLoadSaleButton':{
		    	afterrender:function(btn){
		    		var status = Ext.getCmp('am_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
		    	},		    	
		    	click:function(btn){
		    		var form=Ext.getCmp('form');
		    		var keyField=form.keyField;
		    		var KeyValue=Ext.getCmp(keyField).value;
		    		if(KeyValue== null||KeyValue==''){
		    			showError('请先保存记录');
		    		}
		    		var me = this; 
		    		var url = basePath + "jsps/pm/make/SaleResource.jsp";  
		    		var panel = Ext.getCmp("am_id=" +KeyValue);                       
		    		var main = parent.Ext.getCmp("content-panel");
		    		var panelId= main.getActiveTab().id;
		    		var urlcondition=" (sa_code,sd_detno) not in (select ad_salecode,ad_saledetno from atpdetail  where ad_amid="+KeyValue+" and ad_saledetno>0 )";
		    		main.getActiveTab().currentGrid = Ext.getCmp('grid');
	    	        if(!panel){ 
	    		         var title = "";
	    		         if (KeyValue.toString().length>4) {
	    		        	 title = KeyValue.toString().substring(KeyValue.toString().length-4);	
		    	         } else {
		    		          title = KeyValue;
		    	         }
	    		         panel = { 
	    		        		 title:'ATP销售来源('+KeyValue+')',
	    		        		 tag : 'iframe',
	    		        		 tabConfig:{tooltip:'ATP销售来源('+title+')'},
	    		        		 frame : true,
	    		        		 border : false,
	    		        		 layout : 'fit',
	    		        		 iconCls : 'x-tree-icon-tab-tab',
	    		        		 html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?urlcondition='+urlcondition+'&whoami=ATPSaleSource&_noc=1&keyValue='+KeyValue+'&panelId='+panelId+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    		        		 closable : true,
	    		        		 listeners : {
	    		        			 close : function(){
	    		        				 main.setActiveTab(main.getActiveTab().id); 
	    		        			 }
	    		        		 } 
		    	         };
	    		         me.FormUtil.openTab(panel,"am_id=" + KeyValue); 
	    	        }else{ 
		    	         main.setActiveTab(panel); 
	    	        } 	                 
		    	}
		    },
		    'erpATPSetAnalysisButton':{  //ATP 齐套分析
    			click: function(btn){
 				   var form=Ext.getCmp('form');
		    		  var keyField=form.keyField;
		    		  var KeyValue=Ext.getCmp(keyField).value;
		    		  if(KeyValue==null||KeyValue==''){
		    		    showError('请先保存记录');
		    		  }
		    		  var me = this; 
		              var url=basePath+"jsps/common/queryDetail.jsp";  
		    		  var panel = Ext.getCmp("setAnalysisid=" +KeyValue);                       
		    	      var main = parent.Ext.getCmp("content-panel");
		    	      var urlCondition='ad_atpid='+KeyValue;
		    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
		    	        if(!panel){ 
		    		          var title = "";
			    	     if (KeyValue.toString().length>4) {
			    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
			    	          } else {
			    		           title = KeyValue;
			    	           }
			    	      panel = { 
			    			title:'齐套分析('+KeyValue+')',
			    			tag : 'iframe',
			    			tabConfig:{tooltip:'齐套分析('+title+')'},
			    			frame : true,
			    			border : false,
			    			layout : 'fit',
			    			iconCls : 'x-tree-icon-tab-tab',
			    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?whoami=ATPProdSetData&_noc=1&urlcondition='+urlCondition+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
			    			closable : true,
			    			listeners : {
			    				close : function(){
			    			    	main.setActiveTab(main.getActiveTab().id); 
			    				}
			    			} 
	    	           };
	    	           me.FormUtil.openTab(panel,"setAnalysisid=" + KeyValue); 
    	                 }else{ 
	    	           main.setActiveTab(panel); 
    	            } 
                 }
		    }
    	});
    }, 


    beforeSubmit:function(btn){
    	var me = this;
    	var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    	me.FormUtil.onSubmit(Ext.getCmp('am_id').value);
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});