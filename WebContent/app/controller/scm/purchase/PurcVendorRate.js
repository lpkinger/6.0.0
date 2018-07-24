Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.PurcVendorRate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.purchase.PurcVendorRate','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Update','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.Scan',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	me.insertnum = 0;
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
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('pvr_id').value));
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addPurcVendorRate', '新增供应商比例更新', 'jsps/scm/purchase/purcVendorRate.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('pvr_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('pvr_id').value);
    			}
    		},
    		/*'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('pvr_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('pvr_id').value);
    			}
    		},*/
    		'erpSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('pvr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('pvr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('pvr_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('pvr_id').value);
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