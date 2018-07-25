Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.GoodsChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.GoodsChange','core.grid.Panel2','core.toolbar.Toolbar','core.button.Split',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.GoodsUpTurnOut','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
        var me=this;
    	this.control({  
    		'erpGridPanel2':{
    			itemclick: function(selModel, record){
    				this.onGridItemClick(selModel, record);
    			}
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
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('gc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('add' + caller, '新增商品变更单', 'jsps/pm/mps/goodsChange.jsp?whoami=' + caller);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('gc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('gc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('gc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('gc_id').value);
    			}
    		},
    		'erpGoodsUpTurnOutButton':{//转拨出单
    			click:function(btn){
    				me.GoodsChangeTurnOut(Ext.getCmp("gc_id").value,btn.ownerCt.ownerCt);
    			},
    			afterrender:function(btn){//已审核转拨出单,如果单据是下架单才允许转拨出单
    				var status = Ext.getCmp('gc_statuscode');
    				var type = Ext.getCmp('gc_type')
    				if((status && status.value != 'AUDITED')){
    					btn.hide();
    				}
    			}
    		},
    		'erpPrintButton': {//打印
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('gc_id').value);
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
	save: function(btn){
		var me = this;
		if(Ext.getCmp('gc_code').value == null || Ext.getCmp('gc_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	GoodsChangeTurnOut:function(id,form){
		var me = this;
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
         	url : basePath + "pm/mps/goodsChangeTurnOut.action",
         	params:{
         	  caller:caller,
         	  id:id
         	},
         	method : 'post',
         	callback : function(options,success,response){
         		me.FormUtil.setLoading(false);
         		var res = new Ext.decode(response.responseText);
         		if(res.exceptionInfo){
         			showError(res.exceptionInfo);return;
         		}else{
         			if(res.log)
	    				 showMessage('提示', res.log);
         		}
         	}
         });
	}

	
});