Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeNotice', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MakeNotice','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow','core.button.Get'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null){
    						item.set('mnd_code',Ext.getCmp('mn_code').value);
        			    	item.set('mnd_date',Ext.getCmp('mn_date').value);
        			    	item.set('mnd_kind',Ext.getCmp('mn_kind').value);
    						if(item.data['mnd_qty'] == null || item.data['mnd_qty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['mnd_detno'] + "行未填写数量，或需求为0");return;
    						}
    					}
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['mnd_planenddate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['mnd_detno'] + '行的计划完工日期为空');return;
    						}else if(item.data['mnd_planbegindate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['mnd_detno'] + '行的计划开工日期为空');return;
    						}else if(item.data['mnd_planenddate'] < item.data['mnd_planbegindate']){
    							bool = false;
    							showError('明细表第' + item.data['mnd_detno'] + '行的计划完工日期小于计划开工日期');return;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.beforeSave(this);
    				}
				}
			},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null){
    						item.set('mnd_code',Ext.getCmp('mn_code').value);
        			    	item.set('mnd_date',Ext.getCmp('mn_date').value);
        			    	item.set('mnd_kind',Ext.getCmp('mn_kind').value);
    						if(item.data['mnd_qty'] == null || item.data['mnd_qty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['mnd_detno'] + "行未填写数量，或需求为0");return;
    						}
    					}
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['mnd_planenddate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['mnd_detno'] + '行的计划完工日期为空');return;
    						}else if(item.data['mnd_planbegindate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['mnd_detno'] + '行的计划开工日期为空');return;
    						}else if(item.data['mnd_planenddate'] < item.data['mnd_planbegindate']){
    							bool = false;
    							showError('明细表第' + item.data['mnd_detno'] + '行的计划完工日期小于计划开工日期');return;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('mn_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMakeNotice', '新增制造通知单维护', 'jsps/pm/make/makeNotice.jsp?whoami=MakeNotice');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mn_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mn_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mn_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mn_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mn_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mn_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mn_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mn_id').value);
    			}
    		},
    		'dbfindtrigger[name=mnd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['mnd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sa_code='" + code + "'";
    				}
    			}
    		},
    	/*	'erpTurnMakeButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mn_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入制造单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/make/turnMake.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('mn_id').value
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
    	    		    					var url = "jsps/pm/make/makeBase.jsp?formCondition=ma_id=" + id + "&gridCondition=";
    	    		    					me.FormUtil.onAdd('Make' + id, '制造单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}*/
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});