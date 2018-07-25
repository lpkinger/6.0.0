Ext.QuickTips.init();
Ext.define('erp.controller.fs.buss.FsRepayment', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.buss.FsRepayment', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.SeparNumber',
			'core.button.Add','core.button.Save','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export','core.button.FormsDoc',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.trigger.AddDbfindTrigger', 'core.form.SeparNumber'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('FsRepayment', '还款申请', 'jsps/fs/buss/fsRepayment.jsp');
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('re_id').value);
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					me.beforeUpdate(false);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.beforeUpdate(true);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('re_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('re_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('re_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('re_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('re_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('re_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('re_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('re_id').value);
				}
			},
			'dbfindtrigger[name=re_aacode]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='re_custcode';
	    			trigger.mappingKey='aa_custcode';
	    			trigger.dbMessage='请先选择客户编号！';
    			}
    		},
			'field[name=re_yftotal]': {
				beforerender:function(f){
					f.labelStyle = 'color:blue';
				}
			},
			'field[name=re_yqtotal]': {
				beforerender:function(f){
					f.labelStyle = 'color:blue';
				}
			},
			'field[name=re_total]': {
				beforerender:function(f){
					f.labelStyle = 'color:blue';
				}
			}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate: function(isUpdate){
		var me = this;
		if(Ext.isEmpty(Ext.getCmp('re_custcode').value)){
        	showError('请选择客户编号！') ;  
			return; 
		}
    	if(Ext.isEmpty(Ext.getCmp('re_aacode').value)){
        	showError('请选择借据编号！') ;  
			return; 
		}
    	if(Ext.isEmpty(Ext.getCmp('re_thisamount').value) || Ext.getCmp('re_thisamount').value <= 0 ){
        	showError('还款金额需大于0！') ;  
			return; 
		}
    	if(Ext.getCmp('re_total').value < Ext.getCmp('re_thisamount').value){
    		showError('还款金额不能大于应还金额！') ;  
			return; 
    	}
//    	if(Ext.isEmpty(Ext.getCmp('re_backdate').value)){
//        	showError('请选择申请还款日期！') ;  
//			return; 
//		}
//    	if(Ext.Date.format(Ext.getCmp('re_backdate').value,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
//			showError('申请还款日期不能小于当前日期！'); return;
//		}
    	if(isUpdate){
    		me.FormUtil.onUpdate(this);
    	} else {
    		me.FormUtil.beforeSave(this);		
    	}
	},
});