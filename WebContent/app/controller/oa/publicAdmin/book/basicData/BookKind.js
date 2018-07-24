Ext.QuickTips.init();
Ext.define('erp.controller.oa.publicAdmin.book.basicData.BookKind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.publicAdmin.book.basicData.BookKind','core.form.Panel','core.button.Scan',
		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
		'core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.toolbar.Toolbar'
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
    				this.FormUtil.onDelete(Ext.getCmp('bk_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addBookKind', '新增图书类型', 'jsps/oa/publicAdmin/book/basicData/bookKind.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);;
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