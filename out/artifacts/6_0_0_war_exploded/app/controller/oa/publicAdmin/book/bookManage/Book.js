Ext.QuickTips.init();
Ext.define('erp.controller.oa.publicAdmin.book.bookManage.Book', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.publicAdmin.book.bookManage.Book','core.form.Panel','core.button.Scan',
		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.ResAudit',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
		'core.form.YnField','core.trigger.AutoCodeTrigger','core.toolbar.Toolbar'
	],
    init:function(){
    	var me = this;
        	this.control({
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
    				this.FormUtil.onDelete(Ext.getCmp('bo_id').value);
    			}
    		},
			'dbfindtrigger[name=bo_kindcode]' : {
				afterrender : function(t) {
					t.dbKey = 'bo_storagecode';//form里面上级数据
					t.mappingKey = 'bs_code';//dbfind: dbfindtrigger[name=bo_kindcode]对应的 配置bsd_code
					t.dbMessage = '请先选择仓库!';
				}
			},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addBook', '新增图书', 'jsps/oa/publicAdmin/book/bookManage/book.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);;
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bo_id').value);
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