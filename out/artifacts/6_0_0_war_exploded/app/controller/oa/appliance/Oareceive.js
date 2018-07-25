Ext.QuickTips.init();
Ext.define('erp.controller.oa.appliance.Oareceive', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.appliance.Oareceive','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.GetOaapplication',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Print','core.button.ResPost'
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
    		'erpPrintButton':{// 打印
    			click: function(btn){
    				var reportName = "OfficePIOlist";
    				var condition = '{Oareceive.or_id}=' + Ext.getCmp('or_id').value + '';
    				var id = Ext.getCmp('or_id').value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('or_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addOareceive', '新增领用申请单', 'jsps/oa/appliance/oareceive.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('or_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('or_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('or_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('or_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('or_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('or_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('or_statuscode');
					if(status && status.value != 'AUDITED' ||Ext.getCmp("or_getapp").value=='1'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('or_id').value);
				}
			},
			'erpGetOaapplicationButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('or_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				var isturn = Ext.getCmp('or_getapp');
    				if(isturn && isturn.value !='0'){
    					btn.hide();
    				}
    			},
				click: function(){
					warnMsg("确定要领料吗?", function(btn){
    					if(btn == 'yes'){
							var gridata = Ext.getCmp('grid').getStore().data.items;
							var jsonGridData = new Array();
							for(var i=0;i<gridata.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
								var data = gridata[i].data;
								jsonGridData.push(Ext.JSON.encode(data));
							}
	
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/getOaapplication.action',
    	    			   		params: {
    	    			   			griddata : "["+jsonGridData.toString()+"]",
    	    			   			id : Ext.getCmp('or_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				Ext.Msg.alert('提示','领料成功！');
    	    		    				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
				}
			},
			'erpResPostButton':{
				beforerender:function(btn){
					btn.setText('退 料');
				},
				afterrender: function(btn){
    				var status = Ext.getCmp('or_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				var isturn = Ext.getCmp('or_getapp');
    				if(isturn && isturn.value !='1'){
    					btn.hide();
    				}
    			},
				click: function(){
					warnMsg("确定要退料吗?", function(btn){
    					if(btn == 'yes'){
							var gridata = Ext.getCmp('grid').getStore().data.items;
							var jsonGridData = new Array();
							for(var i=0;i<gridata.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
								var data = gridata[i].data;
								jsonGridData.push(Ext.JSON.encode(data));
							}
	
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/appliance/returnOaapplication.action',
    	    			   		params: {
    	    			   			griddata : "["+jsonGridData.toString()+"]",
    	    			   			id : Ext.getCmp('or_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				Ext.Msg.alert('提示','退料成功！');
    	    		    				window.location.reload();
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
	}
});