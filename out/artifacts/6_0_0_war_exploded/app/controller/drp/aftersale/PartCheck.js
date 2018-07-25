Ext.QuickTips.init();
Ext.define('erp.controller.drp.aftersale.PartCheck', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.aftersale.PartCheck','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.TurnStorage','core.button.TurnCheck','core.button.Confirm',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
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
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPartCheck', '新增分检信息单', 'jsps/drp/aftersale/partCheck.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
            'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpConfirmButton': {
    			afterrender: function(btn){
    				var statu = Ext.getCmp('pc_statuscode');
    				var isConfirm=Ext.getCmp('pc_confirmstatus').value;
    				if(statu && statu.value != 'AUDITED'||isConfirm=='已处理'){
					btn.hide();
    				}
			},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('pc_id').value);
    				
    			}
    		} ,
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);
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
	onConfirm: function(id){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + 'drp/confirmPartCheck.action',
	   		params: {
	   			id: id,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				//audit成功后刷新页面进入可编辑的页面 
	   				window.location.reload();
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '确认成功');
    	   					window.location.reload();
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	}
});