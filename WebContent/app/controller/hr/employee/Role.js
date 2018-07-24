Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.Role', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.Role','core.form.Panel','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.button.Banned','core.button.ResBanned',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('Role', '新增角色', 'jsps/hr/employee/Role.jsp');
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
        				me.FormUtil.onDelete((Ext.getCmp('ro_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ro_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onSubmit(Ext.getCmp('ro_id').value);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ro_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('ro_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ro_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('ro_id').value);
        			}
        		},
        		'erpResAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ro_statuscode');
        				if(status && status.value != 'AUDITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResAudit(Ext.getCmp('ro_id').value);
        			}
        		},
        		//禁用按钮
        		'erpBannedButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ro_statuscode');
        				if(status && status.value != 'AUDITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.banned();
        			}
        		},
        		//反禁用按钮
        		'erpResBannedButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ro_statuscode');
        				if(status && status.value != 'DISABLE'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.resBanned();
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
    	banned: function(){
    		Ext.Ajax.request({
		   		url : basePath + 'hr/employee/bannedRole.action',
		   		params: {
		   			id: Ext.getCmp('ro_id').value, 
		   			caller: caller
		   		},
		   		method : 'post',
			   	callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						showMessage('提示', '禁用成功!', 1000);
						window.location.reload();
					}
			   	}
			});
    	},
    	resBanned: function(){
    		Ext.Ajax.request({
		   		url : basePath + 'hr/employee/resBannedRole.action',
		   		params: {
		   			id: Ext.getCmp('ro_id').value, 
		   			caller: caller
		   		},
		   		method : 'post',
			   	callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						showMessage('提示', '反禁用成功!', 1000);
						window.location.reload();
					}
			   	}
			});
    	}
});