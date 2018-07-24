Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.SYSJob', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.SYSJob','core.toolbar.Toolbar','core.form.FtField','core.button.ResSubmit','core.button.ResAudit',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.Test','core.button.RunSYSJobNow',
    		'core.button.Enable','core.button.Banned'
    	],
    init:function(){
    var me=this;
    	this.control({ 
    		'erpGridPanel2': {  
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(this);
    			}
    		}, 
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);    				
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp("sj_id").value);
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addJOB', '新增JOB', 'jsps/pm/mps/SYSJob.jsp?whoami=' + caller);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp("sj_id").value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode"); 
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp("sj_id").value);
    			}
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp("sj_id").value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp("sj_id").value);
    			}
    		} ,
    		'erpBannedButton':{
    			click: function(btn){
    				me.FormUtil.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'pm/mps/stopOracleJob.action',
    					params: {
    						id: Ext.getCmp('sj_id').value,
    						caller:caller
    					},
    					method: 'post',
    					callback: function(options, success, response){
    						me.FormUtil.setLoading(false);
    						var res = new Ext.decode(response.responseText);
							window.location.href = window.location.href;
    						if(res.exceptionInfo) {
    							showError(res.exceptionInfo);
    						} else {
    							showMessage("提示","禁用成功");
    						}
    					}
    				});
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		
    		},
    		'erpEnableButton':{
    			click: function(btn){
    				me.FormUtil.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'pm/mps/enableOracleJob.action',
    					params: {
    						id: Ext.getCmp('sj_id').value,
    						caller:caller
    					},
    					method: 'post',
    					callback: function(options, success, response){
    						me.FormUtil.setLoading(false);
    						var res = new Ext.decode(response.responseText);
					  		window.location.href = window.location.href;
    						if(res.exceptionInfo) {
    							showError(res.exceptionInfo);
    						} else {
    							showMessage("提示","启用成功");
    						}
    					}
    				});
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'DISABLE'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTestButton': {  
    			click: function(btn){
    				Ext.Ajax.request({
    					url: basePath + 'pm/mps/testOracleJob.action',
    					params: {
    						id: Ext.getCmp('sj_id').value,
    						caller:caller
    					},
    					method: 'post',
    					callback: function(options, success, response){
    						var res = new Ext.decode(response.responseText);
    						if(res.exceptionInfo) {
    							showError(res.exceptionInfo);
    						} else {
    							alert("测试通过");
    							window.location.reload();
    						}
    					}
    				});
    			}
    		},
    		'erpRunSYSJobNowButton':{
    			click: function(btn){
    				me.FormUtil.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'pm/mps/runOracleJob.action',
    					params: {
    						id: Ext.getCmp('sj_id').value,
    						caller:caller
    					},
    					method: 'post',
    					timeout:'1200000',
    					callback: function(options, success, response){
    						me.FormUtil.setLoading(false);
    						var res = new Ext.decode(response.responseText);
    						if(res.exceptionInfo) {
    							showError(res.exceptionInfo);
    						} else {
    							showMessage("提示","执行成功");
    						}
    					}
    				});
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp("sj_statuscode");
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'textfield[name=sj_jobname]': {
    			beforerender: function(field){
					if(field.value != null && field.value != ''){
						field.readOnly=true; 
					}
				}
    		}
    	});
    }, 
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form=Ext.getCmp('form'); 
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	} 
});