Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.Packing', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.Packing','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.CatchMadeIn','core.button.PrintByCondition','core.button.Sync',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.SendEdi','core.button.CancelEdi'
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
			'field[name=pi_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pi_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pi_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPacking', '新增包装单', 'jsps/scm/reserve/packing.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pi_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pi_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pi_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pi_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
				var reportName='';
    		    reportName="PIOPackinglist";
				var condition='{Packing.pi_id}='+Ext.getCmp('pi_id').value+'';
				var id=Ext.getCmp('pi_id').value;
			    me.FormUtil.onwindowsPrint(id,reportName,condition);
				}
			},
			'erpSyncButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				}
    		},
			'erpCatchMadeInButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var form = btn.ownerCt.ownerCt,
						pi_id = Ext.getCmp('pi_id').value;
						form.setLoading(true);
					Ext.Ajax.request({
						url: basePath + 'scm/reserve/updateMadeIn.action',
						params: {
							pi_id: pi_id
						},
						callback: function(opt, s, r) {
							form.setLoading(false);
							var rs = Ext.decode(r.responseText);
							if(rs.success) {
								alert('更新成功!');
								me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'pd_piid=' + pi_id});
							}
						}
					});
				}
			},
			'dbfindtrigger[name=pd_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('pi_custcode')){
    					var code = Ext.getCmp('pi_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
        				}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('pi_custcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		},
    		'field[name=pi_code]': {
  			   afterrender:function(f){
 				   f.setFieldStyle({
 					   'color': 'blue'
 				   });
 				   f.focusCls = 'mail-attach';
 				   var c = Ext.Function.bind(me.openInvoice, me);
 				   Ext.EventManager.on(f.inputEl, {
 					   mousedown : c,
 					   scope: f,
 					   buffer : 100
 				   });

  			   }
    		},
    		'field[name=pi_relativecode]': {
   			   afterrender:function(f){
  				   f.setFieldStyle({
  					   'color': 'blue'
  				   });
  				   f.focusCls = 'mail-attach';
  				   var c = Ext.Function.bind(me.openRelative, me);
  				   Ext.EventManager.on(f.inputEl, {
  					   mousedown : c,
  					   scope: f,
  					   buffer : 100
  				   });
   			   }
     		},
    		'dbfindtrigger[name=pd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['pd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sd_code='" + code + "'";
    				}
    			}
    		},
    		'dbfindtrigger[name=pi_custcode2]': {
    			afterrender:function(trigger){
    				trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=pi_invoiceremark]': {
    			afterrender:function(trigger){
    				trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=pi_packingremark]': {
    			afterrender:function(trigger){
    				trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=pi_receivecode]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
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
	getCodeCondition: function(){
		var field = null;
		var fields = '';
		var tablename = '';
		var myfield = '';
		var tFields = '';
		tFields = 'pi_custcode,pi_custname,pi_currency,pi_rate,pi_paymentcode,pi_payment,pi_sellercode,pi_seller,pi_address';
		fields = 'sa_custcode,sa_custname,sa_currency,sa_rate,sa_paymentscode,sa_payments,sa_sellercode,sa_seller,sa_toplace';
		tablename = 'Sale';
		myfield = 'sa_code';
		field = "sa_custcode";
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	openInvoice: function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#in_id');
		if(i && i.value) {
			url = 'jsps/scm/reserve/invoice.jsp?formCondition=in_idIS' + i.value + '&gridCondition=id_inidIS' + i.value;
			openUrl(url);
		}
	},
	openRelative: function(e, el, obj) {
		var f = obj.scope;
		if(f.value) {
			this.FormUtil.onAdd('ProdInOut', f.ownerCt.down('#pi_source').value, 
					this.getRelativeUrl(f));
		}
	},
	getRelativeUrl: function(f) {
		var v = f.value, form = f.ownerCt,
			s = form.down('#pi_source'),
			cal = this.getRelativeCaller(s.value),
			u = '';
		if(!Ext.isEmpty(v)) {
			if(v.indexOf(',') > 0) {
				var _v = v.split(',');
				for(var i in _v) {
					_v[i] = "'" + _v[i] + "'";
				}
				var id = this.getRelativeId(_v.join(','), s.value);
				u = 'jsps/common/datalist.jsp?whoami=' + cal + '&urlcondition=pi_class=\'' 
					+ s.value + '\' and pi_id in(' + id + ')';
			} else {
				var id = this.getRelativeId('\'' + v + '\'', s.value);
				u = 'jsps/scm/reserve/prodInOut.jsp?whoami=' + cal + '&formCondition=pi_idIS' +
					id + '&gridCondition=pd_piidIS' + id;
			}
		}
		return u;
	},
	getRelativeId: function(c, s) {
		var id = 0;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'ProdInOut',
	   			field: 'wmsys.wm_concat(pi_id)',
	   			condition: 'pi_inoutno in(' + c + ') and pi_class=\'' + s + '\''
	   		},
	   		method : 'post',
	   		callback : function(o, s, r){
	   			var rs = new Ext.decode(r.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return;
	   			}
    			if(rs.success){
    				if(rs.data != null){
    					id = rs.data;
    				}
	   			}
	   		}
		});
		return id;
	},
	getRelativeCaller: function(v) {
		var c = '';
		switch (v) {
			case '出货单':
				c = 'ProdInOut!Sale';break;
			case '销售退货单':
				c = 'ProdInOut!SaleReturn';break;
			case '拨出单':
				c = 'ProdInOut!AppropriationOut';break;
		}
		return c;
	}
});