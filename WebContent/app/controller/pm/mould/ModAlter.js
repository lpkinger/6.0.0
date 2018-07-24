Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.ModAlter', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.ModAlter','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.TurnFeePlease','core.button.TurnAPBill','core.button.TurnSale','core.button.TurnOffPrice',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('al_statuscode');
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
					me.FormUtil.onDelete(Ext.getCmp('al_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode');
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
					me.FormUtil.onAdd('addModAlter', '新增修改申请单', 'jsps/pm/mould/modAlter.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('al_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('al_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('al_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode'),
    					turnstatus = Ext.getCmp('al_turnsalecode'),
    					pricestatus = Ext.getCmp('al_turnpricecode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNSA'){
    					btn.hide();
    				}
    				if(pricestatus && pricestatus.value == 'TURNPM'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('al_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('al_id').value);
				}
			},
			'dbfindtrigger[name=ald_psdetno]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['ald_pscode'];
    				if(code == null || code == ''){
    					showError("请先选择模具编号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "ps_code='" + code + "'";
    				}
    			}
    		},
    		'erpTurnAPBillButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode');
    				var isturnbill = Ext.getCmp('al_isturnbill');
    				if((status && status.value != 'AUDITED')||(isturnbill&&isturnbill.value!='否')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具发票吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnAPBill.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('al_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(o, s, res){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(res.responseText);
    	    			   			if(r.exceptionInfo){
						   				showError(r.exceptionInfo);
						   			}else{
						   				showMessage("提示", r.log);
					    				window.location.reload();
						   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
			},
			'erpTurnFeePleaseButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具付款申请单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnFeePlease.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('al_id').value
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
    	    		    					var url = "jsps/pm/mould/mouldFeePlease.jsp?whoami=FeePlease!Mould&formCondition=mp_id=" + id + 
    	    		    						"&gridCondition=mfd_mpid=" + id ;
    	    		    					me.FormUtil.onAdd('MOULDFEEPLEASE' + id, '模具付款申请单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
			},
			'erpTurnSaleButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode'),
    					turnstatus = Ext.getCmp('al_turnsalecode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNSA'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模销售单吗?", function(btn){
    					if(btn == 'yes'){
    						var id = Ext.getCmp('al_id').value;
    						me.FormUtil.setLoading(true);
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/alterTurnMouldSale.action',
    	    			   		params: {
    	    			   			id: id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
						   			me.FormUtil.setLoading(false);
						   			var localJson = new Ext.decode(response.responseText);
						   			if(localJson.exceptionInfo){
						   				showError(localJson.exceptionInfo);
						   				return "";
						   			}
					    			if(localJson.success){
					    				if(localJson.log){
					    					showMessage("提示", localJson.log);
					    				}
					    				window.location.reload();
						   			}
			   					}
    	    				});
    					}
    				});
    			}
			},
			'erpTurnOffPriceButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('al_statuscode'), turnstatus = Ext.getCmp('al_turnpricecode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNPM'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具报价单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/alterTurnPriceMould.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('al_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(o, s, res){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(res.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success && r.id){
    	    		    				var id = r.id, msg = '模具报价单号:<br>';
	    		    					for(var i in id) {
	    		    						msg += '<a href="javascript:openUrl(\'jsps/pm/mould/priceMould.jsp?formCondition=pd_id=' 
	    		    							+ id[i].id + '&gridCondition=pmd_pdid=' + id[i].id + '\');">' + id[i].code + '</a><hr>';
	    		    					}
	    		    					showMessage('提示', msg);
    	    			   			}
    	    			   			window.location.reload();
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