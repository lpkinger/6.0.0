Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.MakeCraftPlant', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.MakeCraftPlant','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','pm.mes.DisplayPanel',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Split','core.button.ForceEnd',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.MakeCraftStepInfoUpdate',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.PrintByCondition','core.button.Print',
    		'core.trigger.TextAreaTrigger','core.form.MultiField','core.trigger.DbfindTrigger','core.button.End','core.button.ResEnd'
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
    				me.FormUtil.onDelete(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMakeCraft', '新增作业单', 'jsps/pm/mes/makeCraftPlant.jsp?whoami='+caller);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpSplitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				//me.FormUtil.onResAudit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('mc_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var id=Ext.getCmp('mc_id').value;
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/endMakeCraft.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id:id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				alert("结案成功！");
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            },
            'erpForceEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('mc_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定强制结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var id=Ext.getCmp('mc_id').value;
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/forceEndMakeCraft.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id:id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				alert("强制结案成功！");
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            },
            'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('mc_id').value);
    			}
    		},
    		'field[name=mc_prodcode]': {
    			beforetrigger: function(field) {
    				var code = Ext.getCmp('mc_makecode').value;
    				if(code == null || code == ''){
    					showError("请先选择制造单号!"); return;
    				} else {
    					field.findConfig = "mm_code='" + code + "'";
    				}
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