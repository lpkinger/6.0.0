Ext.QuickTips.init();
Ext.define('erp.controller.plm.cost.ProjectConfirm', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.cost.ProjectConfirm','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan',
    			'core.form.MonthDateField', 'core.form.ConDateField', 'core.form.ColorField','core.form.YnField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
       var me=this;
    	this.control({ 
    	    'erpSaveButton': {
    	    	click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
    		},
    		'monthdatefield': {
    			afterrender: function(f) {
    				me.getCurrentYearmonth(f);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectConfirm', '新增项目确认收入', 'jsps/plm/cost/projectConfirm.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
    			}
    		
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
    			}
    		},   
    	   'erpResAuditButton':{
    	      afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);
    			}
    	   },
    	   'field[name = pc_incomeamount]' : {
	   			change : function(f){
	   				var form = Ext.getCmp('form');
	   				var pc_incomeamount = Ext.getCmp('pc_incomeamount').value;		//收入金额
	   				var pc_rate = Ext.getCmp('pc_rate').value;
	   				var pc_amount = Ext.getCmp('pc_amount').value;				    //开票金额
	   				if(!Ext.isEmpty(pc_rate) && !Ext.isEmpty(pc_incomeamount)){
	   					var amount = form.BaseUtil.numberFormat(pc_incomeamount*(1+pc_rate/100),2);
	   					if(pc_amount != amount){
	   						Ext.getCmp('pc_amount').setValue(amount);
	   					}
	   				}
	   			}
   			},
   			'field[name = pc_rate]' : {
	   			change : function(f){
	   				var form = Ext.getCmp('form');
	   				var pc_incomeamount = Ext.getCmp('pc_incomeamount').value;		//收入金额
	   				var pc_rate = Ext.getCmp('pc_rate').value;
	   				var pc_amount = Ext.getCmp('pc_amount').value;				    //开票金额
	   				if(!Ext.isEmpty(pc_rate) && !Ext.isEmpty(pc_incomeamount)){
	   					var amount = form.BaseUtil.numberFormat(pc_incomeamount*(1+pc_rate/100),2);
	   					if(pc_amount != amount){
	   						Ext.getCmp('pc_amount').setValue(amount);
	   					}
	   				}
	   			}
   			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'plm/cost/getCurrentYearmonth.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				}
			}
		});
	}
});