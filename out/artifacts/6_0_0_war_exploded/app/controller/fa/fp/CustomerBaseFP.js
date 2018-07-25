Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.CustomerBaseFP', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.CustomerBaseFP','core.form.Panel','core.form.MultiField','core.grid.Panel2','core.button.HandleHang','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Banned','core.button.ResBanned','core.button.Sync','core.button.FormsDoc',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn), s = Ext.getCmp('cu_source'), 
						codeField = Ext.getCmp(form.codeField), kind =Ext.getCmp('cu_kind').value;
					if(s) {
						var source = s.value;
						if(codeField.value == null || codeField.value == ''){
							me.getSetting(function(s){
								if(s) {
									if(source == '国内'){
										me.BaseUtil.getRandomNumber('Customer!G');//自动添加编号
									} else if(source == '海外'){
										me.BaseUtil.getRandomNumber('Customer!W');//自动添加编号
									} else {
										me.BaseUtil.getRandomNumber(null,10,null);
										var res = me.getLeadCode(kind);
										if(res != null && res != ''){
											codeField.setValue(res + codeField.getValue());
										}
									}
								} else {
									me.BaseUtil.getRandomNumber(null,10,null);
									var res = me.getLeadCode(kind);
									if(res != null && res != ''){
										codeField.setValue(res + codeField.getValue());
									}
								}
							});
	    				}
					} else {
						if(codeField.value == null || codeField.value == ''){
							me.BaseUtil.getRandomNumber(null,10,null);//自动添加编号
							var res = me.getLeadCode(kind);
							if(res != null && res != ''){
								codeField.setValue(res + codeField.getValue());
							}
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
					var id = Ext.getCmp('cu_id').value;
					if(id == null || id == '' || id == '0' || id == 0){
						this.FormUtil.beforeSave(this);
					} else {
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('cu_id').value);
				}
			},
			'erpUpdateButton': {
/*				afterrender: function(btn){
    				var status = Ext.getCmp('cu_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},*/
    			click: function(btn){
    				me.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustomerBaseFP', '新增客户基础资料', 'jsps/fa/fp/customerBaseFP.jsp');
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
					me.beforeSubmit(btn);
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
    		'erpHandleHangButton':{
    		   afterrender:function(btn){
    			   var status=Ext.getCmp('cu_statuscode');
    			   if(status && status.value !='HUNG'){
    				   btn.hidden();
    			   }
    		   },	
    		   click:function (btn){
    			   Ext.Ajax.request({
    		        	url : basePath +'fa/fp/submitHandleHangCustomerBaseFP.action',
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
    beforeSubmit:function(btn){
    	var me = this;
    	var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    	me.FormUtil.onSubmit(Ext.getCmp('cu_id').value);
    },
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
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
	}
});