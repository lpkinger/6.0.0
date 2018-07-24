Ext.QuickTips.init();
Ext.define('erp.controller.plm.scm.Purchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','plm.scm.Purchase','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.End','core.button.ResEnd',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.StatusField'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
    		'erpGridPanel2': { 
    			afterrender: function(grid){
    				var status = Ext.getCmp('pu_statuscode');
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
    				//保存之前的一些前台的逻辑判定
    				this.beforeSavePurchase();
    			}
    		},
    		'field[name=pu_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pu_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pu_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
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
    				me.FormUtil.onAdd('addPurchase', '新增试产采购单', 'jsps/plm/scm/purchase.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				//数量不能为空或0
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['pd_qty'] == null){
    							bool = false;
    							showError('明细表第' + item.data['pd_detno'] + '行的数量为空');return;
    						}
    					}
    				});
    				//采购价格不能为0
    				if(Ext.getCmp('pu_getprice').value == 0){//是否自动获取单价
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
    				}
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
    				});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('pu_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pu_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pu_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pu_id').value);
    			}
    		},
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('pu_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pu_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('pu_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    			 var reportName = '';
                 reportName = "PURCLIST";
                 var condition = '{Purchase.pu_id}=' + Ext.getCmp('pu_id').value + '';
                 var id = Ext.getCmp('pu_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'field[name=pu_vendcode]': {
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					if(Ext.getCmp('pu_receivecode').value == null || Ext.getCmp('pu_receivecode').value.toString().trim() == ''){
    						Ext.getCmp('pu_receivecode').setValue(f.value);
    					}
    				}
    			}
    		},
    		'field[name=pu_vendname]': {
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					if(Ext.getCmp('pu_receivename').value == null || Ext.getCmp('pu_receivename').value.toString().trim() == ''){
    						Ext.getCmp('pu_receivename').setValue(f.value);
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
	beforeSavePurchase: function(){
		Ext.getCmp('pu_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid');
		var vend = Ext.getCmp('pu_vendid').value;
		if(vend == null || vend == '' || vend == '0' || vend == 0){
			showError('未选择供应商，或供应商编号无效!');
			return;
		}
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('pd_code',Ext.getCmp('pu_code').value);
	    	item.set('pd_vendid', Ext.getCmp('pu_vendid').value);
			item.set('pd_vendcode', Ext.getCmp('pu_vendcode').value);
			item.set('pd_vendname', Ext.getCmp('pu_vendname').value);
		});
		//手工录入采购单,合同类型不能为标准
		var pu_kind = Ext.getCmp('pu_kind').value;
		if(pu_kind == null || pu_kind == ''){
			showError('合同类型不能为空');return;
		}
		if(pu_kind == '标准' || pu_kind == 'normal' || pu_kind == '標準'){
			showError('手工录入采购单,合同类型不能为标准');return;
		}
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_qty'] == null){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的数量为空');return;
				}
			}
		});
		//采购价格不能为0
		if(Ext.getCmp('pu_getprice').value == 0){//是否自动获取单价
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
		}
		//物料交货日期不能小于录入日期
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_delivery'] == null){
					item.set('pd_delivery', Ext.getCmp('pu_delivery'));
				} else if(item.data['pd_delivery'] < Ext.getCmp('pu_indate').value){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于单据录入日期');return;
				}
			}
		});
		//保存purchase
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid');
		var vend = Ext.getCmp('pu_vendid').value;
		if(vend == null || vend == '' || vend == '0' || vend == 0){
			showError('未选择供应商，或供应商编号无效!');
			return;
		}
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('pd_code',Ext.getCmp('pu_code').value);
	    	item.set('pd_vendid', Ext.getCmp('pu_vendid').value);
			item.set('pd_vendcode', Ext.getCmp('pu_vendcode').value);
			item.set('pd_vendname', Ext.getCmp('pu_vendname').value);
		});
	    var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_qty'] == null || item.data['pd_qty'] == '' || item.data['pd_qty'] == '0'
					|| item.data['pd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的数量为空');return;
				}
			}
		});
		//采购价格不能为0
		if(Ext.getCmp('pu_getprice').value == 0){//是否自动获取单价
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
		}
		//物料交货日期不能小于录入日期
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['pd_delivery'] == null){
					item.set('pd_delivery', Ext.getCmp('pu_delivery'));
				} else if(item.data['pd_delivery'] < Ext.getCmp('pu_indate').value){
					bool = false;
					showError('明细表第' + item.data['pd_detno'] + '行的交货日期小于单据录入日期');return;
				}
			}
		});
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	}
});