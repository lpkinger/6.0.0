Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.ARBill', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.ARBill','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
     		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('abd_statuscode');
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
    						if(item.data['abd_qty'] == null){
    							bool = false;
    							showError('明细表第' + item.data['abd_detno'] + '行的数量为空');return;
    						}
    					}
    				});
    				//价格不能为0
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['abd_price'] == null){
    							bool = false;
    							showError('明细表第' + item.data['abd_detno'] + '行的价格为空');return;
    						} else if(item.data['abd_price'] == 0 || item.data['abd_price'] == '0'){
    							bool = false;
    							showError('明细表第' + item.data['abd_detno'] + '行的价格为0');return;
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
					me.FormUtil.onDelete(Ext.getCmp('ab_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('ab_statuscode');
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
					me.FormUtil.onAdd('addARBill', '新增预测冲销', 'jsps/scm/sale/ARBill.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ab_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ab_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ab_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ab_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ab_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ab_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ab_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ab_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('ab_id').value);
				}
			},
    		'dbfindtrigger[name=abd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['abd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}  else {
    					var field = me.getBaseCondition();
    					if(field){
    						t.dbBaseCondition = field + "='" + code + "'";
    					}
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
		Ext.getCmp('ab_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('ab_custid').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('abd_code',Ext.getCmp('ab_code').value);
		});
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['abd_qty'] == null || item.data['abd_qty'] == '' || item.data['abd_qty'] == '0'
					|| item.data['abd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['abd_detno'] + '行的数量为空');return;
				}
			}
		});
		//销售价格不能为0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['abd_price'] == null){
					bool = false;
					showError('明细表第' + item.data['abd_detno'] + '行的价格为空');return;
				} else if(item.data['abd_price'] == 0 || item.data['abd_price'] == '0'){
					bool = false;
					showError('明细表第' + item.data['abd_detno'] + '行的价格为0');return;
				}
			}
		});
		//保存sale
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		Ext.getCmp('ab_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('ab_custid').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('abd_code',Ext.getCmp('ab_code').value);
		});
		var items = grid.store.data.items;
		var bool = true;
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['abd_qty'] == null || item.data['abd_qty'] == '' || item.data['abd_qty'] == '0'
					|| item.data['abd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['abd_detno'] + '行的数量为空');return;
				}
			}
		});
		//销售价格不能为0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['abd_price'] == null){
					bool = false;
					showError('明细表第' + item.data['abd_detno'] + '行的价格为空');return;
				} else if(item.data['abd_price'] == 0 || item.data['abd_price'] == '0'){
					bool = false;
					showError('明细表第' + item.data['abd_detno'] + '行的价格为0');return;
				}
			}
		});
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	},
	getBaseCondition: function(){
		var field = null;
		switch (caller) {
			case 'ARBill': //出货单开票
				field = "pd_inoutno";
			case 'ARBill!Sale': //销售退货单开票
				field = "pd_inoutno";
		}
		return field;
	}
});