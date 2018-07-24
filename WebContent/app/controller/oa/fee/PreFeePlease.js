Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.PreFeePlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.fee.OrderFood','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan',
    		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.TurnBankDJ','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.button.TurnFYBX'
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
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('fp_id').value);
				}
			},
			'dbfindtrigger[name=fpd_d1]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='fp_department';
	    			trigger.mappingKey='fcs_departmentname';
	    			trigger.dbMessage='请先选择申请部门';
    			}
    		},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPreFeePlease', '新增费用申请', 'jsps/oa/fee/preFeePlease.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('fp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fp_statuscode');					
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('fp_id').value);
				}
			},
			'erpTurnFYBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				var turnstatus=Ext.getCmp('fp_v7');
    				if((status && status.value != 'AUDITED')||turnstatus.value=='已转'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入费用报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/prefeeplease/turnFYBX.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('fp_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/oa/fee/feePleaseFYBX.jsp?whoami=FeePlease!FYBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '费用报销单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fp_statuscode');
					var turnstatus=Ext.getCmp('fp_v7');
					if((status && status.value != 'AUDITED')||turnstatus.value=='已转'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('fp_id').value);
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