Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.ShortForecast', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'scm.sale.ShortForecast','core.form.Panel','scm.sale.ShortForecastGrid','core.button.DeleteAllDetails','core.button.LoadMake','core.button.ImportExcel',
	       'core.button.Add','core.button.Save','core.button.Close','core.form.MultiField','core.button.Scan','core.button.ConfirmRange',
	       'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.RunLackMaterial','core.grid.YnColumn',
	       'core.button.Update','core.button.Delete','core.form.YnField','core.button.LoadMake','core.button.LackMateriallResult',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.InMaterialNeed','core.button.MaterialLackTwo'
	       ],
	       init:function(){
	    	   var statuscode=null;
	    	   var me = this;
	    	   this.control({ 
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   var form = me.getForm(btn);
	    				   var codeField=Ext.getCmp(form.codeField);
	    				   if(codeField.value == null || codeField.value == ''){
	    					   me.BaseUtil.getRandomNumber();//自动添加编号
	    				   }
	    				   this.FormUtil.beforeSave(this);
	    			   }
	    		   },	 
	    		   'erpShortForecastGrid':{
	    			 select:me.onGridItemClick  
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.update(this);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('sf_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete(Ext.getCmp('wc_id').value);
	    			   },
	    		    afterrender:function(btn){  
	    		    	statuscode=Ext.getCmp('sf_statuscode').getValue();
    				   if(statuscode!='ENTERING'){
    					   btn.hide();
    				   }
    			   } 
	    		   },
	    		   'erpAddButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onAdd('SaleForecast!Short', '新增短期预测', 'jsps/scm/sale/ShortForecast.jsp');
	    			   }
	    		   },
	    		   'erpConfirmRangeButton':{
	    			   click: function(btn){	 
    		             var grid=Ext.getCmp('grid');
	    		         var fromdate=Ext.getCmp('sf_fromdate').getValue();
	    		         var todate=Ext.getCmp('sf_todate').getValue();
	    		         var type=Ext.getCmp('sf_method');
	    		         var lastcolumns=new Array();
	    		         var startfield="";
	                     var endfield="";
	    		         Ext.Array.each(gridcolumns,function(item,m){
	    		 			if(item.dataIndex=='sf_needdate'){
	    		 				var columns=new Array();
	    		 				var count=(todate-fromdate)/(86400000*7);
	    		 				for(var i=0;i<count+1;i++){
	    		 					endfield=Ext.Date.format(Ext.Date.add(startdate,Ext.Date.DAY,7*(i+1)),'Y-m-d');
 	    		 					startfield=Ext.Date.format(Ext.Date.add(startdate,Ext.Date.DAY,7*i),'Y-m-d');
	    		 					columns.push({
	    		 					 text:startfield.substring(5,10)+"~"+endfield.substring(5,10),
	    		 					 dataIndex:startfield+"#"+endfield,
	    		 					 width    : 120,
	    		 				     sortable : true                				     
	    		 					});
	    		 				}
	    		 				item.columns=columns;
	    		 			}
	    		 			lastcolumns[m]=item;
	    		 		});	    		 
	    		         console.log(gridcolumns);
	    		         grid.reconfigure(grid.store,gridcolumns);
	    		         grid.store.loadData(griddata);
	    			   }	   		   
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('wc_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('wc_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('wc_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('wc_id').value);
	    			   }
	    		   },
	    		   'multifield[name=wc_recorder]':{
	    			   beforerender:function(field){
	    				   field.items.items[1].xtype='datefield';    			
	    			   },
	    			   afterrender:function(field){
	    				   if(!field.secondvalue){
	    					   field.items.items[1].setValue(Ext.Date.format(new Date(), 'Y-m-d'));
	    				   }
	    			   }
	    		   },
	    		   'erpImportExcelButton':{
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    		   },
	    		   'filefield[id=excelfile]':{
	    			   change: function(field){
	   					var filename = '';
	   			    	if(contains(field.value, "\\", true)){
	   			    		filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
	   			    	} else {
	   			    		filename = field.value.substring(field.value.lastIndexOf('/') + 1);
	   			    	}
	   					field.ownerCt.getForm().submit({
	   	            	    url: basePath + 'common/upload.action?em_code=' + em_code,
	   	            		waitMsg: "正在解析文件信息",
	   	            		success: function(fp,o){
	   	            			if(o.result.error){
	   	            				showError(o.result.error);
	   	            			} else {	            				
	   	            				var filePath=o.result.filepath;	
	   	            				var keyValue=Ext.getCmp('wc_id').getValue();
	   	            				Ext.Ajax.request({//拿到form的items
	   	            		        	url : basePath + 'pm/make/ImportExcel.action',
	   	            		        	params:{
	   		            					  id:keyValue,
	   		            					  fileId:filePath
	   		            				  },
	   	            		        	method : 'post',
	   	            		        	callback : function(options,success,response){
	   	            		        		var result=Ext.decode(response.responseText);
	   	            		        		if(result.success){
	   	            		        			var grid=Ext.getCmp('grid');
	   	            		        			var param={
	   	            		        				caller:'WCPlan',
	   	            		        				condition:'wd_wcid='+keyValue
	   	            		        			};
	   	            		        			grid.GridUtil.loadNewStore(grid,param);
	   	            		        		}else{
	   	            		        			if(result.exceptionInfo != null){
	   	            		            			showError(res.exceptionInfo);return;
	   	            		            		}
	   	            		        		}
	   	            		        	}
	   	            				});	            				
	   	            			}
	   	            		}	
	   	            	});
	   				}
	    		   },
	    		   'erpDeleteAllDetailsButton':{
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				  me.DeleteAllDetails();
	    			   }
	    		   },
	    		   'erpLoadMakeButton':{
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
                          },
	    			   click: function(btn){
	    			    		  var form=Ext.getCmp('form');
	    			    		  var keyField=form.keyField;
	    			    		  var KeyValue=Ext.getCmp(keyField).value;
	    			    		  if(KeyValue==null||KeyValue==''){
	    			    		    showError('请先保存记录');
	    			    		  }
	    			    		  var me = this; 
	    			              var url=basePath+"jsps/pm/make/makeResource.jsp";   
	    			    		  var panel = Ext.getCmp("sourceid=" +KeyValue);                       
	    			    	      var main = parent.Ext.getCmp("content-panel");
	    			    	      var panelId= main.getActiveTab().id;
	    			    	      var urlcondition=" ma_code not in (select wd_makecode from wcplandetail where wd_wcid="+KeyValue+")"; 
	    			    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
	    			    	        if(!panel){ 
	    			    		          var title = "";
	    				    	     if (KeyValue.toString().length>4) {
	    				    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
	    				    	          } else {
	    				    		           title = KeyValue;
	    				    	           }
	    				    	      panel = { 
	    				    			title:'生产排程来源('+KeyValue+')',
	    				    			tag : 'iframe',
	    				    			tabConfig:{tooltip:'生产排程来源('+title+')'},
	    				    			frame : true,
	    				    			border : false,
	    				    			layout : 'fit',
	    				    			iconCls : 'x-tree-icon-tab-tab',
	    				    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?urlcondition='+urlcondition+'&whoami=WCPlanSource&_noc=1&keyValue='+KeyValue+'&panelId='+panelId+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
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
	    		   'erpRunLackMaterialButton':{
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){ 
						   	// confirm box modify
							// zhuth 2018-2-1
							Ext.Msg.confirm("提示", '确定要运算缺料?', function(btn) {
								if(btn == 'yes') {
									var code = Ext.getCmp('wc_code').getValue(); 
									var mb = new Ext.window.MessageBox();
									mb.wait('正在运算中','请稍后...');
									Ext.Ajax.request({//拿到form的items
										 url : basePath + "pm/make/RunLackMaterial.action",
										 params:{
											  code:code					  
										   },
										 method : 'post',
										 timeout: 300000,
										 callback : function(options,success,response){  
											 mb.close();
											 var result=Ext.decode(response.responseText);
											 if(result.success){
												 Ext.Msg.alert('提示','运算成功!',function(){
													 window.location.reload();
												 });
												 
											 }else{
												 if(result.exceptionInfo != null){
													 showError(result.exceptionInfo);return;
												 }
											 }
										 }
									 });	 
								}
							});
	    			   }
	    		   },
	    		   'erpLackMateriallResultButton':{
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   var form=Ext.getCmp('form');
 			    		  var keyField=form.keyField;
 			    		  var KeyValue=Ext.getCmp(keyField).value;
 			    		  if(KeyValue==null||KeyValue==''){
 			    		    showError('请先保存记录');
 			    		  }
 			    		  var me = this; 
 			              var url=basePath+"jsps/pm/make/WCPlanResult.jsp";   
 			    		  var panel = Ext.getCmp("lackResultid=" +KeyValue);                       
 			    	      var main = parent.Ext.getCmp("content-panel");
 			    	      var urlCondition='ml_planid='+KeyValue;
 			    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
 			    	        if(!panel){ 
 			    		          var title = "";
 				    	     if (KeyValue.toString().length>4) {
 				    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
 				    	          } else {
 				    		           title = KeyValue;
 				    	           }
 				    	      panel = { 
 				    			title:'排产缺料运算结果('+KeyValue+')',
 				    			tag : 'iframe',
 				    			tabConfig:{tooltip:'排产缺料运算结果('+title+')'},
 				    			frame : true,
 				    			border : false,
 				    			layout : 'fit',
 				    			iconCls : 'x-tree-icon-tab-tab',
 				    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?whoami=MaterialLackForWCPlan&_noc=1&urlcondition='+urlCondition+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
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
	    			   'erpMaterialLackTwoButton':{
	    				   afterrender:function(btn){   				   
		    				   if(statuscode!='AUDITED'){
		    					   btn.hide();
		    				   }
		    			   },
		    			   click: function(btn){
		    				   var form=Ext.getCmp('form');
	 			    		  var keyField=form.keyField;
	 			    		  var KeyValue=Ext.getCmp(keyField).value;
	 			    		  var startdate=Ext.getCmp('wc_fromdate').value;
	 			    		  var enddate=Ext.getCmp('wc_todate').value;
	 			    		  if(KeyValue==null||KeyValue==''){
	 			    		    showError('请先保存记录');
	 			    		  }
	 			    		  var me = this; 
	 			              var url=basePath+"jsps/pm/make/WCPlanMaterialLack.jsp";   
	 			    		  var panel = Ext.getCmp("towsourceid=" +KeyValue);                       
	 			    	      var main = parent.Ext.getCmp("content-panel");
	 			    	      var urlCondition='ml_planid='+KeyValue;
	 			    	      url=url+"?whoami=MaterialLackForWCPlan&_noc=1&urlcondition="+urlCondition+"&startdate="+startdate+"&enddate="+enddate;
	 			    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
	 			    	        if(!panel){ 
	 			    		          var title = "";
	 				    	     if (KeyValue.toString().length>4) {
	 				    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
	 				    	          } else {
	 				    		           title = KeyValue;
	 				    	           }
	 				    	      panel = { 
	 				    			title:'排产缺料二维查看('+KeyValue+')',
	 				    			tag : 'iframe',
	 				    			tabConfig:{tooltip:'排产缺料二维查看('+title+')'},
	 				    			frame : true,
	 				    			border : false,
	 				    			layout : 'fit',
	 				    			iconCls : 'x-tree-icon-tab-tab',
	 				    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	 				    			closable : true,
	 				    			listeners : {
	 				    				close : function(){
	 				    			    	main.setActiveTab(main.getActiveTab().id); 
	 				    				}
	 				    			} 
	 				    	           };
	 				    	           me.FormUtil.openTab(panel,"twosourceid=" + KeyValue); 
	 			    	                 }else{ 
	 				    	           main.setActiveTab(panel); 
	 			    	            } 
	 			                 }
	    			   },
	    			   'erpInMaterialNeedButton':{
	    				   afterrender:function(btn){   				   
		    				   if(statuscode!='AUDITED'){
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
		 			              var url=basePath+"jsps/pm/make/WCPlanResult.jsp";   
		 			    		  var panel = Ext.getCmp("lackNotify=" +KeyValue);                       
		 			    	      var main = parent.Ext.getCmp("content-panel");
		 			    	      var urlCondition='mln_planid='+KeyValue;
		 			    	      main.getActiveTab().currentGrid = Ext.getCmp('grid');
		 			    	        if(!panel){ 
		 			    		          var title = "";
		 				    	     if (KeyValue.toString().length>4) {
		 				    		       title = KeyValue.toString().substring(KeyValue.toString().length-4);	
		 				    	          } else {
		 				    		           title = KeyValue;
		 				    	           }
		 				    	      panel = { 
		 				    			title:'送货需求('+KeyValue+')',
		 				    			tag : 'iframe',
		 				    			tabConfig:{tooltip:'送货需求('+title+')'},
		 				    			frame : true,
		 				    			border : false,
		 				    			layout : 'fit',
		 				    			iconCls : 'x-tree-icon-tab-tab',
		 				    			html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?whoami=MaterialLackForNotify&_noc=1&urlcondition='+urlCondition+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
		 				    			closable : true,
		 				    			listeners : {
		 				    				close : function(){
		 				    			    	main.setActiveTab(main.getActiveTab().id); 
		 				    				}
		 				    			} 
		 				    	           };
		 				    	           me.FormUtil.openTab(panel,"lackNotify=" + KeyValue); 
		 			    	                 }else{ 
		 				    	           main.setActiveTab(panel); 
		 			    	            } 
		    			   }
	    			   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
	      	 if(!selModel.view.ownerCt.readOnly){
	      		var grid=selModel.view.ownerCt;
	      		var detno="sd_detno";
	      		var index = null,arr=new Array();
	  			    index = record.data[detno];
	  				index = index == null ? (record.index + 1) : index;
	  				if(index.toString() == 'NaN'){
	  					index = '';
	  				}
	  				if(index == grid.store.last().data[detno]){//如果选择了最后一行
	  					for(var i=0;i < 10;i++ ){
	  						var o = new Object();
	  						o[detno] = Number(index) + i + 1;
	  						arr.push(o);
	  					}
	  					grid.store.loadData(arr, true);
	  		    	}
	  				
	  			} 
	      },
	       DeleteAllDetails:function(){ 
	    	   var id=Ext.getCmp('wc_id').getValue(); 
	    	   Ext.Ajax.request({//拿到form的items
		        	url : basePath + 'pm/make/deleteAllDetails.action',
		        	params:{
   					  id:id,   					  
   				  },
		        	method : 'post',
		        	callback : function(options,success,response){
		        		var result=Ext.decode(response.responseText);
		        		if(result.success){
		        			var grid=Ext.getCmp('grid');
		        			var param={
		        				caller:'WCPlan',
		        				condition:'wd_wcid='+id
		        			};
		        			grid.GridUtil.loadNewStore(grid,param);
		        		}else{
		        			if(res.exceptionInfo != null){
		            			showError(res.exceptionInfo);return;
		            		}
		        		}
		        	}
				});	         
	       },
	       update:function(grid){
	    	   var mm = this.FormUtil;
	   		var form = Ext.getCmp('form');
	   		var s1 = mm.checkFormDirty(form);
	   		var s2 = '';
	   		var grid = Ext.getCmp('grid');
	   		if(grid.GridUtil){
	   			var msg = grid.checkGridDirty(grid);
	   			if(msg.length > 0){
	   				s2 = s2 + '<br/>' + grid.checkGridDirty(grid);
	   			 }
	   		  }
	   		if(s1 == '' && (s2 == '' || s2 == '<br/>')){
	   			showError($I18N.common.form.emptyData + '<br/>' + $I18N.common.grid.emptyDetail);
	   			return;
	   		}
	   		if(form && form.getForm().isValid()){
	   			//form里面数据
	   			var r = form.getValues(false, true);
	   			//去除ignore字段
	   			var keys = Ext.Object.getKeys(r), f;
	   			Ext.each(keys, function(k){
	   				f = form.down('#' + k);
	   				if(f && f.logic == 'ignore') {
	   					delete r[k];
	   				}
	   			});
	   			if(!mm.contains(form.updateUrl, '?caller=', true)){
	   				form.updateUrl = form.updateUrl + "?caller=" + caller;
	   				console.log(form.updateUrl);
	   			}
	   			var params = [];	   			
	   				var param = grid.getGridStore();
	   				console.log(param);
	   				if(grid.necessaryField.length > 0 && (param == null || param == '')){
	   					warnMsg('明细表还未添加数据,是否继续?', function(btn){
	   						if(btn == 'yes'){
	   							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
	   						} else {
	   							return;
	   						}
	   					});
	   				} else {
	   					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
	   				}

	   			mm.update(r, params);
	   		}else{
	   			mm.checkForm(form);
	   		}
	       },
	       onSubmit:function(id){
	   		var me = this;
			var form = Ext.getCmp('form');
			var grid = Ext.getCmp('grid');	
			var s2='';
			if(form && form.getForm().isValid()){
				var s = this.FormUtil.checkFormDirty(form);	
					var param = me.GridUtil.getAllGridStore();
					if(grid.necessaryField.length > 0 && (param == null || param == '')){
						showError("明细表还未添加数据,无法提交!");
						return;
					}
					if(grid.GridUtil){
			   			var msg = grid.GridUtil.checkGridDirty(grid);
			   			if(msg.length > 0){
			   				s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
			   			 }
			   		  }
				if(s2 == '' || s2 == '<br/>'){
					me.FormUtil.submit(id);
				} else {
					Ext.MessageBox.show({
					     title:'保存修改?',
					     msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
					     buttons: Ext.Msg.YESNOCANCEL,
					     icon: Ext.Msg.WARNING,
					     fn: function(btn){
					    	 if(btn == 'yes'){
					    		 me.FormUtil.onUpdate(form);
					    	 } else if(btn == 'no'){
					    		 me.FormUtil.submit(id);	
					    	 } else {
					    		 return;
					    	 }
					     }
					});
				}
			} else {
				me.FormUtil.checkForm();
			}
		},
});