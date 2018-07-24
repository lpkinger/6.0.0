Ext.QuickTips.init();
Ext.define('erp.controller.oa.device.DeviceInventory', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
    		'oa.device.DeviceInventory','core.form.Panel','core.grid.Panel2','core.button.DeviceInventoryButton','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.DeviceAttributeButton','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync','core.button.Modify',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.button.ModifyDetail','core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber','core.button.AutoInvoice','core.button.DealConfirmation'
      	],
	init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
    		'erpFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('grid');
    				if(grid)
    					me.resize(form, grid);
    			}    			
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid){
    				var form = Ext.getCmp('form');
        			if(form)
        				me.resize(form, grid);
    			}
    		},
    		'erpModifyDetailCommonButton':{
				afterrender: function(btn){
					var status = Ext.getCmp(Ext.getCmp("form").statuscodeField);
					btn.setDisabled(true);
					if(status && status.value == 'COMMITED'){
						btn.setDisabled(false);
					}
				}
			},
    		'erpSaveButton': {
    			afterrender: function(btn){
    				var form = me.getForm(btn);
    				var codeField = Ext.getCmp(form.codeField);  
    				if(Ext.getCmp(form.codeField) && (Ext.getCmp(form.codeField).value != null && Ext.getCmp(form.codeField).value != '')){
    						btn.hide();
    					}
    			},
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeviceInventoryButton':{
    			afterrender: function(btn){
     				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && (status.value != 'COMMITED')){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				Ext.MessageBox.confirm('温馨提示', '确认无盘点结果的设备盘亏?',function(btn){
				  		if(btn=='yes'){	
				  			Ext.Ajax.request({
		                         url : basePath + 'oa/device/lossDeviceInventory.action',
		                         params: {
		                             id: Ext.getCmp('db_id').value,
		                             caller:caller
		                         },
		                         method : 'post',
		                         callback : function(options,success,response){
		                             var localJson = new Ext.decode(response.responseText);
		                             if(localJson.success){
		                            	 //todo 重新加载对应的数据
		                                 Ext.Msg.alert("提示","操作成功！");
		                                 window.location.reload();
		                             } else {
		                            	 showError(localJson.exceptionInfo);return;
		                             }
		                         }
		                     })
				  		}else{
	   						return;
	   					}
    			});
    		}},
    		'erpDeviceAttributeButton':{
    			afterrender: function(btn){
     				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && (status.value != 'COMMITED')){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				Ext.Ajax.request({
                        url : basePath + '/oa/device/getDeviceAttribute.action',
                        params: {
                            id: Ext.getCmp('db_id').value,
                            caller:caller
                        },
                        method : 'post',
                        callback : function(options,success,response){
                            var localJson = new Ext.decode(response.responseText);
                            if(localJson.success){
                           	 //todo 重新加载对应的数据
                                Ext.Msg.alert("提示","操作成功！");
                                window.location.reload();
                            } else {
                            	showError(localJson.exceptionInfo);return;
                            }
                        }
                    })
    			}
    		},
    		'field[name=dc_actionresult]':{
    			/*change: function(a){
    				console.log("beforeedit");
//    				var status = Ext.getCmp(me.getForm(btn).statuscodeField)
    			}*/
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
     				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
     				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('db_statuscode');
                    if(status && status.value != 'COMMITED'){
                        btn.hide();
                    }
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(caller =='DeviceBatch!Stock'){    					
    					if (!confirm('盘点不在库的设备是否执行盘亏?')){
    						return;
    					}
    				}
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);	
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpDealconfirmationButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('db_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
    				Ext.Ajax.request({
                        url: basePath + 'oa/device/confirmDeal.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('db_id').value
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
			},
    		'dbfindtrigger[name=mp_linecode]': {
				beforetrigger: function(t){
    				var wccode = Ext.getCmp('mp_wccode');
					if(wccode && wccode.value !='' && wccode.value != null){
						t.dbBaseCondition = " li_wccode='"+ wccode.value+"'";
				  }
    			}
    		}
    		})
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