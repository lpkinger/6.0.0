Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.BadDebtsAudit', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'core.button.PrintByCondition','fa.fp.BadDebtsAudit','core.form.Panel','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField','core.grid.Panel2',
		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.FormsDoc','core.button.CallProcedureByConfig',
		'core.button.Save','core.button.Print','core.button.TurnRecBalanceIMRE','core.button.Close','core.button.Upload','core.button.Update','core.button.Delete',
		'core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger', 'core.trigger.DbfindTrigger','core.form.YnField','core.form.MonthDateField'
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
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBadDebtsAudit', '新呆账处理审批', 'jsps/fa/fp/BadDebtsAudit.jsp');
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bda_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bda_statuscode');
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
    				var status = Ext.getCmp('bda_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bda_id').value);
    			}
    		}, 
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bda_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bda_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bda_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bda_id').value);
    			}
    		},
    		'erpPrintButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "BadDebtsAudit";
                    var condition = '{BadDebtsAudit.bda_id}=' + Ext.getCmp('bda_id').value + '';
                    var id = Ext.getCmp('bda_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpTurnRecBalanceIMREButton':{			   
            	click:function(btn){
    				me.turnRecBalanceIMER();
    			},
            	afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
            },
            'multidbfindtrigger[name=bdad_ordercode]': {
    			afterrender:function(trigger){
//	    			trigger.dbBaseCondition="ab_custcode='"+Ext.getCmp('bda_customcode').value+"'";
	    			trigger.dbKey='bda_customcode';
	    			trigger.mappingKey='ab_custcode';
	    			trigger.dbMessage='请先选择客户编号';
    			}
    		},
    		'field[name=bda_earliestdebtdate]': {
    			change: function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('bda_applydate')){
    					var e = Ext.util.Format.date(Ext.getCmp('bda_earliestdebtdate').value, 'Y-m-d');// 格式化日期控件值
    		            var s= Ext.util.Format.date(Ext.getCmp('bda_applydate').value, 'Y-m-d');// 格式化日期控件值
    		            var end = new Date(s); 
    		            var start = new Date(e); 
    		            var elapsed = Math.ceil((end.getTime() - start.getTime())/86400000); // 计算间隔月数
    					Ext.getCmp('bda_overdue').setValue(elapsed);
    				}
    			}
    		}
    	});
    },
    turnRecBalanceIMER: function(){
    	var me=this;
    	var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/BadDebtsAuditController/turnRecBalanceIMER.action',
	   		params: {
	   			id:Ext.getCmp("bda_id").value,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				turnSuccess(function(){
    					var id = localJson.id;
    					var url = "jsps/fa/ars/recBalance.jsp?formCondition=rb_id=" + id +"&gridCondition=rbd_rbid="+id+"&whoami=RecBalance!IMRE";
    					me.FormUtil.onAdd('RecBalance' + id, '冲应收款' + id, url);    					
    				});
	   			}
	   		}
		}); 			
    },
    beforeSubmit:function(btn){
    	var me = this;
    	me.FormUtil.onSubmit(Ext.getCmp('bda_id').value);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){// grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
	}
});
