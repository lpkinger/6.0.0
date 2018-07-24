Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.PreCustomer', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.PreCustomer','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.TurnCustomer','core.button.TurnFKCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.form.CascadingCityField'
  	],
	init:function(){
		var me = this;
		this.control({
      		'erpFormPanel':{
      			afterload:function(form){
      				//用updatedate来判断是否是刚录入的
      				var cuCode = form.getComponent("cu_code");
      				//如果是从商机转过来的客户
      				if(bc_code!='null'&&typeof(cuCode)!="undefined"&&(cuCode.value==""||cuCode.value==null)){
      					
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
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn),
						codeField = Ext.getCmp(form.codeField), kind =Ext.getCmp('cu_kind').value;
					if(codeField.value == null || codeField.value == ''){
						//var res = me.getLeadCode(kind);
						var res = me.getCode(kind);
						if(res != null && res != ''){
							//codeField.setValue(res + codeField.getValue());
							codeField.setValue(res);
						} else 
							me.BaseUtil.getRandomNumber('Customer',10,null);//自动添加编号
					}
					var cust = Ext.getCmp('cu_code').value, custname = Ext.getCmp('cu_name').value, 
					shcustcode = Ext.getCmp('cu_shcustcode').value, arcode=Ext.getCmp('cu_arcode').value;
					cust = cust != null ? cust.toUpperCase() : null;
					var cuShortname = Ext.getCmp('cu_shortname');
					//消除公司名称与简称前后空格
					if (custname) {
						custname = custname.replace(/(^\s*)|(\s*$)/g, "");
						Ext.getCmp('cu_name').setValue(custname);
					}
					if (cuShortname) {
						var cuShort = cuShortname.value;
						if (cuShort) {
							cuShort = cuShort.replace(/(^\s*)|(\s*$)/g, "");
							cuShortname.setValue(cuShort);
						}
					}
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
				click: function(btn){
					var cust = Ext.getCmp('cu_code').value, custname = Ext.getCmp('cu_name').value, 
					 kind =Ext.getCmp('cu_kind').value,shcustcode = Ext.getCmp('cu_shcustcode').value, 
					 arcode=Ext.getCmp('cu_arcode').value;
					//cust = cust != null ? cust.toUpperCase() : null;
					var cuShortname = Ext.getCmp('cu_shortname');
					//消除公司名称与简称前后空格
					if (custname) {
						custname = custname.replace(/(^\s*)|(\s*$)/g, "");
						Ext.getCmp('cu_name').setValue(custname);
					}
					if (cuShortname) {
						var cuShort = cuShortname.value;
						if (cuShort) {
							cuShort = cuShort.replace(/(^\s*)|(\s*$)/g, "");
							cuShortname.setValue(cuShort);
						}
					}
					if(cust==null||cust==''){
						var res = me.getCode(kind);
						if(res != null && res != ''){
							Ext.getCmp('cu_code').setValue(res);
						} else {
							me.BaseUtil.getRandomNumber('Customer',10,null);//自动添加编号
						}
					}
					if(shcustcode == null || shcustcode == ''){
						Ext.getCmp('cu_shcustcode').setValue(cust);
						Ext.getCmp('cu_shcustname').setValue(custname);
					}	
					if(arcode == null || arcode == ''){
						Ext.getCmp('cu_arcode').setValue(cust);
						Ext.getCmp('cu_arname').setValue(custname);
					}	
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPreCustomer', '新增客户资料', 'jsps/scm/sale/preCustomer.jsp');
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
			'erpTurnFKCustomerButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('cu_auditstatuscode');
					if(status && status.value != 'TURNED'){
						btn.hide();
					}
				},
				click:function(){    				
    				var cu_code=Ext.getCmp('cu_code').value;
    				var cu_id;
					Ext.Ajax.request({
					url:basePath+'common/getFieldData.action',
					async: false,
					params: {
			   			caller: 'Customer',
			   			field: 'cu_id',
			   			condition: "cu_code='"+cu_code+"'"
			   		},
			   		method : 'post',
			   		callback:function(opt, s, res){
			   			var r = new Ext.decode(res.responseText);
			   			if(r.exceptionInfo){
			   				showError(r.exceptionInfo);return;
			   			}
		    			if(r.success && r.data){
		    				cu_id = r.data;
			   			}
			   		}
    				});
    				var formCondition="cu_id='"+cu_id+"'";
    				var gridCondition="shi_cuid='"+cu_id+"'";
    				var linkCaller='Customer!BaseFP'; 
    				me.FormUtil.onAdd('Customer!BaseFP', '风控客户资料', 'jsps/fa/fp/customerBaseFP.jsp?_noc=1&whoami='+linkCaller+'&formCondition='+formCondition+'&gridCondition='+gridCondition);    			
    			}
			},
    		'erpTurnCustomerButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cu_auditstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入客户吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/sale/turnCustomer.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('cu_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			console.log(localJson);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/sale/customerBase.jsp?formCondition=cu_id=" + id;
    	    		    					me.FormUtil.onAdd('Customer' + id, '客户' + id, url);
    	    		    					window.loaction.href = window.loaction.href;
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
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