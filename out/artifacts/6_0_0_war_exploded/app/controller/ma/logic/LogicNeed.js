Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.LogicNeed', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.logic.LogicNeed','core.form.Panel',
   		'core.button.Add','core.button.Save','core.button.Close',
   		'core.button.Update', 'core.button.Design', 'core.form.YnField',
   		'core.trigger.DbfindTrigger'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'textarea[name=ln_analyse]': {
    			afterrender: function(f){
    				f.setHeight(300);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt;
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var f = Ext.getCmp('ln_deal');
    				if (f.value != 0) {//已处理
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDesignButton': {
    			afterrender: function(btn){
    				var f = Ext.getCmp('ln_deal');
    				if (f.value != 0) {//已处理
    					btn.setWidth(90);
    					btn.setText('查看方案');
    					var items = f.ownerCt.items.items;
    					Ext.each(items, function(item){
    						item.setReadOnly(true);
    						item.setFieldStyle('background:#f1f1f1;');
    					});
    				}
    			},
    			click: function(){
    				var f = Ext.getCmp('ln_deal');
    				if (f.value != 0) {//已处理
    					var id = Ext.getCmp('ln_ldid').value;
    					me.FormUtil.onAdd('logicDesc' + id, '算法设计', 'jsps/ma/logic/logicDesc.jsp?formCondition=ld_idIS' + id + 
    							"&gridCondition=ldf_ldidIS" + id);
    				} else {
    					var c = Ext.getCmp('ln_code').value;
        				me.FormUtil.onAdd('addLogicDesc', '算法设计', 'jsps/ma/logic/logicDesc.jsp?ld_lncode=' + c);
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addLogicNeed', '添加新需求', 'jsps/ma/logic/logicNeed.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});