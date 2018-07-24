Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.BillOut', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.button.PrintByCondition','core.form.Panel','fa.ars.BillOut','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.trigger.MultiDbfindTrigger','core.button.Post','core.button.ResPost',
      		'core.button.TicketTaxes', 'core.button.PrintVoucherCode','core.button.OpenInvoice',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			afterrender:function(g){
 				   me.BaseUtil.getSetting('BillOut', 'allowUpdatePrice', function(v) {
 					   g.plugins[0].on('beforeedit', function(args) {
                    		if(args.field == "ard_nowprice") {
                    			return v;
                    		}
                    	});
 				   });
	   			},
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.getAmount();
    				this.getTaxamount();
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bi_id').value);
    			}
    		},
    		'field[name=bi_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=bi_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpFormPanel': {
    			afterload: function(field) {
    				var status = Ext.getCmp('bi_statuscode'),refno = Ext.getCmp('bi_refno'),token = Ext.getCmp('bi_token');
    				if(status && status.value == 'POSTED' && token && token.value && refno && !refno.value){
    					var biid = Ext.getCmp('bi_id').value;
    					if(biid){
	    					Ext.Ajax.request({
					    		url: basePath + 'fa/ars/queryInvoiceInfo.action',
					    	   	params: {
					    	   		caller: caller,
						    		bi_id: biid
					    	   	},
						    	callback: function(opt, s, r) {
						    		var res = Ext.decode(r.responseText);
							   		if(res.exceptionInfo) {
							   			showError(res.exceptionInfo);
							   		} else if (res.success){
							   			if(res.data){
							   				refno && refno.setValue(res.data);
							   			}
							   		}
					   			}
					    	});
	    				}
    				}
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.getAmount();
    				this.getTaxamount();
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBillOut', '新增应收开票记录', 'jsps/fa/ars/billOut.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bi_statuscode');
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
    				var status = Ext.getCmp('bi_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bi_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bi_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bi_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bi_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bi_id').value);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bi_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('bi_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bi_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('bi_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    			 var reportName = '';
                 reportName = "billout_voice";
                 var condition = '{billout.bi_id}=' + Ext.getCmp('bi_id').value + '';
                 var id = Ext.getCmp('bi_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpPrintVoucherCodeButton': {
    			click: function(btn){
    				if(Ext.getCmp('bi_vouchercode').value ==null || Ext.getCmp('bi_vouchercode').value ==''){	
    					showError('当前发票还没有制作凭证，不能打印');
    				}else{
	    				 var reportName = '';
	   	                 reportName = "billout_voice";
	   	                 var condition = '{billout.bi_vouchercode}=\'' + Ext.getCmp('bi_vouchercode').value +'\'';
	   	                 var id=Ext.getCmp('bi_id').value;
	   	                 me.FormUtil.onwindowsPrint(id, reportName, condition);
	    			}
	    			 
    			}
    		},
    		'erpOpenInvoiceButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bi_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.openInvoice();
    			}
    		},
    		'erpTicketTaxesButton': {
    			click: function(btn){
    				var me = this, win = Ext.getCmp('ticketTaxes-win');
  				   	if(!win){
  					   var bi_refno = Ext.getCmp('bi_refno').value, bi_remark = Ext.getCmp('bi_remark').value,
  					   win = Ext.create('Ext.Window', {
  						   id: 'ticketTaxes-win',
  						   title: '更新税票信息',
  						   height: 200,
  						   width: 400,
  						   items: [{
  							   xtype: 'form',
  							   height: '100%',
  							   width: '100%',
  							   bodyStyle: 'background:#f1f2f5;',
  							   items: [{
  								   margin: '10 0 0 0',
  								   xtype: 'textfield',
  								   fieldLabel: '税票编号',
  								   name:'bi_refno',
  								   value: bi_refno
  							   },{
  								   margin: '10 0 0 0',
  								   xtype: 'textfield',
  								   fieldLabel: '备注',
  								   name:'bi_remark',
  								   value: bi_remark
  							   }],
  							   closeAction: 'hide',
  							   buttonAlign: 'center',
  							   layout: {
  								   type: 'vbox',
  								   align: 'center'
  							   },
  							   buttons: [{
  								   text: $I18N.common.button.erpConfirmButton,
  								   cls: 'x-btn-blue',
  								   handler: function(btn) {
  									   var form = btn.ownerCt.ownerCt,
  									   a = form.down('textfield[name=bi_refno]'),
  									   b = form.down('textfield[name=bi_remark]');
  									   if(form.getForm().isDirty()) {
  										   me.updateTaxcode(Ext.getCmp('bi_id').value, a.value, b.value);
  									   }
  								   }
  							   }, {
  								   text: $I18N.common.button.erpCloseButton,
  								   cls: 'x-btn-blue',
  								   handler: function(btn) {
  									   btn.up('window').hide();
  								   }
  							   }]
  						   }]
  					   });
  				   }
  				   win.show();
    			}
    		},
    		'dbfindtrigger[name=ard_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('bi_custcode')){
    					var code = Ext.getCmp('bi_custcode').value;
    					if(code != null && code != ''){
    						t.dbBaseCondition = "ab_custcode" + "='" + code + "'";
        				}
    				}
    			},
    			aftertrigger: function(t, r) {
    				if(Ext.getCmp('bi_custcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=ard_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['ard_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "abd_code='" + code + "'";
    				}
    			}
    		}
    	});
    }, 
    //计算发票金额   并写入主表总额字段
	getAmount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_ordercode'])){
				var a = Number(grid.BaseUtil.numberFormat(Number(item.data['ard_nowprice']),8));
	   	    	var b = Number(grid.BaseUtil.numberFormat(Number(item.data['ard_nowqty']),2));
	   	    	amount = amount + Number(grid.BaseUtil.numberFormat(a*b,2));
			}
		});
		Ext.getCmp('bi_amount').setValue(Ext.Number.toFixed(amount, 2));
	},
	
	//计算税金  然后写入主表税金总计字段
	getTaxamount:function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var taxsum = 0, differ = Ext.getCmp('bi_taxdiffer');
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_ordercode'])){
				var a = Number(grid.BaseUtil.numberFormat(Number(item.data['ard_nowprice']),8));
   	    		var b = Number(grid.BaseUtil.numberFormat(Number(item.data['ard_nowqty']),2));
   	    		var rate = Number(grid.BaseUtil.numberFormat(Number(item.data['ard_taxrate']),2));
   	    		taxsum = taxsum + Number(grid.BaseUtil.numberFormat((a*b*rate/100)/(1+rate/100),2));
			}
		});
		if(differ && !Ext.isEmpty(differ.value)){
			Ext.getCmp('bi_taxamount').setValue(Ext.Number.toFixed(taxsum+differ.value, 2));
		} else {
			Ext.getCmp('bi_taxamount').setValue(Ext.Number.toFixed(taxsum, 2));
		}
	},
	
    beforeSubmit:function(btn){
    	var me = this;
    	var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    	var amount = Number(Ext.getCmp('bi_amount').getValue());
    	var taxamount = Number(Ext.getCmp('bi_taxamount').getValue());
    	var detailamount = 0;
//    	Ext.each(items,function(item,index){
//    		if(Ext.Number.toFixed(item.data['ard_nowqty'], 2) > Ext.Number.toFixed(item.data['ard_qty'], 2)){
//    			//抛出异常
//    			showError('明细开票数量大于发票数量,不能提交');return;
//    		}
//    		detailamount = detailamount+Number(item.data['ard_nowbalance']);
//    		if(Ext.Number.toFixed(amount, 2) != Ext.Number.toFixed(detailamount, 2)){
//    			//抛出异常
//    			showError('明细开票方金额与开票总额不等,不能提交');return;
//    		}
//    	});
    	me.FormUtil.onSubmit(Ext.getCmp('bi_id').value);
    },
    getCodeCondition: function(){
		var	field = "ab_custcode";
		var	tFields = 'bi_custcode,bi_custname,bi_currency,bi_rate,bi_seller';
		var	fields = 'ab_custcode,ab_custname,ab_currency,ab_rate,ab_seller';
		var	tablename = 'ARBill';
		var	myfield = 'ab_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	var me = this.GridUtil || this;
   		var grid = selModel.ownerCt;
   		if(grid && !grid.readOnly && !grid.NoAdd){
   			var btn = grid.down('erpDeleteDetailButton');
   			if(btn)
   				btn.setDisabled(false);
   			btn = grid.down('copydetail');
   			if(btn)
   				btn.setDisabled(false);
   			btn = grid.down('pastedetail');
   			if(btn)
   				btn.setDisabled(false);
   			if(grid.down('tbtext[name=row]')){
   				grid.down('tbtext[name=row]').setText(index+1);
   			}
   		}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	updateTaxcode: function(biid, val1, val2) {
		var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/updateBillOutTaxcode.action',
    	   	params: {
    	   		caller: caller,
	    		bi_id: biid,
	    		bi_refno: val1,
	    		bi_remark: val2
    	   	},
	    	callback: function(opt, s, r) {
	    		var rs = Ext.decode(r.responseText);
		   		if(rs.exceptionInfo) {
		   			showError(rs.exceptionInfo);
		   		} else {
		   			alert('更新成功!');
		   			window.location.reload();
		   		}
   			}
    	});
	},
	openInvoice: function(){
		var me = this;
		var win = Ext.getCmp('showInvoicewin');
		
		if(win){
			win.show();
			return;
		}
		var token = Ext.getCmp('bi_token');
		
		if(token&&token.value){
			me.showInvoiceApply(Ext.decode(token.value));
		}else{
			var biid = Ext.getCmp('bi_id').value;
	    	Ext.Ajax.request({
	    		url: basePath + 'fa/ars/openInvoice.action',
	    	   	params: {
	    	   		caller: caller,
		    		bi_id: biid
	    	   	},
		    	callback: function(opt, s, r) {
		    		var res = Ext.decode(r.responseText);
			   		if(res.exceptionInfo) {
			   			showError(res.exceptionInfo);
			   		} else if (res.success){
			   			if(res.data.token){
			   				var token = Ext.getCmp('bi_token');
			   				token && token.setValue(res.data.token);
			   				me.showInvoiceApply(Ext.decode(res.data.token));
			   			}
			   			if(res.data.resMsg){
			   				showMessage(res.data.resMsg);
			   			}
			   		}
	   			}
	    	});
		}
	},
	showInvoiceApply:function(Token){
		var me = this;
		var token = Token[0].token;
		var url = 'http://saas.fapiaoxx.com';
		Ext.Ajax.request({
    		url: basePath + 'fa/ars/getTaxWebSite.action',
    	   	async: false,
	    	callback: function(opt, s, r) {
	    		var res = Ext.decode(r.responseText);
		   		if(res.exceptionInfo) {
		   			showError(res.exceptionInfo);
		   		} else if (res.success){
		   			if(res.url){
		   				url = res.url;
		   			}
		   		}
   			}
    	});
		url += '/piaoplus/external/invocieList.html?token=' + token;
		var win = Ext.getCmp('showInvoicewin');
		if(!win){
			win = Ext.create('Ext.window.Window', {
				title : '开具发票申请',
				height : '90%',
				width : 980,
				layout : 'fit',
				id : 'showInvoicewin',
				tag : 'iframe',
				html : '<iframe id="iframe_InvoiceApply" src="'+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
				buttonAlign:'center',
				buttons:[{
					xtype : 'button',
					text : '取消申请',
					iconCls : 'x-button-icon-close',
					cls : 'x-btn-gray',
					style : {
						marginLeft : '10px'
					},
					handler : function(btn) {
						me.cancelInvoiceApply(btn.ownerCt.ownerCt);
					}
				},{
					xtype : 'button',
					text : $I18N.common.button.erpCloseButton,
					iconCls : 'x-button-icon-close',
					cls : 'x-btn-gray',
					style : {
						marginLeft : '10px'
					},
					handler : function(btn) {
						btn.ownerCt.ownerCt.close();
					}
				}]
			});
		}
		win.show();
	},
	cancelInvoiceApply: function(win){
		var me = this;
		warnMsg('确定取消开具发票', function(btn){
			if(btn == 'yes'){
				var biid = Ext.getCmp('bi_id').value;
		    	Ext.Ajax.request({
		    		url: basePath + 'fa/ars/cancelInvoiceApply.action',
		    	   	params: {
		    	   		caller: caller,
			    		bi_id: biid
		    	   	},
			    	callback: function(opt, s, r) {
			    		var res = Ext.decode(r.responseText);
				   		if(res.exceptionInfo) {
				   			showError(res.exceptionInfo);
				   		} else if (res.success){
				   			if(res.resMsg){
				   				showMessage(res.resMsg);
				   			}else{
				   				showMessage("取消成功！");
				   			}
				   			var token = Ext.getCmp('bi_token');
				   			token && token.setValue(null);
				   			
				   			win.close();
				   		}
		   			}
		    	});
			}
		});
	}
});