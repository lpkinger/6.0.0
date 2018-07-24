Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SaleClose', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.SaleClose','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.form.FileField', 'core.form.MultiField',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
					me.FormUtil.onDelete(Ext.getCmp('sc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSaleClose', '新增销售结案申请单', 'jsps/scm/sale/SaleClose.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('sc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sc_id').value);
				}
			},
    		'dbfindtrigger[name=scd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['scd_ordercode'];
    				var grid = Ext.getCmp('grid');
    				if(code == null || code == ''){
    					showError("请先选择订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sa_code='" + code + "'";
    				}
    				var cond ="(sa_code='"+code+"' ";
    				var arr = new Array();
    				Ext.each(grid.store.data.items, function(item){
    					if(item.data['scd_ordercode'] != null && item.data['scd_ordercode'] != ''
    						&& item.data['scd_orderdetno'] != null && item.data['scd_orderdetno'] !=''){
    						if(item.data['scd_ordercode'] ==code){
    							arr.push("sd_detno<>"+item.data['scd_orderdetno']);
    						}
    					}
    				});
    				if(arr.length > 0){
    					cond += ' AND ' + arr.join(' and ');
    				}
    				t.dbBaseCondition = cond + ") ";
    			}
    		},
    		'multidbfindtrigger[name=scd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['scd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sd_code='" + code + "'";
    				}
    			}
    		},
    		'dbfindtrigger[name=sc_departmentcode]': {
    			afterrender: function(t) {// 根据录入人带出部门
    				Ext.defer(function(){
    					var form = t.up('form'),
    						status = form.down('#sc_statuscode');
    					if(Ext.isEmpty(t.getValue()) && status && status.value == 'ENTERING'){
    						me.getDepart(function(data){
    							t.setValue(data.dp_code);
    							form.down('#sc_departmentname').setValue(data.dp_name);
    						});
    					}
    				}, 300);
    			}
    		}
		});
	}, 
	/**
	 * @param allowEmpty 是否允许Grid为空
	 */
	onSubmit: function(id, allowEmpty, errFn, scope){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var s = me.FormUtil.checkFormDirty(form);
			if(s == '' || s == '<br/>'){
				me.FormUtil.submit(id);
			} else {
				Ext.MessageBox.show({
				     title:'保存修改?',
				     msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
				     buttons: Ext.Msg.YESNOCANCEL,
				     icon: Ext.Msg.WARNING,
				     fn: function(btn){
				    	 if(btn == 'yes'){
				    		 if(typeof errFn === 'function')
				    			 errFn.call(scope);
				    		 else
				    			 me.FormUtil.onUpdate(form, true);
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
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getDepart: function(fn) {
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsData.action',
	   		async: false,
	   		params: {
	   			caller: 'Department left join employee on em_depart=dp_name',
	   			fields: 'dp_code,dp_name',
	   			condition: 'em_code=\'' + em_code + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			} else if(r.success && r.data){
    				fn.call(null, r.data);
    			}
	   		}
		});
	}
});