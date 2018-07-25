Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.FinancialCheck', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.FinancialCheck','core.form.MultiField', 'core.grid.Panel2','core.button.Modify',
	         'core.button.Save','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.SeparNumber'],
	init : function() {
		var me = this;
		this.control({
			'field[name=li_fcrealtedesc]': {
    			beforerender : function(f) {
    				f.emptyText = '对上述变动情况进行说明';
				},
				afterrender : function(f) {
    				if(!me.hasCheck()){
    					f.hide();
    					f.height=null;
    				}
				}
    		},
    		'field[name=li_fcchisu]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_fcchcond').hide();
    					Ext.getCmp('li_fcchamount1').hide();
    					Ext.getCmp('li_fcchamount2').hide();
    				} else {
    					Ext.getCmp('li_fcchcond').show();
    					Ext.getCmp('li_fcchamount1').show();
    					Ext.getCmp('li_fcchamount2').show();
    				}
				},
				change : function(f) {
					if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_fcchcond').hide();
    					Ext.getCmp('li_fcchamount1').hide();
    					Ext.getCmp('li_fcchamount2').hide();
    				} else {
    					Ext.getCmp('li_fcchcond').show();
    					Ext.getCmp('li_fcchamount1').show();
    					Ext.getCmp('li_fcchamount2').show();
    				}
				}
    		},
    		'field[name=li_fcysisu]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_fcyscond').hide();
    					Ext.getCmp('li_fcysamount1').hide();
    					Ext.getCmp('li_fcysamount2').hide();
    					Ext.getCmp('li_fcyscuname').hide();
    				} else {
    					Ext.getCmp('li_fcyscond').show();
    					Ext.getCmp('li_fcysamount1').show();
    					Ext.getCmp('li_fcysamount2').show();
    					if(Ext.getCmp('li_fcyscond').value == '关注；某一客户应收账款增加'){
    						Ext.getCmp('li_fcyscuname').show();
    					} else {
    						Ext.getCmp('li_fcyscuname').hide();
    					}
    				}
				},
				change : function(f) {
					if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_fcyscond').hide();
    					Ext.getCmp('li_fcysamount1').hide();
    					Ext.getCmp('li_fcysamount2').hide();
    					Ext.getCmp('li_fcyscuname').hide();
    				} else {
    					Ext.getCmp('li_fcyscond').show();
    					Ext.getCmp('li_fcysamount1').show();
    					Ext.getCmp('li_fcysamount2').show();
    					if(Ext.getCmp('li_fcyscond').value == '关注；某一客户应收账款增加'){
    						Ext.getCmp('li_fcyscuname').show();
    					} else {
    						Ext.getCmp('li_fcyscuname').hide();
    					}
    				}
				}
    		},
    		'field[name=li_fcyscond]': {
    			beforerender : function(f) {
    				if(f.value == '关注；某一客户应收账款增加'){
    					Ext.getCmp('li_fcyscuname').show();
    				} else {
    					Ext.getCmp('li_fcyscuname').hide();
    				}
    			},
    			change : function(f) {
    				if(f.value == '关注；某一客户应收账款增加'){
    					Ext.getCmp('li_fcyscuname').show();
    				} else {
    					Ext.getCmp('li_fcyscuname').hide();
    				}
				}
    		},
    		'checkbox': {
    			change: function(f, newValue, oldValue){
    				var desc = Ext.getCmp('li_fcrealtedesc');
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