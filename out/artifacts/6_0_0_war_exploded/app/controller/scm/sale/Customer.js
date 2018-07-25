Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.Customer', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.Customer','core.form.Panel','core.form.MultiField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Upload','core.button.Update',
    			'core.button.Delete','core.button.Sync',
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.SendEdi'
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
      								
					this.FormUtil.onUpdate(this);
					
      			}
      		},
      		'erpUpdateButton': {
      			click: function(btn){
      				var form = me.getForm(btn);
      				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
      				this.FormUtil.onUpdate(this);
      			}
      		},
      		'erpAddButton': {
      			click: function(){
      				me.FormUtil.onAdd('addCustomer', '新增客户主档资料', 'jsps/scm/sale/customer.jsp');
      			}
      		},
      		'erpCloseButton': {
      			click: function(btn){
      				me.FormUtil.beforeClose(me);
      			}
      		}
      	});
      },
      getForm: function(btn){
      	return btn.ownerCt.ownerCt;
      }
});