Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.TurnBomPlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.TurnBomPlease','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Turnstandard','core.button.Flow','core.form.FileField'
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
					me.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('tp_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					me.FormUtil.onUpdate(this);
					
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('TurnBomPlease', '新增转标准BOM申请单', 'jsps/pm/bom/turnBomPlease.jsp?whoami=TurnBomPlease');
				}
			},
			'erpTurnstandardButton':{
    			afterrender:function(btn){
    				var statuscode=Ext.getCmp('tp_statuscode').value;
    				if(statuscode &&  statuscode !='AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				var id=Ext.getCmp('tp_id').value;    				
    				me.FormUtil.setLoading(true);
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/bom/turnStandard.action',
    			   		params: {
    			   			id: id,
    			   			caller:caller
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.FormUtil.setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    		    			if(localJson.success){
    		    				showMessage('提示', '转入成功!', 1000);
	    						window.location.reload();  		    				
    		    			} else {
    		    				if(localJson.exceptionInfo){    
    		    	   				showMessage("提示", localJson.exceptionInfo);return;
    		    	   			}
    		    			}
    			   		}
    			});
    		}
    		},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('tp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('tp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('tp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
						if(status && status.value != 'AUDITED'){
					btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('tp_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('tp_id').value);
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