Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.CustomerType', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'drp.distribution.CustomerType', 'core.form.Panel', 'core.grid.Panel2', 'core.form.MultiField','core.form.FileField',
			'core.button.Save', 'core.button.Add', 'core.button.Submit', 'core.button.Print', 'core.button.Upload',
			'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.HandleHang',
			'core.button.Update', 'core.button.ResSubmit', 'core.button.Banned', 'core.button.ResBanned',
			'core.button.Hung','core.button.ReHung',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.form.YnField', 'core.button.Sync' ],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick

			},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					var cust = Ext.getCmp('cu_code').value, custname = Ext.getCmp('cu_name').value, shcustcode = Ext
							.getCmp('cu_shcustcode').value, arcode = Ext.getCmp('cu_arcode').value;
					if (shcustcode == null || shcustcode == '') {
						Ext.getCmp('cu_shcustcode').setValue(cust);
						Ext.getCmp('cu_shcustname').setValue(custname);
					}
					if (arcode == null || arcode == '') {
						Ext.getCmp('cu_arcode').setValue(cust);
						Ext.getCmp('cu_arname').setValue(custname);
					}
					var id = Ext.getCmp('cu_id').value;
					if (id == null || id == '' || id == '0' || id == 0) {
						this.FormUtil.beforeSave(this);
					} else {
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cu_id').value);
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value != 'ENTERING' && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					var id = Ext.getCmp('cu_id').value;
					if (id == null || id == '' || id == '0' || id == 0) {
						this.FormUtil.beforeSave(this);
					} else {
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addCustomerBase', '新增客户基础资料', 'jsps/scm/sale/customerBase.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('cu_id').value, true);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cu_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cu_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cu_id').value);
				}
			},
			'erpPrintButton' : {
				click : function(btn) {
					me.FormUtil.onPrint(Ext.getCmp('cu_id').value);
				}
			},
			'erpBannedButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value == 'DISABLE') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onBanned(Ext.getCmp('cu_id').value);
				}
			},
			'erpResBannedButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_auditstatuscode');
					if (status && status.value != 'DISABLE') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResBanned(Ext.getCmp('cu_id').value);
				}
			},
			'erpHungButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'AUDITED'){
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
	    		        	params: {
	    		        		id:Ext.getCmp('cu_id').value
	    		        	},
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
	    		}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick : function(selModel, record) {// grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	}
});