Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BillAPChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gs.BillAPChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.trigger.MultiDbfindTrigger','core.button.Accounted','core.button.ResAccounted',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.SeparNumber',
			'core.window.AssWindow', 'core.button.AssDetail','core.button.AssMain'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'field[name=ca_asstype]':{
    			change: function(f){
    				var btn = Ext.getCmp('assmainbutton');
    				if(Ext.getCmp('bpc_kind').value != '其他付款'){
    					btn.hide();
    				} else {
    					btn.show();
    					btn && btn.setDisabled(Ext.isEmpty(f.value));
    				}
    			}
    		},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				if(Ext.getCmp('bpc_kind').value != '其他付款'){
    					btn.hide();
    				} else {
    					if(Ext.getCmp('ca_asstype') && Ext.isEmpty(Ext.getCmp('ca_asstype').getValue())){
        					btn.setDisabled(true);
        				} else {
        					btn.setDisabled(false);
        				}
    				}
    			}
    		},
    		'erpFormPanel' : {
    			afterload : function(form) {
    				var t = form.down('#bpc_kind');
    				this.hidecolumns(t);
				}
    		},
    		'field[name=bpc_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=bpc_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'combo[name=bpc_kind]': {
    			afterrender: function(m){
    				Ext.defer(function(){
    					var btn = Ext.getCmp('assmainbutton');
        				if(Ext.getCmp('bpc_kind').value != '其他付款'){
        					btn.hide();
        				} else {
        					btn.show();
        				}
    				}, 200);
    			},
    			change: function(m){
    				var btn = Ext.getCmp('assmainbutton');
    				if(Ext.getCmp('bpc_kind').value != '其他付款'){
    					btn.hide();
    				} else {
    					btn.show();
    				}
					this.hidecolumns(m);
				}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['bpd_bapcode'])){
							if(item.data['bpd_amount'] > item.data['bap_leftamount']){
								bool = false;
								showError('明细表第' + item.data['bpd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
    				this.getAmount();
    				if(bool){
    					if(! me.FormUtil.checkForm()){
    						return;
    					}
    					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    						me.FormUtil.getSeqId(form);
    					}
    					var detail = Ext.getCmp('grid');
    					var param2 = new Array();

    					if(Ext.getCmp('assmainbutton')){
    						Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
    							Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
    								d['ass_conid'] = key;
    								param2.push(d);
    							});
    						});	
    					}
    					var param1 = me.GridUtil.getGridStore(detail);
    					if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && (param1.length == 0) && param2.length == 0){
    						showError($I18N.common.grid.emptyDetail);
    						return;
    					} else {
    						param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
    						param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
    						if(form.getForm().isValid()){
    							Ext.each(form.items.items, function(item){
    								if(item.xtype == 'numberfield'){
    									if(item.value == null || item.value == ''){
    										item.setValue(0);
    									}
    								}
    							});
    							var r = form.getValues();
    							form.getForm().getFields().each(function(){
    								if(this.logic == 'ignore') {
    									delete r[this.name];
    								}
    							});
    							me.FormUtil.save(r, param1, param2);
    						}else{
    							me.FormUtil.checkForm();
    						}
    					}
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['bpd_bapcode'])){
							if(item.data['bpd_amount'] > item.data['bap_leftamount']){
								bool = false;
								showError('明细表第' + item.data['bpd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
    				this.getAmount();
    				if(bool){
    					if(! me.FormUtil.checkForm()){
    						return;
    					}
    					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    						me.FormUtil.getSeqId(form);
    					}
    					var detail = Ext.getCmp('grid');
    					var param2 = new Array();
    					if(Ext.getCmp('assmainbutton')){
    						Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
    							Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
    								d['ass_conid'] = key;
    								param2.push(d);
    							});
    						});	
    					}
    					var param1 = me.GridUtil.getGridStore(detail);
    					if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && (param1.length == 0)
    							&& param2.length == 0){
    						showError($I18N.common.grid.emptyDetail);
    						return;
    					} else {
    						param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
    						param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
    						if(form.getForm().isValid()){
    							Ext.each(form.items.items, function(item){
    								if(item.xtype == 'numberfield'){
    									if(item.value == null || item.value == ''){
    										item.setValue(0);
    									}
    								}
    							});
    							var r = form.getValues();
    							form.getForm().getFields().each(function(){
    								if(this.logic == 'ignore') {
    									delete r[this.name];
    								}
    							});
    							me.FormUtil.update(r, param1, param2);
    						}else{
    							me.FormUtil.checkForm();
    						}
    					}
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBillAPChange', '新增应付票据异动作业', 'jsps/fa/gs/billAPChange.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bpc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['bpd_bapcode'])){
							if(item.data['bpd_amount'] > item.data['bap_leftamount']){
								bool = false;
								showError('明细表第' + item.data['bpd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
    				if(bool)
    					me.beforeSubmit(btn);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bpc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bpc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['bpd_bapcode'])){
							if(item.data['bpd_amount'] > item.data['bap_leftamount']){
								bool = false;
								showError('明细表第' + item.data['bpd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
    				if(bool)
    					me.FormUtil.onAudit(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bpc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'erpAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bpc_statuscode');
    				if(status && status.value != 'AUDITED' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAccounted(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'erpResAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bpc_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAccounted(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('bpc_id').value);
    			}
    		},
    		'dbfindtrigger[name=bpd_bapcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('bpc_vendcode')){
    					var code = Ext.getCmp('bpc_vendcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('bpc_vendcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		}	
    	});
    }, 
    //计算借方金额   并写入主表借方总额字段
	getAmount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		Ext.each(items,function(item,index){
			if(item.data['bpd_bapcode']!=null&&item.data['bpd_bapcode']!=""){
				amount= amount + Number(item.data['bpd_amount']);
			}
		});
		Ext.getCmp('bpc_amount').setValue(Ext.Number.toFixed(amount, 2));
	},
    beforeSubmit:function(btn){
    	var me = this;
    	var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    	var amount = Number(Ext.getCmp('bpc_amount').getValue());
    	var detailamount = 0;
    	Ext.each(items,function(item,index){
    		detailamount = detailamount+Number(item.data['bpd_amount']);
    		if(Ext.Number.toFixed(amount, 2) != Ext.Number.toFixed(detailamount, 2)){
    			//抛出异常
    			showError('明细行借方金额与借方总额不等,不能提交');return;
    		}
			me.FormUtil.onSubmit(Ext.getCmp('bpc_id').value);
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	 hidecolumns:function(m){
			if(!Ext.isEmpty(m.getValue())) {
				var form = m.ownerCt;
				if(m.value == '兑现'){
					form.down('#bpc_vendcode') && form.down('#bpc_vendcode').show();
					form.down('#bpc_vendname') && form.down('#bpc_vendname').show();
				} else if(m.value == '退票'){
					form.down('#bpc_vendcode') && form.down('#bpc_vendcode').hide();
					form.down('#bpc_vendname') && form.down('#bpc_vendname').hide();
					form.down('#bpc_catecode') && form.down('#bpc_catecode').hide();
					form.down('#bpc_catename') && form.down('#bpc_catename').hide();
				} else {
					form.down('#bpc_vendcode') && form.down('#bpc_vendcode').hide();
					form.down('#bpc_vendname') && form.down('#bpc_vendname').hide();
				}
			}
	 },
	 getCodeCondition: function(){
			var field = "bap_vendcode";
			var tFields = 'bpc_vendcode,bpc_vendname,bpc_catecode,bpc_catename';
			var fields = 'bap_vendcode,bap_vendname,bap_paybankcode,bap_paybank';
			var tablename = 'BillAP';
			var myfield = 'bap_code';
			var obj = new Object();
			obj.field = field;
			obj.fields = fields;
			obj.tFields = tFields;
			obj.tablename = tablename;
			obj.myfield = myfield;
			return obj;
		}
});