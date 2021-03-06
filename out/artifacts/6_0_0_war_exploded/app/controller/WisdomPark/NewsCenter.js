Ext.QuickTips.init();
Ext.define('erp.controller.WisdomPark.NewsCenter', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
     		'core.form.Panel','core.form.ExtKindEditor','core.trigger.AddDbfindTrigger','core.button.Save',
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
    				var status = Ext.getCmp('nc_status');
    				if(status&&status.value=='已发布'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('nc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			/*afterrender:function(btn){
    				var status = Ext.getCmp('nc_status');
    				if(status&&status.value=='已发布'){
    					btn.hide();
    				}
    			},*/
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpPublishButton': {
    			afterrender:function(btn){
    				var status = Ext.getCmp('nc_status');
    				if(status&&status.value=='已发布'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg('确定发布新闻', function(btn){
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
										showMessage('新闻发布成功！');
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
    				var status = Ext.getCmp('nc_status');
    				if(status&&status.value!='已发布'){
    					btn.hide();
    				}
    			},
    			click: function(b){
    				warnMsg('确定撤销新闻', function(btn){
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
										showMessage('新闻撤销成功！');
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
    				me.FormUtil.onAdd('NewsCenter', '新增新闻', 'jsps/wisdomPark/newsCenter.jsp');
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