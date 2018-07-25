Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.ProjectRequest', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.request.ProjectRequest','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.ProductType','core.form.MultiField',
			'core.button.TurnPlmMainTask','core.button.TurnProjectTask','core.grid.ItemGrid','core.plugin.NewRowNumberer','core.form.BtnDateField','plm.request.ProjectPanel','core.button.ToMeeting','core.button.CallProcedureByConfig'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpTurnPlmMainTaskButton': {
	                click: function(btn) {
	                    me.PlanMainTask(btn);
	                },
	                afterrender: function(btn) {
	                	var statuscode = Ext.getCmp('prj_auditstatuscode');
	                	var turnstatus = Ext.getCmp('prj_turnstatus');
	                	if(statuscode !=null && turnstatus !=null ){
	                		if (statuscode.value != 'AUDITED'||turnstatus.value==-1) {                    	
		                        btn.hide();
		                    }
	                	}
	                }
	            },
	            'erpTurnProjectTask' :{
	            	click : function(btn){
	            		me.TurnProjectTask(btn);
	            	},
	            	afterrender : function(btn){
	            		var statuscode = Ext.getCmp('prj_preauditcode');
	                	var isturnstatus = Ext.getCmp('prj_isturnpro');
	                	var prstatus = Ext.getCmp('prj_prstatus');
	                	var prjplanid = Ext.getCmp('prj_id');
	                	var bool = false;
	                	Ext.Ajax.request({
	                		url:basePath + 'plm/gantt.action',
	                		method:'post',
	                		async:false,
	                		params:{
	                			condition:'prjplanid='+prjplanid.value
	                		},
	                		callback:function(options,success,response){
	                				var localJson = new Ext.decode(response.responseText);
	                				if(localJson.length<=0){
	                					bool = true;
	                					return;
	                				}
	                				var rebool = false;
	                				Ext.each(localJson,function(l){
	                					findchildnode(l);
	                					function findchildnode(node){
	                			             var childnodes = node.children;
	                			             if(childnodes!=null){
	                			            	 for(var i=0;i<childnodes.length;i++){  //从节点中取出子节点依次遍历
	                			            		 var rootnode = childnodes[i];
	                			            		 if(!rootnode.leaf){  //判断子节点下是否存在子节点
	                			            			 findchildnode(rootnode);    //如果存在子节点  递归
	                			            		 }else{
	                			            			 if(rootnode.handstatus=='已完成'){
	                			            				 bool=true;
	                			            			 }else{
	                			            				 bool=false;
	                			            				 rebool = true;
	                			            				 return false;
	                			            			 }
	                			            		 }
	                			            	 }
	                			             }else{
	                			            	 if(node.handstatus=='已完成'){
	                			            		 bool=true;
	                			            	 }else{
	                			            		 bool=false;
	                			            		 rebool = true;
	                			            		 return false;
	                			            	 }
	                			             }
	                			         }
	                					if(rebool){
	                						return false;
	                					}
	                				})
	                		}
	                	});
	                	if(prstatus.value != '预立项'){
		                	btn.hide();
	                	}else{
	                		if(statuscode !=null && isturnstatus !=null){
		                		if (statuscode.value == 'AUDITED' && isturnstatus.value=='0') {
		                			if(!bool){
		                				btn.hide();
		                			}
			                    }else if(statuscode.value == 'AUDITED' && isturnstatus.value=='1'){
			                    	btn.hide();
			                    }else if(statuscode.value != 'AUDITED'){
			                    	btn.hide();
			                    }
		                	}
	                    }
	            	}
	            },
        		'#ProjectSob': { 
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);
        			}
        		},
        		'#grid': { 
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);
        			}
        		},
        		'#ProjectTeam': { 
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);
    					
        			}
        		},
        		'erpAddButton': {
        			click: function(){
        				if('ProjectRequest'==caller){
        					me.FormUtil.onAdd('ProjectRequest', '新增项目申请单', 'jsps/plm/request/ProjectRequest.jsp?whoami='+caller);
        				}else{
        					me.FormUtil.onAdd('PreProject', '新增预立项任务书', 'jsps/plm/request/ProjectRequest.jsp?whoami='+caller);
        				}	
        			}
        		},
        		'erpSaveButton': {
        			click: function(btn){
        				var form = me.getForm(btn);
        				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
        					me.BaseUtil.getRandomNumber();//自动添加编号
        				}
						var bool = me.checkTime();
						var flag = false;
						me.BaseUtil.getSetting('ProjectRequest', 'checkDate', function(v) {
							if(v){
								flag=true;
							}
						},false);
						if(bool==='small'&&!flag&&'ProjectRequest'==caller){
							showError("计划开始日期不能小于当前日期!");							
						}else if(bool==='over'&&'ProjectRequest'==caller){
							showError("计划结束日期不能小于计划开始日期");
						}else{
							var w = window.frames['gant'];
							if(w!=null&&w!=''){
								var gant = window.frames['gant'].window||window.frames['gant'].contentWindow;
								gant.Ext.getCmp('save').fireEvent('click',gant.Ext.getCmp('save'));
							}
							this.beforeSave(this);
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
        				var form = me.getForm(btn);
        				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
        					me.BaseUtil.getRandomNumber();//自动添加编号
        				}
						var bool = me.checkTime();
						var flag = false;
						me.BaseUtil.getSetting('ProjectRequest', 'checkDate', function(v) {
							if(v){
								flag=true;
							}
						},false);
						if(bool==='small'&&!flag){
							showError("计划开始日期不能小于当前日期!");							
						}else if(bool==='over'){
							showError("计划结束日期不能小于计划开始日期");
						}else{
							var w = window.frames['gant'];
							if(w!=null&&w!=''){
								var gant = window.frames['gant'].window||window.frames['gant'].contentWindow;
								gant.Ext.getCmp('save').fireEvent('click',gant.Ext.getCmp('save'));
							}
							this.beforeUpdate();
						}
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('prj_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				if('PreProject'==caller){
        					var status = Ext.getCmp('prj_preauditcode');
            				if(status && status.value != 'ENTERING'){
            					btn.hide();
            				}
        				}else if('ProjectRequest'==caller){
        					var status = Ext.getCmp('prj_auditstatuscode');
            				if(status && status.value != 'ENTERING'){
            					btn.hide();
            				}
        				}
        			},
        			click: function(btn){
        				this.FormUtil.onSubmit(Ext.getCmp('prj_id').value, true, this.beforeUpdate, this);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				if('PreProject'==caller){
        					var status = Ext.getCmp('prj_preauditcode');
            				if(status && status.value != 'COMMITED'){
            					btn.hide();
            				}
        				}else if('ProjectRequest'==caller){
        					var status = Ext.getCmp('prj_auditstatuscode');
            				if(status && status.value != 'COMMITED'){
            					btn.hide();
            				}
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('prj_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				if('PreProject'==caller){
        					var status = Ext.getCmp('prj_preauditcode');
            				if(status && status.value != 'COMMITED'){
            					btn.hide();
            				}
        				}else if('ProjectRequest'==caller){
        					var status = Ext.getCmp('prj_auditstatuscode');
            				if(status && status.value != 'COMMITED'){
            					btn.hide();
            				}
        				}
        			},
        			click: function(btn){
        				this.TurnTask(btn);
        				me.FormUtil.onAudit(Ext.getCmp('prj_id').value);
        			}
        		},
        		'erpResAuditButton': {
        			afterrender: function(btn){
        				if('PreProject'==caller){
        					var status = Ext.getCmp('prj_preauditcode');
            				if(status && status.value != 'AUDITED'){
            					btn.hide();
            				}
        				}else if('ProjectRequest'==caller){
        					var status = Ext.getCmp('prj_auditstatuscode');
            				if(status && status.value != 'AUDITED'){
            					btn.hide();
            				}
        				}
        				var isturnpro = Ext.getCmp('prj_isturnpro');
        				if(isturnpro!=null && isturnpro.value==-1 && 'PreProject'==caller){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				var isturnpro = Ext.getCmp('prj_isturnpro');
        				if(isturnpro!=null && isturnpro.value==-1 && 'PreProject'==caller){
        					showError("已转正式立项,禁止反审核！");
        					return;
        				}
        				me.FormUtil.onResAudit(Ext.getCmp('prj_id').value);
        			}
        		},
        		'htmleditor[name=prj_sourcecode]':{
	    			   afterrender:function(editor){
	    				   editor.getToolbar().hide();
	    				   editor.readOnly=true;
	    				   var sourceType = Ext.getCmp("prj_sourcetype").value;    				   
	    				   if(sourceType=='需求单'){
	    				  	    editor.setValue('<a style="text-decoration:none;overflow:hidden" href="javascript:parent.openFormUrl(\'' + editor.value + '\',\'pr_code\',\'jsps/plm/request/require.jsp\',\'需求单\''+ ');">' + editor.value + '</a>');	
	    				   }else if(sourceType=='预立项'){
	    				   		editor.setValue('<a style="text-decoration:none;overflow:hidden" href="javascript:parent.openFormUrl(\'' + editor.value + '\',\'pp_code\',\'jsps/plm/request/PreProject.jsp\',\'预立项任务书\''+ ');">' + editor.value + '</a>');
	    				   }
	    				   
	    			   }
	    		},
	    		'erpFormPanel field[name=prj_producttype]':{
	    			   afterrender:function(field){
	    			   	   field.setEditable(false);
	    			   }
	    		},
	    		'erpToMeetingButton':{
	    			afterrender:function(btn){
	    				var prj_preauditcode = Ext.getCmp('prj_preauditcode');
	    				if(!(caller=='PreProject'&&prj_preauditcode&&prj_preauditcode.value=='AUDITED')){
	    					btn.hide();
	    				}
	    			}
	    		},
	    		'erpFormPanel field[name=prj_mainprocode]':{
	    			change:function(field){
	    				var data = field.getValue();
	    				Ext.Ajax.request({
	    					url:basePath+'plm/request/setMainProjectRule.action',
	    					params:{
	    						maincode:data
	    					},
	    					method:'post',
	    					asycn:false,
	    					callback:function(options,success,respons){
	    						var res = Ext.decode(respons.responseText);
	    	    				if(res.success){
	    	    					if(res.result<0){
	    	    						showError("禁止选择的主项目是另一个项目的子项目！");
	    	    						Ext.getCmp('prj_mainprocode').setValue('');
	    	    						Ext.getCmp('prj_mainproname').setValue('');
	    	    						Ext.getCmp('prj_mainproid').setValue('');
	    	    					}
	    	    				}else if(res.exceptionInfo){
	    	       					showError(res.exceptionInfo);
	    	       				}else{
	    	       					showError('未知错误');
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
       checkTime: function(){
    	   var date = new Date();
    	   var start=Ext.getCmp('prj_start').value;
    	   var end=Ext.getCmp('prj_end').value;
    	   var extCurrentDate = Ext.Date.format(new Date,'Y-m-d');
    	   var extStart = Ext.Date.format(start,'Y-m-d');
    	   if(extStart<extCurrentDate){
    		   return 'small';
    	   }		
    	   if(start>end){
    		   return 'over';
    	   }
    	   return true;
      },
      PlanMainTask: function(btn) {
        var form = btn.ownerCt.ownerCt;
        var id = Ext.getCmp('prj_id').getValue();
        Ext.Ajax.request({
            url: basePath + form.planTaskUrl,
            params: {
                id: id
            },
            method: 'post',
            callback: function(options, success, response) {
                var localJson = new Ext.decode(response.responseText);
                if (localJson.success) {
                    Ext.Msg.alert('提示', '下达研发任务书成功!', window.location.reload());
                } else {
                    if (localJson.exceptionInfo) {
                        var str = localJson.exceptionInfo;
                        if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
                            str = str.replace('AFTERSUCCESS', '');
                            submitSuccess(function() {
                                window.location.reload();
                            });
                        }
                        showMessage("提示", str);
                        return;
                    }
                }
            }
        });
    },
    TurnProjectTask : function(btn){
    	var form = btn.ownerCt.ownerCt;
    	var id = Ext.getCmp('prj_id').getValue();
    	Ext.Ajax.request({
    		url : basePath + form.turnProject,
    		params : {
    			id:id
    		},
    		method:'post',
    		callback : function(options, success, response){
    			 var localJson = new Ext.decode(response.responseText);
                 if (localJson.success) {
                     Ext.Msg.alert('提示', '转正式立项成功!', window.location.reload());
                 }else{
                	 Ext.Msg.alert('提示', '转正式立项失败!', window.location.reload());
                 }
    		}
    	});
    },
    beforeUpdate: function(me, ignoreWarn, opts, extra){
    	var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var params1 = new Array();	
		var params2 = new Array();
		var params3 = new Array();
		var data = Ext.getCmp('grid').getStore().data.items;
		for(var i=0;i<data.length;i++){
			data[i].dirty=true;
		}
		var data1 = me.GridUtil.getGridStore(Ext.getCmp('grid'));
		params1[0] = data1 == null ? [] : "[" + data1.toString().replace(/\\/g,"%") + "]";
		var data2 = me.GridUtil.getGridStore(Ext.getCmp('ProjectSob'));
		params2[0] = data2 == null ? [] : "[" + data2.toString().replace(/\\/g,"%") + "]";
		var data3 = me.GridUtil.getGridStore(Ext.getCmp('ProjectTeam'));
		params3[0] = data3 == null ? [] : "[" + data3.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){					
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!me.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.update(r,params1,params2,params3);
		}else{
			me.FormUtil.checkForm();
		}
		
	},
	update:function(r,params1,params2,params3){
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		var formStore = unescape(escape(Ext.JSON.encode(r)));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : {
	   			params1:unescape(params1.toString()),params2:unescape(params2.toString()),
	   			params3:unescape(params3.toString()),formStore:formStore
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){var localJson = new Ext.decode(response.responseText);
   			if(localJson.success){
				updateSuccess(function(){
	   				var value = r[form.keyField];
	   		    	var formCondition = form.keyField + "IS" + value ;
	   		    	if(me.contains(window.location.href, '?', true)){
		   		    	window.location.href = window.location.href.split('?')[0] +'?whoami='+caller +'&formCondition=' + 
		   					formCondition+'&gridCondition=IS'+value;
		   		    } else {
		   		    	window.location.href = window.location.href + '?whoami='+caller+'&formCondition=' + 
		   					formCondition+'&gridCondition=IS'+value;
		   		    }
				});
   			} else if(localJson.exceptionInfo){
   				var str = localJson.exceptionInfo;
   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
   					str = str.replace('AFTERSUCCESS', '');
   					updateSuccess(function(){
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;

		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   		    	formCondition+'&gridCondition=IS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   		    	formCondition+'&gridCondition=IS'+value;
			   		    }
    				});
   					showError(str);
   				} else {
   					showError(str);
	   				return;
   				}
   			} else{
   				saveFailure();
   			}}  		
		});
	},
    contains: function(string,substr,isIgnoreCase){
		    if(isIgnoreCase){
		    	string=string.toLowerCase();
		    	substr=substr.toLowerCase();
		    }
		    var startChar=substr.substring(0,1);
		    var strLen=substr.length;
		    for(var j=0;j<string.length-strLen+1;j++){
		    	if(string.charAt(j)==startChar){
		    		if(string.substring(j,j+strLen)==substr){
		    			return true;
		    			}   
		    		}
		    	}
		    return false;
	},
	beforeSave: function(me, ignoreWarn, opts, extra){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		
		var params1 = new Array();
		var params2 = new Array();
		var params3 = new Array();
		var data = Ext.getCmp('grid').getStore().data.items;
		for(var i=0;i<data.length;i++){
			data[i].dirty=true;
		}
		var data1 = me.GridUtil.getGridStore(Ext.getCmp('grid'));
		params1[0] = data1 == null ? [] : "[" + data1.toString().replace(/\\/g,"%") + "]";
		var data2 = me.GridUtil.getGridStore(Ext.getCmp('ProjectSob'));
		params2[0] = data2 == null ? [] : "[" + data2.toString().replace(/\\/g,"%") + "]";
		var data3 = me.GridUtil.getGridStore(Ext.getCmp('ProjectTeam'));
		params3[0] = data3 == null ? [] : "[" + data3.toString().replace(/\\/g,"%") + "]";
		
		if(form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){					
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!me.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.save(r, params1,params2,params3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	save: function(r,params1,params2,params3){
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
    	var formStore = unescape(escape(Ext.JSON.encode(r)));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params :{
	   			params1:unescape(params1.toString()),params2:unescape(params2.toString()),
	   			params3:unescape(params3.toString()),formStore:formStore,caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition+'&gridCondition=IS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=IS'+value;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;

			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   		    	formCondition+'&gridCondition=IS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=IS'+value;
				   		    }
	    				});
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
	    },
	    TurnTask:function(btn){
	    	   var id=Ext.getCmp('prj_id').getValue();
	    	   Ext.Ajax.request({
	    		   url:basePath+'plm/gantt/getPreTask.action',
	    		   params:{
	    			   id:id
	    		   },
	    		   method:'post',
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','任务节点激活成功!',function(){window.location.reload();});
	    			   }else {
    					   if(localJson.exceptionInfo){
    						   var str = localJson.exceptionInfo;
    						   if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    							   str = str.replace('AFTERSUCCESS', '');
    							   submitSuccess(function(){
    								   window.location.reload();
    							   });
    						   }
    						   showMessage("提示", str);return;
    					   }
    				   }
	    		   }
	    	   });
	       }
});