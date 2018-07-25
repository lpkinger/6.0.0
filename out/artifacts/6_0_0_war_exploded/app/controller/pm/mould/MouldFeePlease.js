Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.MouldFeePlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','pm.mould.MouldFeePlease','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField', 'core.form.SeparNumber',
     		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
 			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
 			'core.button.TurnPurcProdIO','core.form.FileField','core.button.UpdatePayStatus',
 			'core.button.TurnBankRegister','core.button.TurnBillAP','core.button.TurnBillARChange',
 			'core.button.End', 'core.button.ResEnd',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField'      
 	],
	init:function(){
		var me = this;
		this.control({
			'textfield[name=mp_thispayamount]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=mp_thispaydate]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=mp_bankcode]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('mp_statuscode');
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
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mp_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
	    			var condition = '{MOULDFEEPLEASE.mp_id}=' + Ext.getCmp('mp_id').value;
	    			var reportName="AccountRegZW_zw";
			    	var id = Ext.getCmp('mp_id').value;
	    			me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode');
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
					me.FormUtil.onAdd('addPurMould', '新增模具付款申请单', 'jsps/pm/mould/mouldFeePlease.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mp_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode'),
    					paystatus = Ext.getCmp('mp_paystatus');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(paystatus && paystatus.value != '未付款'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mp_id').value);
    			}
    		},
    		'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('mp_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onEnd(Ext.getCmp('mp_id').value);
                }
            },
            'erpResEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('mp_statuscode');
                    if (status && status.value != 'FINISH') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResEnd(Ext.getCmp('mp_id').value);
                }
            },
			//转银行登记
    		'erpTurnBankRegisterButton':{
    			click:function(btn){
    				me.turnBankRegister();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//应付票据付款
    		'erpTurnBillAPButton':{
    			click:function(btn){
    				me.turnBillAP();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//应付票据付款
    		'erpTurnBillARChangeButton':{
    			click:function(btn){
    				me.turnBillARChange();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
			'field[name=mfd_purccode]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('mp_vendcode')){
    					var code = Ext.getCmp('mp_vendcode').value;
    					if(code == null || code == ''){
        					showError("请先选择供应商!");   
        					t.setHideTrigger(true);
        					t.setReadOnly(true);
        				} else {      
        					t.dbBaseCondition = "pm_vendcode='" + code + "'";
        				}
    				}
    			}
    		},
    		'dbfindtrigger[name=mfd_yscode]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				var code = record.data['mfd_purccode'];
    				if(code == null || code == ''){
    					showError("请先选择模具采购单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "yd_mjhtcod='" + code + "'";
    				}
    			}
    		},
    		'dbfindtrigger[name=mfd_purcdetno]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				var code = record.data['mfd_purccode'];
    				if(code == null || code == ''){
    					showError("请先选择模具采购单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "pm_code='" + code + "'";
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
	turnBankRegister: function(){
		var me = this;
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('mp_bankcode').value;
        var thisamount = Ext.getCmp('mp_thispayamount').value, amount = Ext.getCmp('mp_payamount').value;
		var ppamount = Ext.getCmp('mp_total').value;
		var thispaydate = Ext.getCmp('mp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		if(thisamount-(ppamount-amount)>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
        if(catecode == null || catecode == ''){
        	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息!');
        	return;
        }
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'pm/mould/turnAccountRegister.action',
	   		params: {
	   			id: Ext.getCmp('mp_id').value,
	   			thisamount: thisamount,
	   			catecode : catecode,
	   			thisdate : thispaydate,
	   			caller : caller
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
    					var url = "jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank&formCondition=ar_id=" + id + "&gridCondition=ard_arid=" + id;
    					me.FormUtil.onAdd('AccountRegister' + id, '银行登记' + id, url);
    				});
	   			}
	   		}
		});
    },
    turnBillAP: function(){
    	var me = this;
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('mp_bankcode').value;
        var thisamount = Ext.getCmp('mp_thispayamount').value, amount = Ext.getCmp('mp_payamount').value;
		var ppamount = Ext.getCmp('mp_total').value;
		var thispaydate = Ext.getCmp('mp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		if(thisamount-(ppamount-amount)>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
        if(catecode == null || catecode == ''){
        	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息!');
        	return;
        }
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'pm/mould/turnBillAP.action',
	   		params: {
	   			id: Ext.getCmp('mp_id').value,
	   			thisamount: thisamount,
	   			catecode : catecode,
	   			thisdate : thispaydate,
	   			caller : caller
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
    					var url = "jsps/fa/gs/billAP.jsp?formCondition=bap_id=" + id;
    					me.FormUtil.onAdd('BillAP' + id, '应付票据' + id, url);
    				});
	   			}
	   		}
		});
    },
    turnBillARChange: function(){
    	var me = this;
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('mp_bankcode').value;
        var thisamount = Ext.getCmp('mp_thispayamount').value, amount = Ext.getCmp('mp_payamount').value;
		var ppamount = Ext.getCmp('mp_total').value;
		var thispaydate = Ext.getCmp('mp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		if(thisamount-(ppamount-amount)>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
        if(catecode == null || catecode == ''){
        	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息!');
        	return;
        }
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'pm/mould/turnBillARChange.action',
	   		params: {
	   			id: Ext.getCmp('mp_id').value,
	   			thisamount: thisamount,
	   			catecode : catecode,
	   			thisdate : thispaydate,
	   			caller : caller
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
    					var url = "jsps/fa/gs/billARChange.jsp?formCondition=brc_id=" + id + "&gridCondition=brd_brcid=" + id;
    					me.FormUtil.onAdd('BillARChange' + id, '应收票据异动' + id, url);
    				});
	   			}
	   		}
		});
    }
});