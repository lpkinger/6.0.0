Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.RecBalanceNotice', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.ars.RecBalanceNotice','core.form.Panel', 'core.form.SeparNumber','core.form.YnField',
    		'core.form.MultiField','core.form.FileField','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.TurnBankRegister','core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.button.TurnBillAR',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger', 'core.trigger.MultiDbfindTrigger',
    		'core.button.PrintByCondition','core.button.Print'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender:function(g){
    				if(caller=='RecBalanceNotice!YS'){
    					me.BaseUtil.getSetting('sys', 'useBillOutAR', function(bool) {
    						if (bool) {
    							Ext.getCmp('bi_code').show();
    						}
    					});
    				}
    				g.plugins[0].on('beforeedit', function(args) {
    					if(args.field == "rbd_amount") {
                    		var bool = true;
                    		if (!Ext.isEmpty(args.record.get('rbd_zqty')) && args.record.get('rbd_zqty') != 0){
                    			bool = false;
                    		}
                    		return bool;
                    	}
                 	});
 	   			},
    			itemclick: this.onGridItemClick
			},
			'#bi_code':{
    			beforetrigger: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.isEmpty(Ext.getCmp("rb_custcode").value)) {
    					showError("请先选择客户!");
    					return false;
    				} else if (Ext.isEmpty(Ext.getCmp("rb_cmcurrency").value)){
    					showError("请先选择冲账币别!");
    					return false;
    				} else {
    					t.dbBaseCondition = " bi_custcode = '" + Ext.getCmp("rb_custcode").value + "' and bi_currency='" + Ext.getCmp("rb_cmcurrency").value + "'";
    				}
    			}
    		},
			'field[name=rb_catecode]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=rb_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=rb_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpPrintButton': {
                click: function(btn) {
                	 var id = Ext.getCmp('rb_id').value;
                     me.FormUtil.onwindowsPrint2(id, "", "");
                }
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
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRecBalanceNotice', '新增回款通知单', 'jsps/fa/ars/recBalanceNotice.jsp?whoami='+caller);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var rb_amount = Ext.getCmp('rb_amount').value;
    					rb_cmamount = Ext.getCmp('rb_cmamount').value;
    					rb_currency = Ext.getCmp('rb_currency').value;
    					rb_cmcurrency = Ext.getCmp('rb_cmcurrency').value;
    				if (rb_currency==rb_cmcurrency&&rb_amount!=rb_cmamount){
    					showError('回款金额与冲账金额不一致!');
    				}else{
    				me.FormUtil.onSubmit(Ext.getCmp('rb_id').value);}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpTurnBankRegisterButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('rb_catecode').value)){
    					showError("请先选择银行科目!");
    					return;
    				}
    				warnMsg("确定要转入银行登记吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/ars/turnAccountRegister.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('rb_id').value,
    	    			   			catecode: Ext.getCmp('rb_catecode').value,
    	    			   			caller: caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				showMessage("localJson", localJson.log);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpTurnBillARButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('rb_catecode').value)){
    					showError("请先选择票据科目!");
    					return;
    				}
    				warnMsg("确定要转入应收票据吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/ars/turnBillAR.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('rb_id').value,
    	    			   			catecode: Ext.getCmp('rb_catecode').value,
    	    			   			caller: caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				showMessage("localJson", localJson.log);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'dbfindtrigger[name=rbd_sacode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('rb_custcode')){
    					var code = Ext.getCmp('rb_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('rb_custcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		},
    		'multidbfindtrigger[name=rbd_sacode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('rb_custcode')){
    					var code = Ext.getCmp('rb_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					} else {
    						showError("请先选择客户编号!");
    						t.setHideTrigger(true);
        					t.setReadOnly(true);
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=rbd_abcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('rb_custcode')){
    					var code = Ext.getCmp('rb_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition2();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('rb_custcode')){
    					var obj = me.getCodeCondition2();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		},
    		'multidbfindtrigger[name=rbd_abcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('rb_custcode')){
    					var code = Ext.getCmp('rb_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition2();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					} else {
    						showError("请先选择客户编号!");
    						t.setHideTrigger(true);
        					t.setReadOnly(true);
    					}
    				}
    			}
    		},
    		//抓取发票信息
    		'button[name=catchab]':{
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var array = new Array();
    				var r = form.getValues();
    				var bars=grid.query('toolbar'),toolbar=bars[0],startdate,enddate;
       			   	var bicode=toolbar.items.items[2].value;
       			    startdate=toolbar.items.items[4].value;
       			    enddate=toolbar.items.items[6].value;
    				Ext.each(items,function(item,index){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						array.push(item);
    					}
    				});
    				var rb_id = Ext.getCmp('rb_id').value;
    				if(!rb_id||(rb_id&&(rb_id == 0||rb_id==''||rb_id==null))){
     					Ext.Msg.alert('提示','请先保存单据');
     				}else{
     					if(array.length>0){
        					Ext.Msg.alert('提示','需要先清除明细行中的数据!');
        				} else {
        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        					if(startdate!=null) params.startdate=Ext.Date.format(startdate,'Y-m-d');
                            if(enddate !=null)  params.enddate=Ext.Date.format(enddate,'Y-m-d');
                            if(bicode !=null)  params.bicode=bicode;
        					params.caller=caller;
        					//抓取
        					Ext.Ajax.request({
        				   		url : basePath + form.catchABUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				catchSuccess(function(){
        			    					window.location.reload();
        			    					/*//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   				var params = {
        					   						caller:caller,
        					   						condition:'rbd_rbid='+value
        					   				};
        					   				grid.GridUtil.loadNewStore(grid, params);*/
        			    				});
        				   			}
        			    			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
        				   		}
        					});
        				}
     				}
    			}
    		},
    		//清除发票信息
    		'button[name=cleanab]':{
    			click:function(btn){
    				var grid = Ext.getCmp('grid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						
    						Ext.Ajax.request({
        				   		url : basePath + form.cleanABUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				cleanSuccess(function(){
        			    					window.location.reload();
        			    					/*//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   				var params = {
        					   						caller:caller,
        					   						condition:'rbd_rbid='+value
        					   				};
        					   				grid.GridUtil.loadNewStore(grid, params);*/
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
        				   			} else{
        				   				cleanFailure();//@i18n/i18n.js
        				   			}
        				   		}
        					});
    					}else{
    						return;
    					}
    				});
    			}
    		}
    	});
    },
    getCodeCondition: function(){
		var field = "sa_apcustcode";
		var tFields = 'rb_custid,rb_custcode,rb_custname,rb_sellercode,rb_sellername,rb_departmentcode,rb_departmentname';
		var fields = 'cu_id,sa_apcustcode,cu_name,sa_sellercode,sa_seller,em_departmentcode,em_depart';
		var tablename = 'Sale left join Employee on sa_sellercode=em_code left join customer on sa_apcustcode=cu_code';
		var myfield = 'sa_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	getCodeCondition2: function(){
		var field = "ab_custcode";
		var tFields = 'rb_custid,rb_custcode,rb_custname,rb_sellercode,rb_sellername,rb_departmentcode,rb_departmentname';
		var fields = 'ab_custid,ab_custcode,ab_custname,ab_sellercode,ab_seller,em_departmentcode,em_depart';
		var tablename = 'ARBill left join Employee on ab_sellercode=em_code';
		var myfield = 'ab_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});