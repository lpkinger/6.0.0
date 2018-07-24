Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.YSReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.YSReport','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField', 'core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.TurnPurcProdIO','core.form.FileField','core.button.TurnMJProject','core.button.Post','core.button.ResPost',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('mo_statuscode');
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
					me.FormUtil.onDelete(Ext.getCmp('mo_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
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
					me.FormUtil.onAdd('addYSReport', '新增模具验收报告', 'jsps/pm/mould/YSReport.jsp?whoami=YSReport!Mould');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mo_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mo_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mo_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode'),
    					turnstatus = Ext.getCmp('mo_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mo_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
	    			var condition = '{MOD_YSREPORT.mo_id}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
	    			var reportName="model_ys";
			    	var id = Ext.getCmp(me.getForm(btn).keyField).value;
	    			me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			},
			'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			buffer : 1000,
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('mo_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('mo_id').value);
    			}
    		},
			'erpTurnMJProjectButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('mo_statuscode'), turnstatus = Ext.getCmp('mo_turnstatuscode');
    				if(status && status.value != 'AUDITED' && status.value != 'POSTED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转模具模具委托保管书吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnMJProject.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('mo_id').value,
    	    			   			caller : caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(o, s, res){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(res.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    			   			if(r.success){
    	    		    				turnSuccess(function(){
    	    		    					window.location.reload();
    	    		    					var id = r.id;
    	    		    					var url = "jsps/pm/mould/MJProject.jsp?whoami=MJProject!Mould&formCondition=ws_id=" + id + 
    	    		    						"&gridCondition=wd_wsid=" + id;
    	    		    					me.FormUtil.onAdd('MJProject' + id, '模具委托保管书' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});