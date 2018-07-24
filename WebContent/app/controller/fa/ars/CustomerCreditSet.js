Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.CustomerCreditSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.ars.CustomerCreditSet','core.form.Panel',
      		'core.button.Save','core.button.Close',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpSaveButton': {
				click: function(btn){
//					var form = me.getForm(btn);
//					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
//    					me.BaseUtil.getRandomNumber();//自动添加编号
//    				}
//					var id = Ext.getCmp('cu_id').value;
//					if(id == null || id == '' || id == '0' || id == 0){
//						this.FormUtil.beforeSave(this);
//					} else {
						this.FormUtil.onUpdate(this);
//					}
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