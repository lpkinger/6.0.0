Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.BaseSituation', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : ['core.form.Panel', 'fs.loaded.BaseSituation','core.form.MultiField','core.button.Save',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.button.Modify'],
	init : function() {
		var me = this;
		this.control({
			'field[name=li_barealtedesc]': {
    			beforerender : function(f) {
    				f.emptyText = '客户经理对上述有变动情况进行调查后说明情况';
				},
				afterrender : function(f) {
    				if(!me.hasCheck()){
    					f.hide();
    					f.height=null;
    				}
				}
    		},
    		'field[name=li_baconclusion]': {
				afterrender : function(f) {
    				Ext.create('Ext.tip.ToolTip', {
				        target: f.el,
				        html:'内容是企业或法人、实际控制人涉及刑事案件或重大经济纠纷（欠款金额500万以上）应列为异常情况'
				    });
				}
    		},
    		'checkbox': {
    			change: function(f, newValue, oldValue){
    				var desc = Ext.getCmp('li_barealtedesc');
    				if(newValue&&desc&&desc.isHidden()){
    					desc.show();
    				}else if(desc&&!desc.isHidden()){
	    				if(!me.hasCheck()){
	    					desc.hide();
	    				}
    				}
    			}
    		},
			'erpSaveButton': {
				afterrender:function(btn){
					var status = Ext.getCmp('li_statuscode');
					if(status&&status.value!='ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){		
    				me.FormUtil.onUpdate(this);
    			}
        	}
		})
	},
	hasCheck: function(){
		var checks = Ext.ComponentQuery.query('form checkbox');
		var hasCheck = false;
		Ext.Array.each(checks,function(check){
			if(check.checked){
				hasCheck = true;
				return;
			}
		});
		return hasCheck;
	}
});