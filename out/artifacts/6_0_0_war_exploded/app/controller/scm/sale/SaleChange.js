Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SaleChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.SaleChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.form.FileField', 'core.form.MultiField','core.trigger.MultiDbfindTrigger','core.button.CallProcedureByConfig',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumnNV'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick,
				reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						me.GridUtil.autoDbfind(grid, 'scd_sacode', detail);
					}
				}
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['scd_sddetno'])){
	    		   			if(Ext.isEmpty(item.data['scd_newdelivery'])){
	    		   				item.set('scd_newdelivery', Ext.getCmp('scd_delivery'));
	    		   			}
	    		   		}
					});
					if(bool)
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
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['scd_sddetno'])){
	    		   			if(Ext.isEmpty(item.data['scd_newdelivery'])){
	    		   				item.set('scd_newdelivery', Ext.getCmp('scd_delivery'));
	    		   			}
	    		   		}
					});
					if(bool)
						this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSaleChange', '新增销售变更单', 'jsps/scm/sale/saleChange.jsp?whoami=' + caller);
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
					me.onSubmit(Ext.getCmp('sc_id').value);
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
			'erpPrintButton': {
				click:function(btn){
					var reportName="SaleChange";
					var condition='{SaleChange.sc_id}='+Ext.getCmp('sc_id').value+'';
					var id=Ext.getCmp('sc_id').value;
					me.FormUtil.onwindowsPrint2(id,reportName,condition);
				}
			},
			'dbfindtrigger[name=scd_sddetno]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['scd_sacode'];
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
    					if(item.data['scd_sacode'] != null && item.data['scd_sacode'] != ''
    						&& item.data['scd_sddetno'] != null && item.data['scd_sddetno'] !=''){
    						if(item.data['scd_sacode'] ==code){
    							arr.push("sd_detno<>"+item.data['scd_sddetno']);
    						}
    					}
    				});
    				if(arr.length > 0){
    					cond += ' AND ' + arr.join(' and ');
    				}
    				t.dbBaseCondition = cond + ") ";
    			}
    		},
    		'multidbfindtrigger[name=scd_sddetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['scd_sacode'];
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
    					if(item.data['scd_sacode'] != null && item.data['scd_sacode'] != ''
    						&& item.data['scd_sddetno'] != null && item.data['scd_sddetno'] !=''){
    						if(item.data['scd_sacode'] ==code){
    							arr.push("sd_detno<>"+item.data['scd_sddetno']);
    						}
    					}
    				});
    				if(arr.length > 0){
    					cond += ' AND ' + arr.join(' and ');
    				}
    				t.dbBaseCondition = cond + ") ";
    			}
    		},
			'textfield[name=sc_newdelivery]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('scd_newdelivery',date);
						});
					}
				}
    		},
    		'dbfindtrigger[name=sc_dpcode]': {
    			afterrender: function(t) {// 根据录入人带出部门
    				Ext.defer(function(){
    					var form = t.up('form'),
    						status = form.down('#sc_statuscode');
    					if(Ext.isEmpty(t.getValue()) && status && status.value == 'ENTERING'){
    						me.getDepart(function(data){
    							t.setValue(data.dp_code);
    							form.down('#sc_dpname').setValue(data.dp_name);
    						});
    					}
    				}, 300);
    			}
    		},
    		'dbfindtrigger[name=scd_newpayments]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var grid = Ext.getCmp('grid');
    				var column = grid.down('gridcolumn[dataIndex=scd_newpayments]');
    				if(column && column.dbfind && column.dbfind.indexOf('CustomerPayments') > -1) {
    					var record = grid.selModel.getLastSelected();
        				var code = record.data['cu_id'];
	    				if(code == null || code == ''){
	    					showError("请先选择订单单号!");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    				} else {
	    					t.dbBaseCondition = "cp_cuid='" + code + "'";
	    				}
    				}
    			}
    		},
    		'dbfindtrigger[name=sc_dpname]':{
				beforetrigger:function(t){
					t.autoDbfind = false;
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