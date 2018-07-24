Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.PreForecast', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	select_id:null,
	views:[
	       'scm.sale.PreForecast','core.form.Panel','scm.sale.PreForecastGrid','core.button.DeleteAllDetails','core.button.LoadMake','core.button.ImportExcel',
	       'core.button.Add','core.button.Save','core.button.Close','core.form.MultiField','core.button.Scan','core.button.ConfirmRange',
	       'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.RunLackMaterial','core.grid.YnColumn',
	       'core.button.Update','core.button.Delete','core.form.YnField','core.button.LoadMake','core.button.LackMateriallResult',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.InMaterialNeed','core.button.MaterialLackTwo','core.form.MonthDateField',
	       'core.form.ConDateField','erp.view.core.button.DeleteDetail','core.button.CopyPreForecast','core.form.ConMonthDateField','core.trigger.MultiDbfindTrigger'
	       ],
	       init:function(){
	    	   var statuscode=null;
	    	   var me = this;
	    	   this.control({
	    		   'erpDeleteDetailButton': {
	    			   click: function(btn){
	    				   Ext.Ajax.request({//拿到form的items
//	    			        	url : basePath + "pm/make/RunLackMaterial.action",
	    					    url : basePath + "scm/sale/deletePreSaleForecastDetail.action",
	    			        	params:{
	    	   					  id:select_id					  
	    	   				   },
	    			        	method : 'post',
	    			        	callback : function(options,success,response){  
	    			        		var result=Ext.decode(response.responseText);
	    			        		if(result.success){
	    			        			Ext.Msg.alert('提示','删除成功!',function(){
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
	    		   },
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
	    		   'erpCopyPreForecast': {
	    			   click:function(btn){
	    				   win = this.copyWindow(btn),
	    				   win.show()
	    			   },    			   	    			  
	    		   },
	    		   'field[name=sf_monthfrom]':{
	    		   		afterrender:function(f){
	    		   			Ext.getCmp('sf_monthto').setMinValue(f.value);
	    		   		},
	    		   		change:function(f){
	    		   			Ext.getCmp('sf_monthto').setMinValue(f.value);
	    		   		}
	    		   },
	    		   'field[name=sf_monthto]':{
	    		   		afterrender:function(f){
	    		   			Ext.getCmp('sf_monthfrom').setMaxValue(f.value);
	    		   		},
	    		   		change:function(f){
	    		   			Ext.getCmp('sf_monthfrom').setMaxValue(f.value);
	    		   		}
	    		   },
	    		   'field[name=sf_method]':{
	    		   		afterrender:function(f){
	    		   			if(Ext.getCmp('sf_monthfrom')&&Ext.getCmp('sf_monthto')){
	    		   				if(f.value=='周'){
	    		   				Ext.getCmp('sf_monthfrom').hide();
	    		   				Ext.getCmp('sf_monthto').hide();
	    		   				Ext.getCmp('sf_fromdate').show();
	    		   				Ext.getCmp('sf_todate').show();
	    		   				Ext.getCmp('sf_dayfrom').hide();
	    		   				Ext.getCmp('sf_dayto').hide();
		    		   			}else if(f.value=='月'){
		    		   				Ext.getCmp('sf_monthfrom').show();
		    		   				Ext.getCmp('sf_monthto').show();
		    		   				Ext.getCmp('sf_fromdate').hide();
		    		   				Ext.getCmp('sf_todate').hide();
		    		   				Ext.getCmp('sf_dayfrom').hide();
		    		   				Ext.getCmp('sf_dayto').hide();
		    		   			}else if(f.value=='天'){
		    		   				Ext.getCmp('sf_monthfrom').hide();
		    		   				Ext.getCmp('sf_monthto').hide();
		    		   				Ext.getCmp('sf_fromdate').hide();
		    		   				Ext.getCmp('sf_todate').hide();
		    		   				Ext.getCmp('sf_dayfrom').show();
		    		   				Ext.getCmp('sf_dayto').show();
		    		   			}	
		    		   		}	   
		    		   		var id=Ext.getCmp('sf_id').getValue();
		    		    	if (Ext.Number.from(id,0)>0) {
		    		    			f.setReadOnly(true);
		    		    			Ext.getCmp('sf_method').setReadOnly(true);
		    		    			Ext.getCmp('sf_monthfrom').setReadOnly(true);
		    		   				Ext.getCmp('sf_monthto').setReadOnly(true);
		    		   				Ext.getCmp('sf_fromdate').setReadOnly(true);
		    		   				Ext.getCmp('sf_todate').setReadOnly(true);
		    		   				Ext.getCmp('sf_dayfrom').setReadOnly(true);
		    		   				Ext.getCmp('sf_dayto').setReadOnly(true);
		    		    	} 
	    		   		},
	    		   		change:function(f){
	    		   			if(Ext.getCmp('sf_monthfrom')&&Ext.getCmp('sf_monthto')){
		    		   			if(f.value=='周'){
		    		   				Ext.getCmp('sf_monthfrom').hide();
		    		   				Ext.getCmp('sf_monthto').hide();
		    		   				Ext.getCmp('sf_fromdate').show();
		    		   				Ext.getCmp('sf_todate').show();
		    		   				Ext.getCmp('sf_dayfrom').hide();
		    		   				Ext.getCmp('sf_dayto').hide();
		    		   			}else if(f.value=='月'){
		    		   				Ext.getCmp('sf_monthfrom').show();
		    		   				Ext.getCmp('sf_monthto').show();
		    		   				Ext.getCmp('sf_fromdate').hide();
		    		   				Ext.getCmp('sf_todate').hide();
		    		   				Ext.getCmp('sf_dayfrom').hide();
		    		   				Ext.getCmp('sf_dayto').hide();
		    		   			}else if(f.value=='天'){
		    		   				Ext.getCmp('sf_monthfrom').hide();
		    		   				Ext.getCmp('sf_monthto').hide();
		    		   				Ext.getCmp('sf_fromdate').hide();
		    		   				Ext.getCmp('sf_todate').hide();
		    		   				Ext.getCmp('sf_dayfrom').show();
		    		   				Ext.getCmp('sf_dayto').show();
		    		   			}
	    		   		}
	    		   		}
	    		   },
	    		   'monthdatefield[name=sf_monthselect]':{
	    			   afterrender : function(f){
		    		    	var id=Ext.getCmp('sf_id').getValue();
		    		    	if (Ext.Number.from(id,0)>0) {
		    		    			f.setReadOnly(true);
		    		    			Ext.getCmp('sf_method').setReadOnly(true);
		    		    			Ext.getCmp('sf_monthfrom').setReadOnly(true);
		    		   				Ext.getCmp('sf_monthto').setReadOnly(true);
		    		   				Ext.getCmp('sf_monthselect').setReadOnly(true);
		    		   				Ext.getCmp('sf_fromdate').setReadOnly(true);
		    		   				Ext.getCmp('sf_todate').setReadOnly(true);
		    		    	} 
		    		    	
	    			   },
	    			   change: function(field){
	    				   var ymonth = field.rawValue;
	    				   var year = ymonth.substr(0,4);
	    				   var month = ymonth.substr(4,2);
	    				   var monthDate = new Date(year,month-1);
	    				   var firstDate = Ext.Date.getFirstDateOfMonth(monthDate);
	    				   var lastDate = Ext.Date.getLastDateOfMonth(monthDate);
	    				   Ext.getCmp('sf_fromdate').setValue(firstDate);
	    				   Ext.getCmp('sf_todate').setValue(lastDate);
	    			   }
	    		   },
	    		   'erpPreForecastGrid':{
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
	    				   me.FormUtil.onDelete(Ext.getCmp('sf_id').value);
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
	    				   me.FormUtil.onAdd('SaleForecast!Pre', '新增人员预测', 'jsps/scm/sale/PreForecast.jsp');
	    			   }
	    		   },
	    		   'erpConfirmRangeButton':{
	    			   click: function(btn){	 
    		             var grid=Ext.getCmp('grid');
	    		         var type=Ext.getCmp('sf_method').value;
	    		         var lastcolumns=new Array();
	    		         var startfield="";
	                     var endfield="";
	    		         Ext.Array.each(gridcolumns,function(item,m){
	    		         	if(type=='周'){
	    		         		if(item.dataIndex=='sd_enddate'){
	    		 				var columns=new Array();
	    		 				item.header='起止时间段(1周)';
	    		 					var fromdate=Ext.getCmp('sf_fromdate').getValue();
	    		         			var todate=Ext.getCmp('sf_todate').getValue();
	    		 					var count=(todate-fromdate)/(86400000*7);
	    		 					count = Math.ceil(count);
	    		 					for(var i=0;i<count;i++){
	    		         			startfield=Ext.Date.format(Ext.Date.add(fromdate,Ext.Date.DAY,7*i),'Y-m-d');
 	    		 					if(i <count-1){
	 	    		 						endfield=Ext.Date.format(Ext.Date.add(sdate,Ext.Date.DAY,6),'Y-m-d');
	 	    		 				} else {
	 	    		 						endfield = Ext.Date.format(todate,'Y-m-d');
	 	    		 				}
	    		 					columns.push({
	    		 					 text:startfield.substring(5,10)+"~"+endfield.substring(5,10),
	    		 					 dataIndex:startfield+"#"+endfield,
	    		 					 width    : 120,
	    		 				     sortable : true                				     
	    		 					});
	    		 				}
	    		 				item.columns=columns;
	    		 			}
	    		         	}else if(type=='月'){
	    		         		if(item.dataIndex=='sd_enddate'){
 	    		 					item.header='起止时间段(1月)';
 	    		 					var columns=new Array();
 	    		 					var startdate=Ext.getCmp('sf_monthfrom').getValue();
 	    		 					var enddate=Ext.getCmp('sf_monthto').getValue();
 	    		 					var y1=(''+enddate).substr(0, 4);
	 	    		 				var y2=(''+startdate).substr(0, 4);
	 	    		 				var m1=(''+enddate).substr(4, 2);
	 	    		 				var m2=(''+startdate).substr(4, 2);
	 	    		 				var count =(y1 - y2) * 12 + (m1 - m2);
	 	    		 				var t1=Ext.getCmp('sf_monthfrom').getValue()+'';
	 	    		 				var from=t1.substring(0,4)+'-'+t1.substring(4,6)+'-01';
	 	    		 				var date= new Date(from);
	 	    		 				for(var i=0;i<=count;i++){
	 	    		 					var sdate=Ext.Date.add(date,Ext.Date.MONTH,i);
	 	    		 					startfield=Ext.Date.format(sdate,'Y-m-d');
	 	    		 					var edate=Ext.Date.getLastDateOfMonth(sdate);
	 	    		 					endfield = Ext.Date.format(edate,'Y-m-d');
	 	    		 					columns.push({
	 	    		 				     readOnly:false,	
	 	    		 					 header:startfield.substring(0,7),
	 	    		 					 cls: "x-grid-header-1",
	 	    		 					 dataIndex:startfield+"#"+endfield,
	 	    		 					 width    : 120,
	 	    		 					 xtype:'numbercolumn',
	 	    		 					 align:'right',
	 	    		 					 format:'0', 	    		 					
	 	    		 				     editor:{
	 	    		 				    	 xtype:'numberfield',
	 	    		 				    	 format:'0',
	 	    		 					     hideTrigger: true
	 	    		 				     }
	 	    		 					});
	 	    		 				}
	 	    		 				item.columns=columns;
	    		         		}
	    		         	}else if(type=='天'){
	    		         		if(item.dataIndex == 'sd_enddate'){
		    		         		item.header = '起止时间段(1天)';
	    		 					var columns=new Array();
	    		 					var dayfrom = Ext.getCmp('sf_dayfrom').getValue();
	    		 					var dayto = Ext.getCmp('sf_dayto').getValue();
	    		 					var count = (dayto-dayfrom)/(86400000);
	    		 					count = Math.ceil(count);
	    		 					for(var i=0;i<=count;i++){
	    		 						var sdate=Ext.Date.add(dayfrom,Ext.Date.DAY,i);
	 	    		 					var startfield=Ext.Date.format(sdate,'Y-m-d');
	 	    		 					var endfield = Ext.Date.format(dayto,'Y-m-d');
	 	    		 					if(i <count-1){
	 	    		 						endfield=Ext.Date.format(Ext.Date.add(sdate,Ext.Date.DAY,1),'Y-m-d');
	 	    		 					} else {
	 	    		 						endfield = Ext.Date.format(dayto,'Y-m-d');
	 	    		 					}
	 	    		 					gridfields.push({
	 	    		 						name:startfield+"#"+endfield,
	 	    		 						type:'int'
	 	    		 					});
	 	    		 					columns.push({
		 	    		 				     readOnly:false,	
		 	    		 					 header:startfield.substring(5,10),
		 	    		 					 cls: "x-grid-header-1",
		 	    		 					 dataIndex:startfield+"#"+endfield,
		 	    		 					 width    : 120,
		 	    		 					 xtype:'numbercolumn',
		 	    		 					 align:'right',
		 	    		 					 format:'0', 	    		 					
		 	    		 				     editor:{
		 	    		 				    	 xtype:'numberfield',
		 	    		 				    	 format:'0',
		 	    		 					     hideTrigger: true
		 	    		 				     }
		 	    		 					});
	    		 					}
	    		 					item.columns=columns;
		    		         	}
	    		         	}
	    		 			lastcolumns[m]=item;
	    		 		});
	    		 		 gridcolumns=lastcolumns;	    
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
	    				   me.FormUtil.onSubmit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender:function(btn){   				   
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('sf_id').value);
	    			   }
	    		   },
	    		
	    		   'erpImportExcelButton':{
	    			   afterrender:function(btn){   				   
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
									mb.wait('正在运算中', '请稍后...');
									Ext.Ajax.request({//拿到form的items
										url: basePath + "pm/make/RunLackMaterial.action",
										params: {
											code: code
										},
										method: 'post',
										timeout: 300000,
										callback: function (options, success, response) {
											mb.close();
											var result = Ext.decode(response.responseText);
											if (result.success) {
												Ext.Msg.alert('提示', '运算成功!', function () {
													window.location.reload();
												});

											} else {
												if (result.exceptionInfo != null) {
													showError(result.exceptionInfo); return;
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
	    	 var statuscode=Ext.getCmp('sf_statuscode').getValue();
			 if(statuscode=='ENTERING'){
				 Ext.getCmp('erpDeleteDetailButton_btn').setDisabled(false);
			 } else {
				 Ext.getCmp('erpDeleteDetailButton_btn').setDisabled(true); 
			 }
	      	 if(!selModel.view.ownerCt.readOnly){
	      		
	      		var grid=selModel.view.ownerCt;
	      		Ext.getCmp('toolbar_tbtext').setText(record.data['sd_detno']);
	      		select_id = record.data['sd_id'];
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
   					  id:id				  
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
	   			}
	   			var params = [];	   			
	   				var param = grid.getGridStore();
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
	       copyWindow : function(btn){    //新增业务员预测方法，点击预测出现新界面选择之后出现新复制的界面
	    	    var me = this;
	    	    var form = Ext.getCmp('form');
	    	    var copylist = form.items.items;
	    	    var means = copylist[4].value;
	    	    var months = copylist[5].value;
	    	    var monthe = copylist[6].value;
	    	    var weeks = copylist[7].value;
	    	    var weeke = copylist[8].value;
	    	    var days = copylist[9].value;
	    	    var daye = copylist[10].value;
	    	   	return	Ext.create('Ext.window.Window',{
							   width : 430,
							   height : 250,
				        	   title: '<h1>预测单复制</h1>',
			        		   layout: {
			        			   type: 'fit'
			        		   },
			        		   items:[{
			        			   xtype : 'form',
			        			   frame : true ,
			        			   defaults:{
			        				   margin:'12 0 0 30'
			        			   },
			        			   layout : {
			        				   type : 'vbox'
			        			   },
			        			   items:[{
			        				   xtype : 'combo',
			        				   name : 'SF_FORECAST',
			        				   editable:false,
			        				   fieldLabel : '预测方法',
			        				   id : 'sf_forecast',
			        				   value : means,
			        				   store: Ext.create('Ext.data.Store', {
					        				fields: ['display', 'value'],
					        				data : [{"display": '周', "value": '周'},
					        				        {"display": '月', "value": '月'},
					        				        {"display": '天', "value": '天'}]
					        			}),
					        			displayField:'display',
					        			valueField: 'value',
					        			listeners:{
					        				afterrender:function(f){						        				
						    		   			if(Ext.getCmp('sf_monthstart')&&Ext.getCmp('sf_monthend')){
							    		   			if(f.value=='周'){
							    		   				Ext.getCmp('sf_monthstart').hide();
							    		   				Ext.getCmp('sf_monthend').hide();
							    		   				Ext.getCmp('sf_weekstart').show();
							    		   				Ext.getCmp('sf_weekend').show();
							    		   				Ext.getCmp('sf_daystart').hide();
							    		   				Ext.getCmp('sf_dayend').hide();
							    		   			}else if(f.value=='月'){
							    		   				Ext.getCmp('sf_monthstart').show();
							    		   				Ext.getCmp('sf_monthend').show();
							    		   				Ext.getCmp('sf_weekstart').hide();
							    		   				Ext.getCmp('sf_weekend').hide();
							    		   				Ext.getCmp('sf_daystart').hide();
							    		   				Ext.getCmp('sf_dayend').hide();
							    		   			}else if(f.value=='天'){
							    		   				Ext.getCmp('sf_monthstart').hide();
							    		   				Ext.getCmp('sf_monthend').hide();
							    		   				Ext.getCmp('sf_weekstart').hide();
							    		   				Ext.getCmp('sf_weekend').hide();
							    		   				Ext.getCmp('sf_daystart').show();
							    		   				Ext.getCmp('sf_dayend').show();
							    		   			}
						    		   			}
					        				},					        			
						        			change:function(f){						        				
						    		   			if(Ext.getCmp('sf_monthstart')&&Ext.getCmp('sf_monthend')){
							    		   			if(f.value=='周'){
							    		   				Ext.getCmp('sf_monthstart').hide();
							    		   				Ext.getCmp('sf_monthend').hide();
							    		   				Ext.getCmp('sf_weekstart').show();
							    		   				Ext.getCmp('sf_weekend').show();
							    		   				Ext.getCmp('sf_daystart').hide();
							    		   				Ext.getCmp('sf_dayend').hide();
							    		   			}else if(f.value=='月'){
							    		   				Ext.getCmp('sf_monthstart').show();
							    		   				Ext.getCmp('sf_monthend').show();
							    		   				Ext.getCmp('sf_weekstart').hide();
							    		   				Ext.getCmp('sf_weekend').hide();
							    		   				Ext.getCmp('sf_daystart').hide();
							    		   				Ext.getCmp('sf_dayend').hide();
							    		   			}else if(f.value=='天'){
							    		   				Ext.getCmp('sf_monthstart').hide();
							    		   				Ext.getCmp('sf_monthend').hide();
							    		   				Ext.getCmp('sf_weekstart').hide();
							    		   				Ext.getCmp('sf_weekend').hide();
							    		   				Ext.getCmp('sf_daystart').show();
							    		   				Ext.getCmp('sf_dayend').show();
							    		   			}
						    		   			}
						    		   		}
					        			}
					        			},{
			        				   xtype : 'datefield',
			        				   id : 'sf_weekstart' ,
			        				   name : 'SF_WEEKSTART',
			        				   fieldLabel : '起始日期',
			        				   value : weeks,    		        
			        				   editable:false
			        			   },{
			        				   xtype : 'monthdatefield',
			        				   id : 'sf_monthstart' ,
			        				   name : 'SF_MONTHSTART',
			        				   fieldLabel : '起始日期',
			        				   value : months,
			        				   editable:false
			        			   },{
			        				   xtype : 'datefield',
			        				   id : 'sf_weekend' ,
			        				   name : 'SF_WEEKEND',
			        				   fieldLabel : '截止日期',
			        				   value : weeke,
			        				   editable:false
			        			   },{
			        				   xtype : 'monthdatefield',
			        				   id : 'sf_monthend' ,
			        				   name : 'SF_MONTHEND',
			        				   fieldLabel : '截止日期',
			        				   value : monthe,
			        				   editable:false
			        			   },{
			        				   xtype : 'datefield',
			        				   id : 'sf_daystart' ,
			        				   name : 'SF_DAYSTART',
			        				   fieldLabel : '起始日期',
			        				   value : days,
			        				   editable:false
			        			   },{
			        				   xtype : 'datefield',
			        				   id : 'sf_dayend' ,
			        				   name : 'SF_DAYEND',
			        				   fieldLabel : '截止日期',
			        				   value : daye,
			        				   editable:false
			        			   }]    		        			   
			        		   }],buttonAlign: 'center',
				        		buttons: [{
				        			xtype: 'button',
				        			text: '复制',
				        			width: 60,
				        			iconCls: 'x-button-icon-save',
				        			handler: function(btn) {
				        				 var sf_id=Ext.getCmp('sf_id');
					    					if(sf_id.value==''){
					    						return;
					    					}
					    					Ext.Ajax.request({
					    				   		url : basePath + 'scm/sale/copyPreForecast.action',
					    				   		params: {
					    				   			id:sf_id.value,
					    				   			forecast:Ext.getCmp('sf_forecast').value,
					    				   			weeks:Ext.getCmp('sf_weekstart').value,
					    				   			weeke:Ext.getCmp('sf_weekend').value,
					    				   			months:Ext.getCmp('sf_monthstart').value,
					    				   			monthe:Ext.getCmp('sf_monthend').value,
					    				   			days:Ext.getCmp('sf_daystart').value,
					    				   			daye:Ext.getCmp('sf_dayend').value
					    				   			},
					    				   		method : 'post',
					    				   		callback : function(options,success,response){
					    				   			var localJson = new Ext.decode(response.responseText);
					    			    			if(success){
					    			    					//add成功后直接进入可编辑的页面 
					    			    				me.FormUtil.onAdd('SaleForecast!Pre', '业务员人员预测', localJson.log);
					    				   			} else if(localJson.exceptionInfo){
					    				   				var str = localJson.exceptionInfo;
					    				   				showError(str);
					    				   				} 
					    			        		}
					    					});
				        			}
				        		},
				        		{
				        			xtype: 'button',
				        			columnWidth: 0.1,
				        			text: '关闭',
				        			width: 60,
				        			iconCls: 'x-button-icon-close',
				        			margin: '0 0 0 10',
				        			handler: function(btn) {
				        				var win = btn.up('window');
				        				win.close();
				        				win.destroy();
				        			}
				        		}],
						   })	     
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
		}
});