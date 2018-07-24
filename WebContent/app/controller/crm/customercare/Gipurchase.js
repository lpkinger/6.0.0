Ext.QuickTips.init();
Ext.define('erp.controller.crm.customercare.Gipurchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customercare.Gipurchase','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.TurnOaacceptance',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	//Ext.getCmp('erpTurnOaacceptanceButton').setText("aaa");
    	//console.log(Ext.getCmp('form'));
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
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
    				me.FormUtil.onDelete(Ext.getCmp('gp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addGipurchase', '新增用品采购单', 'jsps/crm/customercare/gipurchase.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('gp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('gp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('gp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('gp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('gp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('gp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('gp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('gp_id').value);
				}
			},
			'erpTurnOaacceptanceButton':{
				beforerender: function(btn){
					btn.setText('转礼品验收');
				},
				afterrender: function(btn){
					var status = Ext.getCmp('gp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var isturn = Ext.getCmp('gp_isturn');
    				if(isturn && isturn.value != '0'){
    					btn.hide();
    				} 
				},
				click: function(btn){
					warnMsg("确定转验收单吗?", function(btn){
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
	    			   		url : basePath + 'crm/customercare/turnGiacceptance.action',
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
	    		    				Ext.Msg.alert('提示','转验收单成功！');
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