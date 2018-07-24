Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.DelegationLetter', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.button.PrintByCondition','fa.fp.DelegationLetter','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.Upload',
    		'core.button.Save','core.button.Submit','core.button.ResSubmit','core.button.ResAudit','core.button.Audit',
  			'core.button.Sync','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
  			'core.button.PrintReceipt','core.button.PrintProxy','core.button.PrintDLBill','core.form.SeparNumber','core.button.End','core.button.ResEnd'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSaveButton': {
      			click: function(btn){
      				var form = me.getForm(btn);
      				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
      				if(Ext.getCmp('dgl_startdate').value && Ext.getCmp('dgl_enddate').value ){
	 					   var start=Ext.getCmp('dgl_startdate').value,
	 					   end=Ext.getCmp('dgl_enddate').value;
	 					   if(end.getTime()<start.getTime()){
	 						   showError('委托截止日期不能小于委托开始日期!');
	 						   return;
	 					   }
	 				}
					this.FormUtil.beforeSave(this);
				}
			},
			'field[name=dgl_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=dgl_recorddate]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=dgl_endreason]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('dgl_statuscode');
    				if(status && status.value == 'AUDITED'){
    					field.readOnly=false;
    				}
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('dgl_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('dgl_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('dgl_startdate').value && Ext.getCmp('dgl_enddate').value ){
	 					   var start=Ext.getCmp('dgl_startdate').value,
	 					   end=Ext.getCmp('dgl_enddate').value;
	 					   if(end.getTime()<start.getTime()){
	 						   showError('委托截止日期不能小于委托开始日期!');
	 						   return;
	 					   }
	 				}
    				me.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addDelegationLetter', '新增收款委托书', 'jsps/fa/fp/DelegationLetter.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('dgl_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('dgl_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('dgl_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('dgl_id').value);
				}
			},
			'erpEndButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
    				var endreason = '';
    				if(Ext.getCmp('dgl_endreason')){
    					endreason=Ext.getCmp('dgl_endreason').value;
    				}
    				this.onEnd(Ext.getCmp('dgl_id').value,endreason);
				}
			},
			'erpResEndButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'FINISH'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResEnd(Ext.getCmp('dgl_id').value);
				}
			},
    		'erpPrintProxyButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    			 var reportName = '';
                 reportName = "Delegation_proxy";
                 var condition = '{DelegationLetter.dgl_id}=' + Ext.getCmp('dgl_id').value + '';
                 var id = Ext.getCmp('dgl_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpPrintReceiptButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
       			 var reportName = '';
                    reportName = "Delegation_receipt";
                    var condition = '{DelegationLetter.dgl_id}=' + Ext.getCmp('dgl_id').value + '';
                    var id = Ext.getCmp('dgl_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
       			}
    		},
    		'erpPrintDLBillButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dgl_statuscode');
    				var receivestatus = Ext.getCmp('dgl_receivestatuscode');
					var dgl_receiptkind = Ext.getCmp('dgl_receiptkind').value;
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					if(dgl_receiptkind=='银行承兑'||dgl_receiptkind=='其它'){
						btn.hide();
					}
				},
    			click: function(btn){
       			 var reportName = '';
                    reportName = "Delegation_bank";
                    var condition = '{DelegationLetter.dgl_id}=' + Ext.getCmp('dgl_id').value + '';
                    var id = Ext.getCmp('dgl_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
       			}
    		},
			'erpSyncButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dgl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				}
			}
		});
	},
    beforeSubmit:function(btn){
    	var me = this;
    	me.FormUtil.onSubmit(Ext.getCmp('dgl_id').value);
    },
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	},   
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onEnd: function(id,endreason){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.endUrl && !me.FormUtil.contains(form.endUrl, '?caller=', true)){
			form.endUrl = form.endUrl + "?caller=" + caller;
		}
		form.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.endUrl,
			params: {
				id: id,
				endreason:endreason
			},
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					endSuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							endSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	}
});