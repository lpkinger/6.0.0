Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.BillOutAP', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','fa.arp.BillOutAP','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.SeparNumber',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
	       'core.button.ResSubmit','core.button.Flow','core.trigger.MultiDbfindTrigger','core.button.Post','core.button.ResPost',
	       'core.button.PrintVoucherCode', 'core.button.TicketTaxes','core.button.TurnYHFKSQ',
	       'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'erpGridPanel2': {
	    			   afterrender:function(g){
	    				   me.BaseUtil.getSetting('BillOutAP', 'allowUpdatePrice', function(v) {
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
	    		   'field[name=bi_currency]': {
	    			   beforetrigger: function(field) {
	    				   var t = field.up('form').down('field[name=bi_date]'),
	    				   value = t.getValue();
	    				   if(value) {
	    					   field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
	    				   }
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete(Ext.getCmp('bi_id').value);
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
	    				   me.FormUtil.onAdd('addBillOutAP', '新增应付开票记录', 'jsps/fa/arp/billOutAP.jsp');
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
	    			        if(status && (status.value != 'COMMITED'&&status.value != 'AUDITED')){
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
		                 reportName = "billoutap_voice";
		                 var condition = '{billoutap.bi_id}=' + Ext.getCmp('bi_id').value + '';
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
			   	                 reportName = "billoutap_voice";
			   	                 var condition = '{billoutap.bi_vouchercode}=\'' + Ext.getCmp('bi_vouchercode').value +'\'';
			   	                 var id=Ext.getCmp('bi_id').value;
			   	                 me.FormUtil.onwindowsPrint(id, reportName, condition);
			    			}
			    			 
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
	    				   if(Ext.getCmp('bi_vendcode')){
	    					   var code = Ext.getCmp('bi_vendcode').value;
	    					   if(code != null && code != ''){
	    						   t.dbBaseCondition = "ab_vendcode" + "='" + code + "'";
	    					   }
	    				   }
	    			   },
	    			   aftertrigger: function(t, r) {
	    				   if(Ext.getCmp('bi_vendcode')){
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
	    		   },
	    		   'erpTurnYHFKSQButton':{
	    			   afterrender: function(btn){
		    			   var status = Ext.getCmp('bi_statuscode');
		    			   if(status && status.value != 'POSTED'){
		    				   btn.hide();
		    			   }
	    			   },
	    			   click: function(m){
	    				   warnMsg("确定生成付款申请?", function(btn){
	    					   if(btn == 'yes'){
	       							me.FormUtil.getActiveTab().setLoading(true);//loading...
	       							Ext.Ajax.request({
	       								url : basePath + 'fa/arp/billoutToPayPlease.action',
	       								params: {
	    	   	    			   			id: Ext.getCmp('bi_id').value,
	    	   	    			   			caller: caller
	    	   	    			   		},
	    	   	    			   		method : 'post',
	    		   	    			   	callback : function(options,success,response){
	    		   	    		   			me.FormUtil.getActiveTab().setLoading(false);
	    		   	    		   			var r = new Ext.decode(response.responseText);
	    		   	    		   			if(r.exceptionInfo){
	    		   	    		   				showError(r.exceptionInfo);
	    		   	    		   			}
	    		   	    		   			if(r.success){
	    		   	    		   				if(r.content && r.content.pp_id){
	    		   	    	    					showMessage("提示", "转入成功,付款申请单号: <a href=\"javascript:openUrl2('jsps/fa/arp/payplease.jsp?formCondition=pp_idIS" + r.content.pp_id
	    		   	    	    							 + "&gridCondition=ppd_ppidIS" + r.content.pp_id + "','付款申请单','pp_id'," + r.content.pp_id
	    		   	    	    							 + ")\">" + r.content.pp_code + "</a>");
	    		   	    	    				}
	    		   	    	    				window.location.reload();
	    		   	    		   			}
	    		   	    		   		}
	    	   	    				});
	    	   					}
	       					});
	    			   }
	    		   },
	    	   });
	       }, 
	       getCodeCondition: function(){
	    	   var	field = "ab_vendcode";
	    	   var	tFields = 'bi_vendcode,bi_vendname,bi_currency,bi_rate';
	    	   var	fields = 'ab_vendcode,ab_vendname,ab_currency,ab_rate';
	    	   var	tablename = 'APBill';
	    	   var	myfield = 'ab_code';
	    	   var obj = new Object();
	    	   obj.field = field;
	    	   obj.fields = fields;
	    	   obj.tFields = tFields;
	    	   obj.tablename = tablename;
	    	   obj.myfield = myfield;
	    	   return obj;
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
//		   			Ext.getCmp('bi_taxamount').setValue(Ext.util.Format.number(taxsum+differ.value, "0.00"));
		   			Ext.getCmp('bi_taxamount').setValue(Ext.Number.toFixed(taxsum+differ.value, 2));
		   			console.log("215A"+Ext.Number.toFixed(taxsum+differ.value, 2));
		   		} else {
//		   			Ext.getCmp('bi_taxamount').setValue(Ext.util.Format.number(taxsum, "0.00"));
		   			
		   			Ext.getCmp('bi_taxamount').setValue(Ext.Number.toFixed(taxsum, 2));
		   			console.log("220A"+Ext.Number.toFixed(taxsum, 2));
		   		}
		   	},
	       beforeSubmit:function(btn){
	    	   var me = this;
	    	   var grid = Ext.getCmp('grid'),items=grid.store.data.items;
	    	   var amount = Number(Ext.getCmp('bi_amount').getValue());
	    	   var detailamount = 0;
//	    	   Ext.each(items,function(item,index){
//	    	   if(Ext.Number.toFixed(item.data['ard_nowqty'], 2) > Ext.Number.toFixed(item.data['ard_qty'], 2)){
//	    	   //抛出异常
//	    	   showError('明细开票数量大于发票数量,不能提交');return;
//	    	   }
//	    	   detailamount = detailamount+Number(item.data['ard_nowbalance']);
//	    	   if(Ext.Number.toFixed(amount, 2) != Ext.Number.toFixed(detailamount, 2)){
//	    	   //抛出异常
//	    	   showError('明细开票方金额与开票总额不等,不能提交');return;
//	    	   }
//	    	   });
	    	   me.FormUtil.onSubmit(Ext.getCmp('bi_id').value);
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
//		   			if(grid.down('tbtext[name=row]')){
//		   				grid.down('tbtext[name=row]').setText(index+1);
//		   			}
		   		}
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       updateTaxcode: function(biid, val1, val2) {
	   		var me = this;
	    	   	Ext.Ajax.request({
	    	   		url: basePath + 'fa/arp/updateBillOutTaxcode.action',
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
	       }
});