Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.CustomerCreWarn', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.CustomerCreWarn','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					Ext.getCmp(form.codeField).setValue(me.BaseUtil.getRandomNumber());//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				this.beforeSaveCustomerCreWarn();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCustomerCreWarn', '新增应收发票单', 'jsps/fa/ars/CustomerCreWarn.jsp');
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
    		}
    	});
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
	beforeSaveCustomerCreWarn: function(){
		/*Ext.getCmp('pu_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid');
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('pd_code',Ext.getCmp('pu_code').value);
		});
		//手工录入采购单,合同类型不能为标准
		var pu_kind = Ext.getCmp('pu_kind').value;
		if(pu_kind == null || pu_kind == ''){
			showError('合同类型不能为空');return;
		}
		if(pu_kind == '标准' || pu_kind == 'normal' || pu_kind == '標準'){
			showError('手工录入采购单,合同类型不能为标准');return;
		}
		//采购价格不能为0
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_price'] == null){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的价格为空');return;
				} else if(item.data['pd_price'] == 0 || item.data['pd_price'] == '0'){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的价格为0');return;
				}
			}
		});
		//物料交货日期不能小于录入日期
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_delivery'] == null){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的承诺日期为空');return;
				} else if(item.data['pd_delivery'] < Ext.getCmp('pu_indate').value){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的承诺日期小于单据录入日期');return;
				}
			}
		});*/
		//保存CustomerCreWarn
		/*if(bool)*/
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid');
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('abd_abid',Ext.getCmp('ab_id').value);
		});
		//采购价格不能为0
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
	/*	Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_price'] == null){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的价格为空');return;
				} else if(item.data['pd_price'] == 0 || item.data['pd_price'] == '0'){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的价格为0');return;
				}
			}
		});
		//物料交货日期不能小于录入日期
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_delivery'] == null){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的承诺日期为空');return;
				} else if(item.data['pd_delivery'] < Ext.getCmp('pu_indate').value){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的承诺日期小于单据录入日期');return;
				}
			}
		});*/
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	}
});