Ext.QuickTips.init();
Ext.define('erp.controller.oa.publicAdmin.book.basicData.BookStorage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.publicAdmin.book.basicData.BookStorage','core.form.Panel','core.grid.Panel2','core.button.Scan',
		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.DeleteDetail',
		'core.button.ResAudit','core.button.Flow','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
		'core.form.YnField','core.trigger.AutoCodeTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger'
	],
    init:function(){
    	var me = this;
        	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(grid){   				
    				var str = me.GridUtil.getGridStore();
    				if(str != null || str != ''){//说明grid加载时带数据
    					me.alloweditor = false;
    				}
    			}
    		},
    		'field[name=bs_returndays]':{
    			afterrender:function(f){
    				f. allowDecimals=false;   			
    			}
    		},
    		'field[name=bs_warndays]':{
    			afterrender:function(f){
    				f. allowDecimals=false;   			
    			}
    		},
    		'field[name=bs_recalldays]':{
    			afterrender:function(f){
    				f. allowDecimals=false;   			
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
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('bs_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addBookStorage', '新增仓库', 'jsps/oa/publicAdmin/book/basicData/bookStorage.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);;
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bs_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('bs_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bs_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bs_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bs_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bs_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bs_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bs_id').value);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	if(this.alloweditor){
    		this.GridUtil.onGridItemClick(selModel, record);
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});