Ext.QuickTips.init();
Ext.define('erp.controller.crm.customercare.Giacceptance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customercare.Giacceptance','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.TurnOainstorage',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
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
    				me.FormUtil.onDelete(Ext.getCmp('ga_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addOaacceptance', '新增采购验收单', 'jsps/crm/customercare/giacceptance.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ga_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ga_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ga_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ga_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ga_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ga_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ga_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ga_id').value);
				}
			},
			'erpTurnOainstorageButton': {
				beforerender:function(btn){
					btn.setText('礼品入库');
				},
				afterrender: function(btn){
					var status = Ext.getCmp('ga_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var isturn = Ext.getCmp('ga_isturn');
    				if(isturn && isturn.value != '0'){
    					btn.hide();
    				}
				},
				click: function(btn){
					warnMsg("确定要礼品入库吗?", function(btn){
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
	    			   		url : basePath + 'crm/customercare/turninstorage.action',
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
	    		    				Ext.Msg.alert('提示','礼品入库成功！');
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