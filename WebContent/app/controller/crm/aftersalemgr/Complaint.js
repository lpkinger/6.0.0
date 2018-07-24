Ext.QuickTips.init();
Ext.define('erp.controller.crm.aftersalemgr.Complaint', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.aftersalemgr.Complaint','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.button.CallProcedureByConfig','core.button.Submit','core.button.ResSubmit',
    		'core.button.Audit','core.button.ResAudit'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					// 保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			},
    			afterrender:function(btn){
					  var dbfind=getUrlParam('dbfind');
						if(dbfind!=null){
							me.dbfindAndSetValue('form',caller,dbfind.split('=')[0],dbfind);
						}
					}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('co_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('co_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addComplaint', '客户投诉', 'jsps/crm/aftersalemgr/complaint.jsp');
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('co_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('co_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('co_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	dbfindAndSetValue:function(which,caller,field,condition){//key:sc_chcode,condition:sc_chcode like '%1%'
		Ext.Ajax.request({
				url : basePath + 'common/autoDbfind.action',
				params: {
		   			which: which,
		   			caller: caller,
		   			field: field,
		   			condition: condition
		   		},
		   		async: false,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var res = new Ext.decode(response.responseText);
		   			if(res.exceptionInfo){
		   				showError(res.exceptionInfo);return;
		   			}
		   			if(!res.data){
		   				return;
		   			}
		   			var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
		   			var form=Ext.getCmp('form');
		   			Ext.Array.each(res.dbfinds,function(db){
		   				if(Ext.getCmp(db.field)){
		   					Ext.getCmp(db.field).setValue(data[0][db.dbGridField]);
		   				}
		   			});
		   		}
		});
	}
});