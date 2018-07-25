Ext.QuickTips.init();
Ext.define('erp.controller.oa.device.DeviceChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','oa.device.DeviceChange','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.TurnCustomer',
			'core.button.Upload','core.button.Update','core.button.FeatureDefinition','core.button.FeatureView','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
			'core.button.ResForBidden','core.button.Banned','core.button.ResBanned','core.button.CopyAll','core.button.CreateFeatrue',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.DealConfirmation','core.button.Modify','core.button.Banned'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						if(caller == "DeviceChange!Scrap"){
							me.BaseUtil.getRandomNumber(caller);
						}else{							
							me.BaseUtil.getRandomNumber();//自动添加编号
						}
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
			'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('dc_id').value);
    			}
    		},
			'erpUpdateButton': { 
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addDeviceChange', '新增设备变更单', 'jsps/oa/device/deviceChange.jsp?whoami='+ caller);
    			}
    		},
			'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('dc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('dc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('dc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('dc_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					btn.setText("转报废单");
					btn.setWidth(100);
					var status = Ext.getCmp('dc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
    				Ext.Ajax.request({
                        url: basePath + 'oa/device/turnScrap.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('dc_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
            				me.FormUtil.setLoading(false);
            				var res = new Ext.decode(response.responseText);
            				if(res.exceptionInfo != null){
            					showError(res.exceptionInfo);return;
            				}else if(res.data){
            					showMessage("提示", res.data);
            					Ext.Msg.alert("提示","转报废单成功!");
            				}
            			}
                    });
				}
			},
			'erpDealconfirmationButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('dc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
    				Ext.Ajax.request({
                        url: basePath + 'oa/device/confirmDeal.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('dc_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                    if (res.log == null || res.log == ''){
                                    	showMessage('提示', '信息处理成功');
                                    	window.location.reload();
                                    }
                                }
                            } else {
                                showMessage('提示', '信息处理失败');
                            }
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
	},
	resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
    			fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
		
			form.setHeight(70 + fh);
			grid.setHeight(height - fh - 70);
			this.resized = true;
		}
    }
});