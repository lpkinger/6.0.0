Ext.QuickTips.init();
Ext.define('erp.controller.WisdomPark.ActivityCenter', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
     		'core.form.Panel','core.trigger.AddDbfindTrigger','core.button.Save','core.button.AdvanceEnd',
     		'core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.trigger.DbfindTrigger',
     		'core.form.FtField','core.form.ConDateField','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Publish',
     		'core.form.MonthDateField','core.form.ConMonthDateField','core.button.Cancel'	
     		],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				me.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			afterrender:function(btn){
    				var status = Ext.getCmp('ac_status');
    				if(status&&status.value=='进行中'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender:function(btn){
    				var status = Ext.getCmp('ac_status');
    				if(status&&status.value=='已结束'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpPublishButton': {
    			afterrender:function(btn){
    				var status = Ext.getCmp('ac_status');
    				if(status&&(status.value=='进行中'||status.value=='已结束')){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg('确定发布活动', function(btn){
						if(btn == 'yes'){
							var form = b.up('form');
							var id = Ext.getCmp(form.keyField).value;
							me.FormUtil.setLoading(true);
							Ext.Ajax.request({
								url : basePath + form.publishUrl,
								params: {
									caller: caller,
									id: id
								},
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}else if(localJson.success){
										showMessage('活动发布成功！');
										window.location.reload();
									}
								}
							});
						}
					});
    			}
    		},
    		'erpCancelButton': {
    			afterrender:function(btn){
    				btn.setText('撤 销');
    				var status = Ext.getCmp('ac_status');
    				if(status&&status.value!='进行中'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg('确定撤销活动', function(btn){
						if(btn == 'yes'){
							var form = b.up('form');
							var id = Ext.getCmp(form.keyField).value;
							me.FormUtil.setLoading(true);
							Ext.Ajax.request({
								url : basePath + form.cancelUrl,
								params: {
									caller: caller,
									id: id
								},
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}else if(localJson.success){
										showMessage('活动撤销成功！');
										window.location.reload();
									}
								}
							});
						}
					});
    			}
    		},
    		'erpAdvanceEndButton': {
    			afterrender:function(btn){
    				var status = Ext.getCmp('ac_status');
    				if(status&&status.value!='进行中'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg('确定提前结束活动', function(btn){
						if(btn == 'yes'){
							var form = b.up('form');
							var id = Ext.getCmp(form.keyField).value;
							me.FormUtil.setLoading(true);
							Ext.Ajax.request({
								url : basePath + form.advanceEndUrl,
								params: {
									caller: caller,
									id: id
								},
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}else if(localJson.success){
										showMessage('活动提前结束成功！');
										window.location.reload();
									}
								}
							});
						}
					});
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('ActivityCenter', '新增活动', 'jsps/wisdomPark/activityCenter.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    }
});