Ext.QuickTips.init();
Ext.define('erp.controller.oa.storage.Propertyrepair', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'oa.storage.Propertyrepair','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
   		'core.button.Update','core.button.Delete','core.form.YnField','core.button.TurnFYBX',
   		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Turnrepair','core.form.MultiField'
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
    				me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPropertyrepair', '新增维修申请', 'jsps/oa/storage/propertyrepair.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					var turnstatus=Ext.getCmp('pr_turnstatus');
					if((status && status.value != 'AUDITED')||turnstatus=='已转'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpTurnFYBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pr_statuscode');
    				var turnstatus=Ext.getCmp('pr_turnstatus');
    				if((status && status.value != 'AUDITED')||turnstatus=='已转'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入费用报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/storage/turnFYBX.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('pr_id').value
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
			'erpTurnrepairButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var isuse = Ext.getCmp('pr_isuse').value;
					if(isuse == '1'){
						btn.hide();
					}
				},
				click: function(btn){
					var param = this.GridUtil.getAllGridStore();
					param = "[" + param.toString() + "]";
					var id = Ext.getCmp('pr_id').value;
					warnMsg('确定要转资产维修记录吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "oa/storage/Propertyrepair.action",
								params:{
									param:param,
									id:id
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "转资产维修记录成功！");
										btn.hide();
									}else{
										Ext.Msg.alert("提示", "转资产维修记录失败！");
									}
								}
							});
						} else {
							return;
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