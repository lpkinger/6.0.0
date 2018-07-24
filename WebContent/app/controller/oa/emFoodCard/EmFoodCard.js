Ext.QuickTips.init();
Ext.define('erp.controller.oa.emFoodCard.EmFoodCard', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','oa.emFoodCard.EmFoodCard','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
      		'core.button.Update','core.button.Delete','core.form.YnField',
      		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
      		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			afterrender: function(grid){
    				var status = Ext.getCmp('ef_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ef_id').value);
    			}
    		},    		
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ef_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPurchase', '新增餐卡记录', 'jsps/oa/emFoodCard/emFoodCard.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ef_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){    				
    					me.FormUtil.onSubmit(Ext.getCmp('ef_id').value);    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ef_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ef_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ef_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ef_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ef_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ef_id').value);
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
	
});