Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.PurcMould', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','pm.mould.PurcMould','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField', 'core.form.FileField',
     		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
 			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
 			'core.button.TurnPurcProdIO', 'core.button.UpdatePayStatus', 'core.button.TurnFeePlease',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField'      
 	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('pm_statuscode');
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
					me.FormUtil.onDelete(Ext.getCmp('pm_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
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
					me.FormUtil.onAdd('addPurMould', '新增模具采购单', 'jsps/pm/mould/purcMould.jsp?whoami=Purc!Mould');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pm_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pm_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pm_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode'),
    					paystatus = Ext.getCmp('pm_paystatus'),
    					turnstatus = Ext.getCmp('pm_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(paystatus && paystatus.value != '未付款'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pm_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
	    			var condition = '{PURMOULD.pm_id}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
	    			var reportName="MouldPur";
			    	var id = Ext.getCmp(me.getForm(btn).keyField).value;
	    			me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			},
			'dbfindtrigger[name=pmd_pscode]': {
    			afterrender: function(t){
    				var grid = Ext.getCmp('grid');
    				var column = grid.down('gridcolumn[dataIndex=pmd_pscode]');
    				if(column && column.dbfind && column.dbfind.indexOf('MouldQuoteDetail') > -1) {
    					t.gridKey = "pm_vendcode";
    	    			t.mappinggirdKey = "mq_vendcode";
    	    			t.gridErrorMessage = "请填写供应商号!";
    				}
    			}
    		},
    		'multidbfindtrigger[name=pmd_pscode]': {
    			afterrender: function(t){
    				var grid = Ext.getCmp('grid');
    				var column = grid.down('gridcolumn[dataIndex=pmd_pscode]');
    				if(column && column.dbfind && column.dbfind.indexOf('MouldQuoteDetail') > -1) {
	    				t.gridKey = "pm_vendcode";
	    				t.mappinggirdKey = "mq_vendcode";
	    				t.gridErrorMessage = "请填写供应商号!";
    				}
    			}
    		},
			'erpTurnPurcProdIOButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode'), turnstatus = Ext.getCmp('pm_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具验收报告吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnYSReport.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pm_id').value
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
    	    		    					var id = r.id;
    	    		    					var url = "jsps/pm/mould/YSReport.jsp?whoami=YSReport!Mould&formCondition=mo_id=" + id + 
    	    		    						"&gridCondition=yd_moid=" + id ;
    	    		    					me.FormUtil.onAdd('YSReport' + id, '模具验收报告' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
			},
			'erpTurnFeePleaseButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('pm_statuscode'), turnstatus = Ext.getCmp('pm_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具付款申请单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/purcTurnFeePlease.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pm_id').value
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
    	    		    					var id = r.id;
    	    		    					var url = "jsps/common/commonpage.jsp?whoami=FeePlease!Mould&formCondition=mp_id=" + id + 
    	    		    						"&gridCondition=mfd_mpid=" + id ;
    	    		    					me.FormUtil.onAdd('MOULDFEEPLEASE' + id, '模具付款申请单' + id, url);
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