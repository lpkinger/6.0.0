Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.lossworktime', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.lossworktime','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.CopyAll',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger',
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
    				//保存之前的一些前台的逻辑判定
    				this.beforeSave();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('lw_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('lw_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addLossWorkTime', '新增损失工时', 'jsps/pm/make/lossworktime.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('lw_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('lw_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('lw_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('lw_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('lw_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('lw_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('lw_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('lw_id').value);
				}
			},
			'erpCopyButton': {
				click:function(btn){
					warnMsg("确定复制?", function(btn){
						if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/make/copyLossWorkTime.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('lw_id').getValue()
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
    	    		    					var url = "jsps/pm/make/lossworktime.jsp?formCondition=lw_id=" + id + "&gridCondition=lwd_lwid=" + id;
    	    		    					me.FormUtil.onAdd('LossWorkTime' + id, '损失工时' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
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

	beforeSave: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('lw_code').value;
		var hours = 0;
		Ext.Array.each(items,function(item){
		   item.set('lwd_code',c);
		   if (!Ext.isEmpty(item.data['lwd_makecode']) && !Ext.isEmpty(item.data['lwd_prodcode'])) {
               if (item.data['lwd_losstime'] == null || item.data['lwd_losstime'] == '' || item.data['lwd_losstime'] == '0' || item.data['lwd_losstime'] == 0) {
                   bool = false;
                   showError('明细表第' + item.data['pd_detno'] + '行的损失时间为空');
                   return;
               }
           }
		});
		Ext.each(items,function(item,index){
			hours= hours + Number(item.data['lwd_checkmantime']);
		});
		Ext.getCmp('lw_lostedhours').setValue(hours);
		this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('lw_code').value;
		var hours = 0;
		Ext.Array.each(items,function(item){
		   item.set('lwd_code',c);
		   if (!Ext.isEmpty(item.data['lwd_makecode']) && !Ext.isEmpty(item.data['lwd_prodcode'])) {
               if (item.data['lwd_losstime'] == null || item.data['lwd_losstime'] == '' || item.data['lwd_losstime'] == '0' || item.data['lwd_losstime'] == 0) {
                   bool = false;
                   showError('明细表第' + item.data['pd_detno'] + '行的损失时间为空');
                   return;
               }
           }
		});
		Ext.each(items,function(item,index){
			hours= hours + Number(item.data['lwd_checkmantime']);
		});
		Ext.getCmp('lw_lostedhours').setValue(hours);
		this.FormUtil.onUpdate(this);
	}
});