Ext.QuickTips.init();
Ext.define('erp.controller.oa.custom.MainDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.custom.MainDetail','core.form.Panel','core.grid.Panel2','core.form.CheckBoxGroup','core.toolbar.Toolbar','core.grid.YnColumn','core.button.Scan','core.button.Modify',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.form.ConDateHourMinuteField',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.DateHourMinuteField','core.button.ModifyDetail',
    			'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField','core.button.Confirm','core.button.ResConfirm',
    			'core.trigger.MultiDbfindTrigger','core.grid.detailAttach','core.form.MultiField','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.grid.ItemGrid',
    			'core.button.PrintByCondition','core.button.EditDetail','core.button.TurnBankRegister','core.button.CallProcedureByConfig','core.button.TurnSalePrice','core.button.TurnPurPrice'
    	,'core.button.TurnDocReturn','core.button.End','core.button.ResEnd','core.form.HrOrgSelectField'
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
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		//转银行登记
    		'erpTurnBankRegisterButton':{
    			click:function(btn){
    				me.turnBankRegister();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('CT_STATUSCODE');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpTurnPurPriceButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp(btn.ownerCt.ownerCt.statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTurnSalePriceButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp(btn.ownerCt.ownerCt.statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTurnDocReturnButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp(btn.ownerCt.ownerCt.statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onBanned(crid);
				}
			},
			'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'BANNED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResBanned(crid);
				}
			},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if((status && status.value != 'AUDITED')||(Ext.getCmp('ct_confirmstatus')&&Ext.getCmp('ct_confirmstatus').value=='已确认')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
  /*  		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},	
   */
    		'erpPrintButton': {
    			click: function(btn){
    				var condition = '{CUSTOMTABLE.CT_ID}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
    				me.FormUtil.onwindowsPrint2(Ext.getCmp(me.getForm(btn).keyField).value,'',condition);
    			}
    		},
    		'erpConfirmButton': {
		    	afterrender: function(btn){
					var status = Ext.getCmp(me.getForm(btn).statuscodeField);
					if((status && status.value != 'AUDITED')||(Ext.getCmp('ct_confirmstatus')&&Ext.getCmp('ct_confirmstatus').value=='已确认')){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var crid = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.FormUtil.onConfirm(crid);
    			}
    		},
    		'erpResConfirmButton': {
	    		afterrender: function(btn){
					var status = Ext.getCmp(me.getForm(btn).statuscodeField);
					if((status && status.value != 'AUDITED')||(Ext.getCmp('ct_confirmstatus')&&Ext.getCmp('ct_confirmstatus').value!='已确认')){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var crid = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.onResConfirm(crid);
    				
    			}
    		},
			'erpEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onEnd(crid);
				}
			},
			'erpResEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResEnd(crid);
				}
			},    		
    		'multifield[id=ct_varchar50_11]':{
    			afterrender: function(field){
    				if (caller=="FYBLRATE"){
					field.setReadOnly(false);
    				}
				}
    		},
    		'numberfield[id=ct_number_4]':{
    			afterrender: function(field){
    				if (caller=="FYBLRATE"){
					field.setReadOnly(false);
					var amount = Ext.getCmp("ct_amount").value;
					var hasturn = Ext.getCmp("ct_number_2").value;
					field.setValue(amount-hasturn);
    				}
				}
    		},
    		'erpYnField[name=me_isusedroom]': {
    			change: function(f){
    				if(f.value == 0){
    					var d = Ext.getCmp('me_customplace');
    					if(d){
    						d.show();
    					}
    				} else {
    					var d = Ext.getCmp('me_customplace');
    					if(d){
    						d.hide();
    					}
    				}
    			}
    		}    		
    	});
    },
	onGridItemClick: function(selModel, record){//grid行选择
		if(Ext.getCmp('fileform'))
		{Ext.getCmp('fileform').setDisabled(false);}	
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	turnBankRegister:function(){
		var form = Ext.getCmp('form');
		var catecode = Ext.getCmp('ct_varchar50_11').value;
		if(catecode == null || catecode == ''){
	        showMessage("警告", '请填写需要转银行登记的付款方信息!');
	        return;
	    }
		var thispayamount=form.BaseUtil.numberFormat(Ext.getCmp('ct_number_4').value,2);
		var back=0;//还款金额
		if(Ext.getCmp('ct_number_3')&&Ext.getCmp('ct_number_3').value!=null&&Ext.getCmp('ct_number_3').value!=''){
			back=form.BaseUtil.numberFormat(Ext.getCmp('ct_number_3').value,2);
		}
		if(form.BaseUtil.numberFormat(Ext.getCmp('ct_number_2').value+thispayamount,2) > form.BaseUtil.numberFormat(Ext.getCmp('ct_amount').value-back,2)){
			showMessage("警告", '本次转金额超出剩余金额!');
	        return;
		}
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'custom/turnBankRegister.action',
	   		params: {
	   			id:Ext.getCmp("CT_ID").value,
	   			paymentcode:Ext.getCmp('ct_varchar50_11').value,
	   			payment:Ext.getCmp('ct_varchar500_4').value,
	   			thispayamount:Ext.getCmp('ct_number_4').value,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					window.location.reload();
    				}
	   			}
	   		}
		});  
	},
	onResConfirm: function(id){
			var form = Ext.getCmp('form');	
			Ext.Ajax.request({
		   		url : basePath + form.resConfirmUrl,
		   		params: {
		   			id: id,
		   			caller:caller
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){    			
		   					window.location.reload();    				
		   			} 
		   			if (localJson.exceptionInfo) {
							showError(localJson.exceptionInfo);
					}
		   		}
			});
	}
});