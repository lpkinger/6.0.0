Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerBase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.CustomerBase','core.form.Panel','core.form.MultiField','core.button.HandleHang','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Banned','core.button.ResBanned','core.button.Sync','core.button.CustomerUU',
  			'core.button.Hung','core.button.ReHung','core.form.CascadingCityField','core.button.RegB2B',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.SendEdi','core.button.CancelEdi',
  			'core.button.RelativeCustomer','core.form.SeparNumber','core.button.Modify','core.trigger.AddDbfindTrigger','core.trigger.MultiDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
      		'erpFormPanel':{
      			afterload:function(form){
      				//用updatedate来判断是否是刚录入的
      				var cuCode = form.getComponent("cu_code");
      				//如果是从商机转过来的客户
      				if(bc_code!='null'&&typeof(cuCode)!="undefined"&&cuCode.value==null){
      					Ext.Ajax.request({
      						url:basePath + "mobile/common/getPanel.action?caller=BusinessChance&formCondition=bc_code=" + bc_code,
      						method:'post',
      						callback:function(options,successs,response){
      							var res = new Ext.decode(response.responseText);
      							
      							if(res.panelData){
      		      					var cu_add1 = form.getComponent("cu_add1");
      		      					var cu_contact = form.getComponent("cu_contact");
      		      					var cu_degree = form.getComponent("cu_degree");
      		      					var cu_tel = form.getComponent("cu_tel");
      		      					var cu_name = form.getComponent("cu_name");
      		      					
      		      					cu_name.setValue(res.panelData.bc_custname);
      		      					cu_add1.setValue(res.panelData.bc_address);
      		      					cu_contact.setValue(res.panelData.bc_contact);	
      		      					cu_degree.setValue(res.panelData.bc_position);	
      		      					cu_tel.setValue(res.panelData.bc_tel);
      							}
      						}
      					});
      					
      				}
      			}
      		},
      		'erpRelativeCustomerButton' : {
				beforerender : function(btn){
					var flag = getUrlParam('flag');
					var uu = getUrlParam('uu');
					if(!flag){
						btn.hide();
					}
					if(uu){
						Ext.getCmp('cu_uu').setValue(uu);
					}
				}
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn),
						codeField = Ext.getCmp(form.codeField), kind =Ext.getCmp('cu_kind').value;
					if(codeField.value == null || codeField.value == ''){
						if(!Ext.isEmpty(kind)){
							var res = me.getCode(kind);
							if(res != null && res != ''){
								codeField.setValue(res);
							} else {
								me.BaseUtil.getRandomNumber(null,10,null);//自动添加编号
							}
						} else {
							me.BaseUtil.getRandomNumber(null,10,null);//自动添加编号
						}
					}
					var cust = Ext.getCmp('cu_code').value, custname = Ext.getCmp('cu_name').value, 
					shcustcode = Ext.getCmp('cu_shcustcode').value, arcode=Ext.getCmp('cu_arcode').value;
					cust = cust != null ? cust.toUpperCase() : null;
					if(shcustcode == null || shcustcode == ''){
						Ext.getCmp('cu_shcustcode').setValue(cust);
						Ext.getCmp('cu_shcustname').setValue(custname);
					}	
					if(arcode == null || arcode == ''){
						Ext.getCmp('cu_arcode').setValue(cust);
						Ext.getCmp('cu_arname').setValue(custname);
					}	
					this.FormUtil.beforeSave(this);
					
					var cuCode = form.getComponent("cu_code");
					var cuName = form.getComponent("cu_name");
					
					Ext.Ajax.request({
						url:basePath + "mobile/crm/updateBusinessChanceCust.action?bc_code=" + bc_code + "&cu_code=" + cuCode.value + "&cu_name=" + cuName.value,
						method:'post',
						callback:function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								Ext.Msg.alert("提示",res.exceptionInfo);
							}
						}
					});
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('cu_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					var cust = Ext.getCmp('cu_code').value, custname = Ext.getCmp('cu_name').value, 
					shcustcode = Ext.getCmp('cu_shcustcode').value, arcode=Ext.getCmp('cu_arcode').value;
					if(shcustcode == null || shcustcode == ''){
						Ext.getCmp('cu_shcustcode').setValue(cust);
						Ext.getCmp('cu_shcustname').setValue(custname);
					}	
					if(arcode == null || arcode == ''){
						Ext.getCmp('cu_arcode').setValue(cust);
						Ext.getCmp('cu_arname').setValue(custname);
					}	
					var id = Ext.getCmp('cu_id').value;
					if(id == null || id == '' || id == '0' || id == 0){
						this.FormUtil.beforeSave(this);
					} else {
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustomerBase', '新增客户基础资料', 'jsps/scm/sale/customerBase.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('cu_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('cu_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('cu_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('cu_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('cu_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && (status.value == 'BANNED' || status.value == 'DISABLE')){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpHungButton': {
				afterrender: function(btn){
					var auditstatus = Ext.getCmp('cu_auditstatuscode');
					var status = Ext.getCmp('cu_statuscode');
					if(auditstatus && auditstatus.value != 'AUDITED'||status && status.value=='HUNG'){
						btn.hide();
					}
				},
    			click: function(btn){
    				warnMsg("确定要挂起吗?", function(btn){
    					if(btn == 'yes'){
			    			Ext.Ajax.request({
			     			    url : basePath + 'scm/customer/hungCustomer.action',
			     			    params: {
			     			        id: Ext.getCmp('cu_id').value,
			     			        caller : caller
			     			    },
			     			    method : 'post',
			     			    callback : function(options,success,response){
			     			   		var localJson = new Ext.decode(response.responseText);
			     			   		if(localJson.success){
			     			   			Ext.Msg.alert("提示","操作成功！");
			     			   			window.location.reload();
			     			   		} else {
			     			   			if(localJson.exceptionInfo){
				     			   			var str = localJson.exceptionInfo;
				     			   			if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
				     			   				str = str.replace('AFTERSUCCESS', '');
				     			   				showMessage('提示', str);
				     			   				window.location.reload();
				     			   			} else if(str == 'OK'){
				     			   				Ext.Msg.alert("提示","挂起成功！");
				     			   			} else {
				     			   				showError(str);return;
				     			   			}
			     			   			}
			     			   		}
			     			   	}
			    			});	 
    					}
    				});
    			}
    		},
    		'erpReHungButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('cu_statuscode');
					if(status && status.value != 'HUNG'){
						btn.hide();
					}
				},
    			click: function(btn){
    				warnMsg("确定要解挂吗?", function(btn){
    					if(btn == 'yes'){
			    			Ext.Ajax.request({
			     			    url : basePath + 'scm/customer/reHungCustomer.action',
			     			    params: {
			     			        id: Ext.getCmp('cu_id').value,
			     			        caller : caller
			     			    },
			     			    method : 'post',
			     			    callback : function(options,success,response){
			     			   		var localJson = new Ext.decode(response.responseText);
			     			   		if(localJson.success){
			     			   			Ext.Msg.alert("提示","操作成功！");
			     			   			window.location.reload();
			     			   		} else {
			     			   			if(localJson.exceptionInfo){
				     			   			var str = localJson.exceptionInfo;
				     			   			if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
				     			   				str = str.replace('AFTERSUCCESS', '');
				     			   				showMessage('提示', str);
				     			   				window.location.reload();
				     			   			} else if(str == 'OK'){
				     			   				Ext.Msg.alert("提示","解挂成功！");
				     			   			} else {
				     			   				showError(str);return;
				     			   			}
			     			   			}
			     			   		}
			     			   	}
			    			});	 
    					}
    				});
    			}
    		},
    		'erpSyncButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'AUDITED' && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				}
    		},
    		'erpHandleHangButton':{
    		   afterrender:function(btn){
    			   var status=Ext.getCmp('cu_statuscode');
    			   if(status && status.value !='HUNG'){
    				   btn.hidden();
    			   }
    		   },	
    		   click:function (btn){
    			   Ext.Ajax.request({
    		        	url : basePath +'scm/sale/submitHandleHangCustomerBase.action',
    		        	params: param,
    		        	method : 'post',
    		        	callback : function(options, success, response){
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo != null){
    		        			showError(res.exceptionInfo);return;
    		        		}else {
    		        			submitSuccess(function(){
    		        				window.location.reload();
    		        			});
    		        		}    		        
    		        	}
    		        });
    		   }
    		},
    		'erpRegB2BButton':{//一键注册
    			afterrender:function(btn){//状态为审核时显示
    			   var status=Ext.getCmp('cu_auditstatuscode');
    			   var uu=Ext.getCmp('cu_uu');
    			   if((status && status.value !='AUDITED')||(uu&&uu.value!='')){
    			   	//状态不为已审核或客户UU已存在时隐藏按钮
    				   btn.hide();
    			   }
    		   	},	
    			click:function(btn){
    				var form=me.getForm(btn);
    				var id=Ext.getCmp('cu_id').value;//客户ID
    				var type='Customer';
    				form.setLoading(true);
    				Ext.Ajax.request({
    		        	url : basePath +'scm/sale/regB2BCustomer.action',
    		        	params: {id:id},
    		        	method : 'post',
    		        	callback : function(options, success, response){
    		        		form.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo != null){
    		        			showError(res.exceptionInfo);return;
    		        		}else {
    		        			showMessage('提示', '注册成功!');
	    						window.location.reload();
    		        		}    		        
    		        	}
    		        });
    			}
    		}
    		
		});
	},
	getSetting : function(fn) {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'CustCodeType\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			var t = false;
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success && r.data){
    				t = r.data == 'true';
	   			}
    			fn.call(me, t);
	   		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getLeadCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'CustomerKind',
	   			field: 'ck_excode',
	   			condition: 'ck_kind=\'' + type + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.data;
	   			}
	   		}
		});
		return result;
	},
	getCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'scm/sale/getCustomerCodeNum.action',
	   		async: false,
	   		params: {
	   			cu_kind: type
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.number;
	   			}
	   		}
		});
		return result;
	}
});