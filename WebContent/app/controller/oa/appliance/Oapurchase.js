Ext.QuickTips.init();
Ext.define('erp.controller.oa.appliance.Oapurchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.appliance.Oapurchase','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.button.ResEnd','core.button.End',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.TurnOaacceptance',
    		'core.trigger.TextAreaTrigger','core.button.Print','core.trigger.DbfindTrigger'
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
    				me.FormUtil.onDelete(Ext.getCmp('op_id').value);
    			}
    		},
    		'erpPrintButton':{// 打印
    			click: function(btn){
    				var reportName = "OfficePurcList";
    				var condition = '{Oapurchase.op_id}=' + Ext.getCmp('op_id').value + '';
    				var id = Ext.getCmp('op_id').value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addOapurchase', '新增用品采购单', 'jsps/oa/appliance/oapurchase.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('op_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('op_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('op_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('op_id').value);
				}
			},
			'erpResEndButton':{
                afterrender: function(btn) {
                    var status = Ext.getCmp('op_statuscode');
                    if (status && status.value != 'FINISH') {
                        btn.hide();
                    }
                },
                click: function(btn) {
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/resEndOapurchase.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id:Ext.getCmp('op_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){    	    		    				
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});   					
                }            
			},
			'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('op_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/endOapurchase.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id:Ext.getCmp('op_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){    	    		    				
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            },
			'erpTurnOaacceptanceButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var isturn = Ext.getCmp('op_isturn');
    				if(isturn && isturn.value != '0'){
    					btn.hide();
    				} 
				},
				click: function(btn){
					warnMsg("确定要转入用品验收单吗?", function(btn){
						if(btn == 'yes'){
							var formdata = Ext.getCmp('form').getValues();
							var gridata = Ext.getCmp('grid').getStore().data.items;
							var jsonGridData = new Array();
							for(var i=0;i<gridata.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
								var data = gridata[i].data;
								jsonGridData.push(Ext.JSON.encode(data));
							}
						}
						Ext.Ajax.request({
	    			   		url : basePath + 'oa/appliance/turnOaacceptance.action',
	    			   		params: {
	    			   			formdata : Ext.JSON.encode(formdata).toString(),
	    			   			griddata : "["+jsonGridData.toString()+"]"
	    			   		},
	    			   		method : 'post',
	    			   		callback : function(options,success,response){
	    			   			var localJson = new Ext.decode(response.responseText);
	    			   			if(localJson.exceptionInfo){
	    			   				showError(localJson.exceptionInfo);
	    			   			}
	    		    			if(localJson.success){
	    		    				Ext.Msg.alert('提示','转用采购品验收单成功！');
	    			   			}
	    			   		}
	    				});
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