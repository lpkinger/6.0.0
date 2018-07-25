Ext.QuickTips.init();
Ext.define('erp.controller.plm.record.WorkRecord', {
	extend: 'Ext.app.Controller',
    FTPfilefield : Ext.create('erp.view.core.form.FTPFileField'),//加载FTP上传组件
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.record.RecordForm','plm.record.WorkRecord','core.grid.Panel2','core.toolbar.Toolbar','core.button.Upload','core.button.DownLoad',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.ResSubmit',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.form.YnField','core.form.HrefField',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.FileField','core.form.MultiField','core.button.Start','core.button.Stop','plm.record.RelationPanel','core.form.FTPFileField'
	       ],
	       init:function(){
	    	   var me=this,statuscode=null,haspretask=false;
	    	   this.control({ 
	    	   	   'erpRecordPanel':{
	    	   	   		afterrender:function(form){
	    	   	   			form.upload = function(){
	    	   	   				var input = document.getElementsByName('file');
	    	   	   				input[1].click();
	    	   	   			};
	    	   	   			form.ftpupload = function(){
	    	   	   				var input = document.getElementsByName('file');
	    	   	   				input[0].click();
	    	   	   			};
	    	   	   		},
	    	   	   		afterload:function(form){
	    	   	   			var id = Ext.getCmp('wr_taskid').value;
	    	   				if(id){
		    	   				//获取任务所需的文件
		    	   	   			Ext.Ajax.request({
		    	   	   				url:basePath + 'plm/record/getTaskFiles.action',
		    	   	   				method:'post',
		    	   	   				params:{
		    	   	   					id:id
		    	   	   				},
		    	   	   				callback:function(options,success,response){
		    	   	   					var res = Ext.decode(response.responseText);
		    	   	   					if(res.success){
		    	   	   						if(res.files.length>0){
		    	   	   							me.addFileGrid(res.files);
		    	   	   						}
		    	   	   					}
		    	   	   				}
		    	   	   			});
	    	   				}
	    	   	   		}
	    	   	   },
	    	   	   'erpGridPanel2':{
	    	   	   		reconfigure:function(grid){
							var totalrate = Ext.getCmp('totalrate');
							if(totalrate){
								var count = 0;
								if(grid.store.data.items.length>0){
									Ext.Array.each(grid.store.data.items,function(item,index){
										count += item.data.wr_percentdone;								
									});								
								}
								totalrate.setValue(count);								
							}
							
							var formCon = getUrlParam('formCondition');
							//判断是否是从审批流界面点进来
							if(formCon.indexOf('wr_id')>-1){
								var wr_id = formCon.substring(formCon.indexOf('IS')+2,formCon.length);
								var data = null;
								Ext.Array.each(grid.store.data.items,function(item,index){
									if(item.data.wr_id==wr_id){
										data = item.data;
										return false;
									}
								});
								
								if(data){
									me.setPercentValue(data.wr_percentdone,data.wr_redcord);
								}

							}
	    	   	   		}
	    	   	   },
	    	   	   'relationPanel':{
	    	   		activate:function(panel,obj){
	    	   			var store=panel.store;
	    	   			var id = Ext.getCmp('wr_taskid').value;
	    	   			if(id){
	    	   				Ext.Ajax.request({
	    	   					url:basePath+'plm/record/loadRelationData.action',
	    	   					method:'post',
	    	   					params:{id:id},
	    	   					callback:function(opts,suc,res){
	    	   						var res=Ext.decode(res.responseText);
	    	   						if(res.exceptionInfo){
	    	   							showError(res.exceptionInfo);
	    	   							return;
	    	   						}
	    	   						if(res.success){
	    	   							var data=res.data;
	    	   							store.loadData(data);
	    	   						}
	    	   					}
	    	   				});
	    	   			}
	    	   		}
	    	   	   },
	    		   'erpSaveButton': {
	    			   afterrender: function(btn){
	    				   var wr_percentdone=Ext.getCmp('wr_percentdone');
	    				   var wr_taskpercentdone = Ext.getCmp('wr_taskpercentdone');
	    				   var tasktype=Ext.getCmp('tasktype').getValue();
	    				   /*
	    				    * 取消测试任务保存按钮不显示*/
	    				   if((wr_percentdone.value!=null && wr_percentdone.value!='') || tasktype=='milestone'){
	    					   btn.hide();
	    				   }

	    				   if(wr_taskpercentdone){
	    				   	   if(wr_taskpercentdone.value>=100){
	    				   	   	   btn.hide();
	    				   	   }
	    				   }
	    				   
	    				   //未激活的任务按钮隐藏
	    				   var taskstatuscode = Ext.getCmp('ra_statuscode');
	    				   var status = Ext.getCmp('statuscode');
	    				   if(taskstatuscode){
	    				       if(taskstatuscode.value=='UNACTIVE'){
	    				       	   btn.hide();
	    				       }
	    				   }    				   
	    				   if(status){
	    				   	   if(status.value=='STOP'){
	    				   	   	   btn.hide();
	    				   	   }else if(status.value=='FINISHED'){
	    				   	   	   btn.hide();
	    				   	   }
	    				   }
	    				   
	    			   },
	    			   click: function(btn){     			      			
	    				   this.save(btn);
	    			   }
	    		   },
	    		   'numberfield[name=wr_percentdone]':{
	    			   afterrender:function(editor){
	    				   editor.decimalPrecision=2;
	    				   editor.allowDecimals;
	    			   }
	    		   },
	    		   'textfield[name=wr_percentdone]': {
	    			   change: function(field){			
	    				   if(field.value>100){
	    					   showError('提交完成率不能大于100');
	    					   field.reset();
	    				   }	
	    			   }
	    		   },
	    		   'htmleditor[name=ptid]':{
	    			   afterrender:function(editor){
	    				   editor.getToolbar().hide();
	    				   editor.readOnly=true;
	    				   var url='jsps/plm/task/projectmaintask.jsp?formCondition=pt_idIS'+editor.value+'&gridCondition=ptidIS'+editor.value;
	    				   editor.setValue('<a style="text-decoration:none;" href="javascript:parent.openUrl(' +'\''+url + '\');">查看任务书</a>');

	    			   }
	    		   },
	    		   'erpStartButton':{
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('ra_statuscode').getValue();    				
	    				   if(statuscode!='STOP'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   me.startTask();
	    			   }
	    		   },
	    		   'erpStopButton':{
	    			   afterrender:function(btn){  
	    				   statuscode=Ext.getCmp('ra_statuscode').getValue();
	    				   if(statuscode!='START'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(){
	    				   me.stopTask();
	    			   }
	    		   },
	    		   'erpUpdateButton':{
	    			   afterrender: function(btn){
	    				   var wr_percentdone = Ext.getCmp('wr_percentdone');
	    				   var wr_taskpercentdone=Ext.getCmp('wr_taskpercentdone');
	    				   if(wr_percentdone.value==0||wr_taskpercentdone.value==100){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   var updatetimes=Ext.getCmp('wr_updatetimes');
	    				   var grid =Ext.getCmp('grid');
	    				   var items=grid.getStore().data.items;
	    				   if(items.length>0){
	    					   var percentdone=Ext.getCmp('wr_percentdone');
	    					   var count=0;
	    					   for(var i=1;i<items.length;i++){
	    						   count+=items[i].data.wr_percentdone;    
	    					   }
	    					   if(percentdone.value>(100-count)){
	    						   showError('你已提交'+count+"更新不能大于"+(100-count)+'请重新输入');
	    						   percentdone.reset();
	    						   return;
	    					   }
	    				   }
	    				   if(updatetimes.value==0){
	    					   showMessage('提示','你的修改次数为0');
	    					   return;
	    				   }else{
	    					   var value=parseInt(updatetimes.value)-1;
	    					   updatetimes.setValue(value);
	    					   showMessage('提示','你还能修改'+value+'次');
	    				   }
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },	
	    		   'erpSubmitButton':{
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('ra_statuscode').value;
	    				   if(statuscode&&statuscode!='RESACTIVE'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(){
	    				   me.SubmitRecord(Ext.getCmp('wr_taskid').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   afterrender:function(btn){
	    				   var pretaskdetno=Ext.getCmp('pretaskdetno').value;
	    				   haspretask=(pretaskdetno&&pretaskdetno!=null&&pretaskdetno!="null")?true:false;    				
	    				   if(!haspretask) btn.hide(); 
	    				   var statuscode=Ext.getCmp('ra_statuscode').value;
	    				   if(statuscode!='START') btn.hide();
	    			   },
	    			   click:function (btn){
	    				   me.resSubmitRecord(Ext.getCmp('wr_taskid').value);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   afterrender: function(btn){
	    				   var field=Ext.getCmp('wr_taskpercentdone'),progress=Ext.getCmp('wr_progress'),value;
	    				   if(field && progress){
	    				   	  value=field.getValue();
	    				   	  progress.updateProgress(value/100,'当前任务进度:'+value+'%'); 
	    				   }			
	    			   },  
	    			   click: function(btn){
	    				   Ext.getCmp('wr_redcord').originalValue=Ext.getCmp('wr_redcord').getValue();
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   }
	    	   });
	       },
	       setPercentValue:function(percentdone,record){
	            var me = this;
				Ext.defer(function(){
					var wr_percentdone = Ext.getCmp('wr_percentdone');
					var wr_record = Ext.getCmp('wr_redcord');
					if(wr_percentdone){
						wr_percentdone.setValue(percentdone);
					}
					if(wr_record){
						wr_record.setValue(record);
					}
				},300);	           
	       },
	       addFileGrid:function(data){
	    	   var me =this;
	       		var form = Ext.getCmp('form');
	           var ftpfileaddform={
  	   					columnWidth:0,
	                 	id:'fileform',
	                 	xtype:'form',
	    	        	layout:'column',
	    	        	hidden:true,
	    	        	bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
				  		items: [{
						xtype : 'filefield',
						name : 'file',
						buttonOnly : true,
						hideLabel : true,
						id : 'ftpfile',
						buttonConfig : {
							iconCls : 'x-button-icon-pic',
							text : '上传附件',
							cls : 'x-btn-gray',
							id:'uploadbtn'
						},
						listeners : {
							change : function(field) {
								var record = Ext.getCmp('filegrid').selModel.lastSelected;
								var filename = '';
								if (contains(field.value, "\\", true)) {
									filename = field.value.substring(field.value
											.lastIndexOf('\\')
											+ 1);
								} else {
									filename = field.value.substring(field.value
											.lastIndexOf('/')
											+ 1);
								}
								var sf_id =10
								var sfd_id =10
								field.ownerCt.getForm().submit({
									url: basePath + 'common/uploadFTP.action?em_code=' + em_code+'&sf_id='+sf_id+'&sfd_id='+sfd_id,
									waitMsg: "正在上传:" + filename,
									success: function(fp, o){
										if(o.result.error){
											showError(o.result.error);
										} else {
											Ext.Msg.alert("恭喜", filename + " 上传成功!");
											var record = Ext.getCmp('filegrid').selModel.lastSelected;
											if (record) {
												record.set('ptt_filepath',filename+";"+o.result.filepath);
												record.dirty = true;
											}}
									}
								});
/*								var grid = Ext.getCmp('FTPFileFieldtest');
								grid.upload(field.ownerCt, field);*/
							}
						
						}
				  		}]									
	           };
   	   			var fileaddform = {
   	   					columnWidth:0,
	                 	id:'fileform',
	                 	xtype:'form',
	    	        	layout:'column',
	    	        	//renderTo: Ext.getBody(),
	    	        	hidden:true,
	    	        	bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
				  		items: [{
						xtype : 'filefield',
						name : 'file',
						buttonOnly : true,
						hideLabel : true,
						id : 'fileadd',
						buttonConfig : {
							iconCls : 'x-button-icon-pic',
							text : '上传附件',
							cls : 'x-btn-gray',
							id:'uploadbtn'
						},
						listeners : {
							change : function(field) {
								var record = Ext.getCmp('filegrid').selModel.lastSelected;

								var filename = '';
								if (contains(field.value, "\\", true)) {
									filename = field.value.substring(field.value
											.lastIndexOf('\\')
											+ 1);
								} else {
									filename = field.value.substring(field.value
											.lastIndexOf('/')
											+ 1);
								}
								field.ownerCt.getForm().submit({
									url : basePath + 'common/upload.action?em_code=' + em_code,
									waitMsg : "正在解析文件信息",
									success : function(fp, o) {
										if (o.result.error) {
											showError(o.result.error);
										} else {
											Ext.Msg.alert("恭喜", filename + " 上传成功!");
											var record = Ext.getCmp('filegrid').selModel.lastSelected;
											if (record) {
												record.set('ptt_filepath',filename+";"+o.result.filepath);
												record.dirty = true;
											}
										}
									}
								});
							}
						}
				  		}]					
					
   	   			};
   	   			
				var grid = {
					xtype:'grid',
					fieldLabel:'test',
					columnWidth:0.75,
					id : 'filegrid',
					columnLines : true,
					readOnly : false,
					layout:'column',
					plugins: [
		                Ext.create('Ext.grid.plugin.CellEditing', {
		                    clicksToEdit: 1
		                })
		            ],
					columns : [{
						header : 'ID',
						dataIndex : 'ptt_id',
						flex : 0,
						hidden : true
					},{
						header : '文件要求',
						dataIndex : 'ptt_filename',
						flex : 0.4,
						hidden : false,
						renderer:function(val,meta,record){
							return '<span style="color:red">' + val + '</span>';
						},
						style:'text-align:center'
					},{
						header : '附件',
						dataIndex : 'ptt_filepath',
						flex : 0.4,
						hidden : false,
						style:'text-align:center',
						renderer:function(val, meta, record, x, y, store, view){		
							if(record&&record.data["ptt_filepath"]!=null&&record.data["ptt_filepath"]!=""){		
								var attach=record.data["ptt_filepath"];
								var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
									if(record.data[field] != attach){
									record.set(field,attach);
								}
								return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + attach.split(";")[0] + '</span>'+'<a href="' + basePath + 'common/downloadbyId.action?id='+attach.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
							}else if(record&&val!=null&&val!=""){
							return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + val.split(";")[0] + '</span>'+'<a href="' + basePath + 'common/downloadbyId.action?id='+val.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
							}else return val;
						}
					},{
						header : '',
						flex : 0.2,
						renderer:function(val,meta){
							meta.style="text-align:center!important";
							return '<div onclick="upload()"><img src="' + basePath + 'resource/images/icon/upload.png"></img>上传文件</div>';
						}
					},
					{
						header : '',
						flex : 0.2,
						renderer:function(val,meta){
							if (Ext.getCmp('wr_isftp')!= null){
							var id = Ext.getCmp('wr_isftp').value;
							if (id==1){
							meta.style="text-align:center!important";
							return '<div onclick="ftpupload()"><img src="' + basePath + 'resource/images/icon/upload.png"></img>ftp上传文件</div>';
							}
							}
							}
					}
					],
					store:Ext.create('Ext.data.Store',{
						fields : ['ptt_id','ptt_filename','ptt_filepath'],
						data:data
					}),
					dockedItems : [{
						xtype : 'toolbar',
						dock : 'top',
						layout : {
							pack : 'left'
						},
						items : [{
							xtype : 'tbtext',
							text : '<span style="font-weight:bold">任务文件信息</span>' + '<span style="color:red">(任务完成时，请提交对应的任务附件!)</span>',
							id : 'toolbartext'
						}]
						}]
					};
				form.add(grid);
				form.add(fileaddform);	       	
				form.add(ftpfileaddform);
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   this.gridLastSelected = record;
	    	   var grid = Ext.getCmp('grid');
	    	   if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
	    		   this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
	    	   } else {
	    		   this.gridLastSelected.findable = false;
	    	   }
	    	   this.GridUtil.onGridItemClick(selModel, record);
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){
	    	   var me = this;
	    	   var grid =Ext.getCmp('grid');
	    	   var filegrid = Ext.getCmp('filegrid');
	    	   var percentdone=Ext.getCmp('wr_percentdone');
	    	   var count=0;
	    	   var items=grid.getStore().data.items;
	    	   Ext.Array.each(items,function(item){   		      
	    		   count+=item.data.wr_percentdone;

	    	   });
	    	   if(percentdone.value>(100-count)){
	    		   showError('你已提交过'+count+"  再提交不能超过"+(100-count)+'请重新输入');
	    		   percentdone.reset();
	    		   return;
	    	   }	
	    	   
	    	   if((count + percentdone.value)==100){
	    	   	   var flag = false;
	    	       if(filegrid){
	    	       	   	Ext.Array.each(filegrid.store.data.items,function(item,index){
							if(!item.data.ptt_filepath){
								flag = true;
							}
	    	   			});
	    	       }
	    	       if(flag){
	    	       	   showError('有文件未上传,请上传后再进行保存');
	    	       	   return;
	    	       }
	    	   }
	    	   
	    	   var description=Ext.getCmp('wr_redcord').getValue();
	    	   description=description.replace(/:/g, '：').replace(/"/g,"");
	    	   Ext.getCmp('wr_redcord').setValue(description);
	    	   var des=description.replace(/&nbsp;/g,"").replace(/ /g,"");
	    	  /* if(des.length<10){
	    		   showError('描述太短了!');
	    		   return;
	    	   }*/
	    	   if(description.length>500){
	    		   showError('请控制描述字节长度');
	    		   return;
	    	   }
	    	   
	    	   //保存文档grid
	    	   var updateArr = new Array();
	    	   if(filegrid){
	    	   		if(filegrid.store.data.items.length>0){  	   			
	    	   			Ext.Array.each(filegrid.store.data.items,function(item,index){
							if(item.dirty){
								updateArr.push(item.data);	
							}
	    	   			});
	    	   		}
	    	   		
	    	   }

	    	   //重写FormUtil的submit方法
	    	   me.FormUtil.submit = function(id){
					var me = this;
					var form = Ext.getCmp('form');
					if(!me.contains(form.submitUrl, '?caller=', true)){
						form.submitUrl = form.submitUrl + "?caller=" + caller;
					}
					me.setLoading(true);//loading...
					Ext.Ajax.request({
						url : basePath + 'plm/record/SubmitWorkRecordFlow.action',
						params: {
							id: id
						},
						method : 'post',
						callback : function(options,success,response){
							me.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								me.getMultiAssigns(id, caller,form);
							} else {
								if(localJson.exceptionInfo){
									var str = localJson.exceptionInfo;
									if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
										str = str.replace('AFTERSUCCESS', '');
										me.getMultiAssigns(id, caller, form,me.showAssignWin);
									} 
									showMessage("提示", str);
								}
							}
						}
					});			   
			   };
	    	   //重写FormUtil的save方法
	    	   me.FormUtil.save = function(){
					var params = new Object();
					var r = arguments[0];
					Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
						if(contains(k, '-', true) && !contains(k,'-new',true)){
							delete r[k];
						}
					});
					r.fileStore = Ext.encode(updateArr);
					params.formStore = unescape(escape(Ext.JSON.encode(r)));
					params.param = unescape(arguments[1].toString());
					for(var i=2; i<arguments.length; i++) {  //兼容多参数
						if(arguments[i])
							params['param' + i] = unescape(arguments[i].toString());
					}  
					var me = this;
					var form = Ext.getCmp('form'), url = form.saveUrl;
					if(url.indexOf('caller=') == -1){
						url = url + "?caller=" + caller;
					}
					Ext.Ajax.request({
						url : basePath + url,
						aysnc:false,
						params : params,
						method : 'post',
						callback : function(options,success,response){
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								//执行提交动作
								me.submit(Ext.getCmp('wr_id').value);								
							}else if(localJson.exceptionInfo){
								var str = localJson.exceptionInfo;
								if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
									str = str.replace('AFTERSUCCESS', '');
									showError(str);
								} else {
									showError(str);
									return;
								}
							} else{
								saveFailure();//@i18n/i18n.js
							}
						}
			
					});
	
	    	   };
	    	   me.FormUtil.beforeSave(me);
	       },
	       changeGrid: function(trigger){
	    	   var grid = Ext.getCmp('grid');
	    	   Ext.Array.each(grid.store.data.items, function(item){
	    		   item.set('tm_prjid',trigger.value);
	    	   });
	       },
	       startTask:function(){
	    	   var id=Ext.getCmp('wr_raid').getValue();
	    	   Ext.Ajax.request({
	    		   url : basePath + 'plm/record/start.action',
	    		   params: {
	    			   id:id
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','开始成功!',function(){
	    					   window.location.reload();
	    				   });
	    			   }else {
	    				   var str = localJson.exceptionInfo;
	    				   showError(str);return;
	    			   }
	    		   }
	    	   });			
	       },
	       stopTask:function(){
	    	   var id=Ext.getCmp('wr_raid').getValue();
	    	   Ext.Ajax.request({
	    		   url : basePath + 'plm/record/stop.action',
	    		   params: {
	    			   id:id
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','暂停成功!',function(){
	    					   window.location.reload();
	    				   });
	    			   }else {
	    				   var str = localJson.exceptionInfo;
	    				   showError(str);return;
	    			   }
	    		   }
	    	   });			
	       },
	       SubmitRecord:function(id){
	    	   var form=Ext.getCmp('form');
	    	   form.FormUtil.setLoading(true);
	    	   Ext.Ajax.request({
	    		   url : basePath + 'plm/record/SubmitRecord.action',
	    		   params: {
	    			   id:id
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   form.FormUtil.setLoading(false);
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','提交成功!',function(){
	    					   window.location.reload();
	    				   });
	    			   }else {
	    				   var str = localJson.exceptionInfo;
	    				   showError(str);return;
	    			   }
	    		   }
	    	   });			
	       },
	       resSubmitRecord:function(id){
	    	   Ext.MessageBox.confirm('提示','反提交是前置任务未完成需重新激活启动上一层任务</br>是否重新启动上一层任务',function(btn){
	    		   if(btn=='yes'){
			    	   var form=Ext.getCmp('form');
			    	   form.FormUtil.setLoading(true);
			    	   Ext.Ajax.request({
			    		   url : basePath + 'plm/record/resSubmitRecord.action',
			    		   params: {
			    			   id:id
			    		   },
			    		   method : 'post',
			    		   callback : function(options,success,response){
			    			   form.FormUtil.setLoading(false);
			    			   var localJson = new Ext.decode(response.responseText);
			    			   if(localJson.success){
			    				   Ext.Msg.alert('提示','反提交成功!',function(){
			    					   window.location.reload();
			    				   });
			    			   }else {
			    				   var str = localJson.exceptionInfo;
			    				   showError(str);return;
			    			   }
			    		   }
			    	   });
	    		   }
	    	   });
	       } 
});