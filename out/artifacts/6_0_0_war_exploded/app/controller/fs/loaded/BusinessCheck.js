Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.BusinessCheck', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : ['core.form.Panel', 'fs.loaded.BusinessCheck','core.form.MultiField','core.button.Save','core.button.Modify',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.SeparNumber'],
	init : function() {
		var me = this;
		this.control({
			'field[name=li_bcrealtedesc]': {
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
    		'field[name=li_bcsdisxj]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_bclastsdf').hide();
    					Ext.getCmp('li_bcthissdf').hide();
    				} else {
    					Ext.getCmp('li_bclastsdf').show();
    					Ext.getCmp('li_bcthissdf').show();
    				}
				},
				change : function(f) {
					if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_bclastsdf').hide();
    					Ext.getCmp('li_bcthissdf').hide();
    				} else {
    					Ext.getCmp('li_bclastsdf').show();
    					Ext.getCmp('li_bcthissdf').show();
    				}
				}
    		},
    		'field[name=li_bctaxisxj]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_bclasttax').hide();
    					Ext.getCmp('li_bcthistax').hide();
    				} else {
    					Ext.getCmp('li_bclasttax').show();
    					Ext.getCmp('li_bcthistax').show();
    				}
				},
				change : function(f) {
					if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_bclasttax').hide();
    					Ext.getCmp('li_bcthistax').hide();
    				} else {
    					Ext.getCmp('li_bclasttax').show();
    					Ext.getCmp('li_bcthistax').show();
    				}
				}
    		},
    		'checkbox': {
    			change: function(f, newValue, oldValue){
    				var desc = Ext.getCmp('li_bcrealtedesc');
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