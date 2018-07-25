Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.Quotation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.Quotation','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.Commonquery',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.Banned','core.button.ResBanned','core.button.TurnSale', 'core.button.AgreeToPrice','core.button.PrintByCondition',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField' ,'core.button.PrintByCondition','core.trigger.MultiDbfindTrigger'    
  	],
	init:function(){
		var me = this;
		this.control({
			'field[name=qu_custcode]' : {
				beforerender : function(field){
					var custid = getUrlParam('cuid');
					var cucode = getUrlParam('cucode');
					var cuname = getUrlParam('cuname');
					if(custid&&cucode&&cuname){
						field.setValue(cucode);
					}
				}
			},
			'field[name=qu_custname]' : {
				beforerender : function(field){
					var custid = getUrlParam('cuid');
					var cucode = getUrlParam('cucode');
					var cuname = getUrlParam('cuname');
					if(custid&&cucode&&cuname){
						field.setValue(cuname);
					}
				}
			},
			'field[name=qu_custid]' : {
				beforerender : function(field){
					var custid = getUrlParam('cuid');
					var cucode = getUrlParam('cucode');
					var cuname = getUrlParam('cuname');
					if(custid&&cucode&&cuname){
						field.setValue(custid);
					}
				}
			},
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'field[name=qu_source]' : {
				beforerender : function(field){
					if(field.value!=''&&field.value!=null){
						Ext.getCmp("qu_custcode").setReadOnly(true);
						Ext.getCmp("qu_currency").setReadOnly(true);
					}
				}
			},
			'field[name=qu_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=qu_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(new Date(value), 'Ym');
    				}
    			}
    		},
    		'field[name=qu_deliveryplace]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=qu_custid]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = "ca_cuid=" + value;
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.beforeSaveSale();
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('qu_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
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
					me.FormUtil.onAdd('addQuotation', '新增报价单', 'jsps/scm/sale/quotation.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('qu_id').value);
    			}
    		},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('qu_statuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('qu_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="bjd_price";
				var condition='{Quotation.qu_id}='+Ext.getCmp('qu_id').value+'';
				var id=Ext.getCmp('qu_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
			},
			/*'textfield[name=qu_currency]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var d = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('qd_pricetype',d);
						});
					}
				}
    		},
			'textfield[name=qu_rate]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var d = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('qd_rate',d);
						});
					}
				}
    		},*/
			'textfield[name=qu_delivery]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var d = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('qd_delivery',d);
						});
					}
				}
    		},
    		'erpAgreeToPriceButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode'),
    					pricestatus = Ext.getCmp('qu_pricestatus');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(pricestatus && pricestatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入价格库吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/quotation/toSalePrice.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('qu_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/sale/salePrice.jsp?whoami=SalePrice&formCondition=sp_id=" + id + "&gridCondition=spd_spid=" + id;
    	    		    					me.FormUtil.onAdd('SalePrice' + id, '价格库' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
			'erpTurnSaleButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode'),
    					turnstatus = Ext.getCmp('qu_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value=='TURNSA'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入销售单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/sale/quoturnSale.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('qu_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_id=" + id + "&gridCondition=sd_said=" + id;
    	    		    					me.FormUtil.onAdd('Sale' + id, '销售单' + id, url);
    	    		    					window.location.reload();
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
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
	beforeSaveSale: function(){
	 //Ext.getCmp('qu_cop').setValue(en_uu);
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('qu_custid').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('qd_code',Ext.getCmp('qu_code').value);
		});
		var items = grid.store.data.items;
		var bool = true;
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['qd_qty'] == null || item.data['qd_qty'] == '' || item.data['qd_qty'] == '0'
					|| item.data['qd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的数量为空');return;
				}
			}
		});
		/*//销售价格不能为0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['qd_price'] == null){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的价格为空');return;
				} else if(item.data['qd_price'] == 0 || item.data['qd_price'] == '0'){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的价格为0');return;
				}
			}
		});*/
		//物料交货日期不能小于录入日期
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['qd_delivery'] != null){
					if(item.data['qd_delivery'] < Ext.getCmp('qu_recorddate').value){
						bool = false;
						showError('明细表第' + item.data['qd_detno'] + '行的交货日期小于单据录入日期');return;
					}
				}
			}
		});
		//保存
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('qu_custid').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('qd_code',Ext.getCmp('qu_code').value);	    	
		});
		var items = grid.store.data.items;
		var bool = true;
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['qd_qty'] == null || item.data['qd_qty'] == '' || item.data['qd_qty'] == '0'
					|| item.data['qd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的数量为空');return;
				}
			}
		});
		/*Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['qd_price'] == null){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的价格为空');return;
				} else if(item.data['qd_price'] == 0 || item.data['qd_price'] == '0'){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的价格为0');return;
				}
			}
		});*/
		//物料交货日期不能小于录入日期
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['qd_delivery'] == null){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的交货日期为空');return;
				} else if(item.data['qd_delivery'] < Ext.getCmp('qu_recorddate').value){
					bool = false;
					showError('明细表第' + item.data['qd_detno'] + '行的交货日期小于单据录入日期');return;
				}
			}
		});
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	}
});