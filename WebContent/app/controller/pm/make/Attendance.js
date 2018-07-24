Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.Attendance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.Attendance','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.CopyAll',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger'
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
    				me.FormUtil.onDelete(Ext.getCmp('at_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('at_statuscode');
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
    				me.FormUtil.onAdd('addAttendance', '新增出勤日报', 'jsps/pm/make/attendance.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('at_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('at_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('at_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('at_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('at_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('at_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('at_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('at_id').value);
				}
			},
			'erpCopyButton': {
				click:function(btn){
					warnMsg("确定复制?", function(btn){
						if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/make/copyAttendance.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('at_id').getValue()
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
    	    		    					var url = "jsps/pm/make/attendance.jsp?formCondition=at_id=" + id + "&gridCondition=ad_atid=" + id;
    	    		    					me.FormUtil.onAdd('Attendance' + id, '出勤日报表' + id, url);
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
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('at_code').value;
		Ext.Array.each(items,function(item){
		   item.set('ad_code',c);
		   if(item.data['ad_kind']=="间接"){
			   item.set('ad_jjgs',Number(item.data['ad_zbgs'])+Number(item.data['ad_jbgs']));
		   }
		});
		this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('at_code').value;
		Ext.Array.each(items,function(item){
		   item.set('ad_code',c);
		   if(item.data['ad_kind']=="间接"){
			   item.set('ad_jjgs',Number(item.data['ad_zbgs'])+Number(item.data['ad_jbgs']));
		   }
		});
		this.FormUtil.onUpdate(this);
	}
});