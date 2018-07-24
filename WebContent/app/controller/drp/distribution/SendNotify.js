Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.SendNotify', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.distribution.SendNotify','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
     		'core.button.Save','core.button.Add','core.button.ResAudit','core.button.Audit','core.button.ResSubmit',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.Consign',
  			'core.button.Submit','core.button.TurnProdIO','core.button.Flow','core.button.Print',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('sn_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.beforeSaveSendNotify();
    				var bool = true;
    				//数量不能为空或0
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['snd_outqty'] == null){
    							bool = false;
    							showError('明细表第' + item.data['snd_pdno'] + '行的数量为空');return;
    						}
    					}
    				});
    				//价格不能为0
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['snd_sendprice'] == null){
    							bool = false;
    							showError('明细表第' + item.data['snd_pdno'] + '行的价格为空');return;
    						} else if(item.data['snd_sendprice'] == 0 || item.data['snd_sendprice'] == '0'){
    							bool = false;
    							showError('明细表第' + item.data['snd_pdno'] + '行的价格为0');return;
    						}
    					}
    				});    			
    				if(bool){
    					this.FormUtil.beforeSave(me);//保存
    				}
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('sn_id').value)});
    			}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('sn_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSendNotify', '新增付款方式', 'jsps/scm/sale/sendNotify.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpPrintButton':{
    			click:function(btn){
    				var reportName="SendNotify";
    				var condition='{SendNotify.sn_id}='+Ext.getCmp('sn_id').value+'';
    				me.FormUtil.onwindowsPrint(Ext.getCmp('sn_id').value, reportName,condition);
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sn_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('sn_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sn_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sn_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sn_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sn_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sn_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sn_id').value);
				}
			},
    		'erpTurnProdIOButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sn_statuscode');
    				if(status && status.value != 'AUDITED' && status.value != 'PARTOUT'){
    					btn.hide();
    				}
    			}
    		},
    		'dbfindtrigger[name=snd_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('sn_custcode')){
    					var code = Ext.getCmp('sn_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
        					if(!Ext.getCmp('sn_custcode').readOnly){
        						Ext.getCmp('sn_custcode').setReadOnly(true);
            					Ext.getCmp('sn_custcode').setFieldStyle(Ext.getCmp('sn_custcode').fieldStyle + ';background:#f1f1f1;');
        					}
        				}
    				}
    			},
    			aftertrigger: function(t){
    				var code = Ext.getCmp('sn_custcode').value;
    				if(code == null || code.toString().trim() == ''){
    					var obj = me.getCodeCondition();
    					me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    				}
    			}
    		},
    		'dbfindtrigger[name=snd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['snd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sa_code='" + code + "'";
    				}
    			}
    		},
    		'field[name=sn_statuscode]': {
    			change: function(f){
    				var grid = Ext.getCmp('grid');
    				console.log(grid);
    				if(grid && f.value != 'ENTERING' && f.value != 'COMMITED'){
    					grid.setReadOnly(true);//只有未审核的订单，grid才能编辑
    				}
    			}
    		},
    		'dbfindtrigger[name=snd_batchcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var pr = record.data['snd_prodcode'];
    				if(pr == null || pr == ''){
    					showError("请先选择料号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}
    			}
    		},
    		'erpConsignButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sn_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
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
	beforeSaveSendNotify: function(){
		Ext.getCmp('sn_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var bool = true;
		var cust = Ext.getCmp('sn_custid').value, sncode = Ext.getCmp('sn_code').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
	    Ext.Array.each(items, function(item){
	    	item.set('snd_code',sncode);
		});
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['snd_outqty'] == null || item.data['snd_outqty'] == '' || item.data['snd_outqty'] == '0'
					|| item.data['snd_outqty'] == 0){
					bool = false;
					showError('明细表第' + item.data['snd_pdno'] + '行的数量为空');return;
				}
			}
		});
		//销售价格不能为0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['snd_sendprice'] == null){
					bool = false;
					showError('明细表第' + item.data['snd_pdno'] + '行的价格为空');return;
				} else if(item.data['snd_sendprice'] == 0 || item.data['snd_sendprice'] == '0'){
					bool = false;
					showError('明细表第' + item.data['snd_pdno'] + '行的价格为0');return;
				}
			}
		});
		//保存sale
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		Ext.getCmp('sn_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var bool = true;
		var cust = Ext.getCmp('sn_custid').value, sncode = Ext.getCmp('sn_code').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
	    Ext.Array.each(items, function(item){
	    	item.set('snd_code',sncode);
		});
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['snd_outqty'] == null || item.data['snd_outqty'] == '' || item.data['snd_outqty'] == '0'
					|| item.data['snd_outqty'] == 0){
					bool = false;
					showError('明细表第' + item.data['snd_pdno'] + '行的数量为空');return;
				}
			}
		});
		//销售价格不能为0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['snd_sendprice'] == null){
					bool = false;
					showError('明细表第' + item.data['snd_pdno'] + '行的价格为空');return;
				} else if(item.data['snd_sendprice'] == 0 || item.data['snd_sendprice'] == '0'){
					bool = false;
					showError('明细表第' + item.data['snd_pdno'] + '行的价格为0');return;
				}
			}
		});
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	},
	/**
	 * snd_ordercode的限制条件
	 */
	getCodeCondition: function(){
		var field = null;
		var fields = '';
		var tablename = '';
		var myfield = '';
		var tFields = 'sn_custid,sn_custcode,sn_custname,sn_currency,sn_rate,sn_payments,sn_payment,sn_toplace,sn_sellerid,sn_sellername';
		switch (caller) {
			case 'SendNotify!Drp': //配货通知单
				field = "sa_custcode";
				fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_paymentsid,sa_payments,sa_toplace,sa_sellerid,sa_seller';
				tablename = 'Sale';
				myfield = 'sa_code';
				break;
		}
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	}
});