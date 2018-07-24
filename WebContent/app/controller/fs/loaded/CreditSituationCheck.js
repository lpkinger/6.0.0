Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.CreditSituationCheck', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : ['core.form.Panel', 'fs.loaded.CreditSituationCheck','core.form.MultiField','core.button.Save','core.button.Modify',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.SeparNumber'],
	init : function() {
		var me = this;
		this.control({
			'field[name=li_crblxdjilish]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_crkind').hide();
    					Ext.getCmp('li_crbalance').hide();
    					Ext.getCmp('li_croverdueamount').hide();
    					Ext.getCmp('li_croverduedays').hide();
    					Ext.getCmp('li_crowecorpus').hide();
    					Ext.getCmp('li_croweinterest').hide();
    					Ext.getCmp('li_crmeasures').hide();
    				} else {
    					Ext.getCmp('li_crkind').show();
    					Ext.getCmp('li_crbalance').show();
    					Ext.getCmp('li_croverdueamount').show();
    					Ext.getCmp('li_croverduedays').show();
    					Ext.getCmp('li_crowecorpus').show();
    					Ext.getCmp('li_croweinterest').show();
    					Ext.getCmp('li_crmeasures').show();
    				}
				},
				change : function(f,newValue) {
					if(newValue){
    					Ext.getCmp('li_crkind').show();
    					Ext.getCmp('li_crbalance').show();
    					Ext.getCmp('li_croverdueamount').show();
    					Ext.getCmp('li_croverduedays').show();
    					Ext.getCmp('li_crowecorpus').show();
    					Ext.getCmp('li_croweinterest').show();
    					Ext.getCmp('li_crmeasures').show();
    				} else {
    					Ext.getCmp('li_crkind').hide();
    					Ext.getCmp('li_crbalance').hide();
    					Ext.getCmp('li_croverdueamount').hide();
    					Ext.getCmp('li_croverduedays').hide();
    					Ext.getCmp('li_crowecorpus').hide();
    					Ext.getCmp('li_croweinterest').hide();
    					Ext.getCmp('li_crmeasures').hide();
    				}
				}
    		},
    		'field[name=li_crxzxdxxish]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_craddcramount').hide();
    				} else {
    					Ext.getCmp('li_craddcramount').show();
    				}
				},
				change : function(f,newValue) {
					if(newValue){
						Ext.getCmp('li_craddcramount').show();
    				} else {
    					Ext.getCmp('li_craddcramount').hide();
    				}
				}
    		},
    		'field[name=li_crxzdbxx]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value) || f.value == '0'){
    					Ext.getCmp('li_craddguamount').hide();
    				} else {
    					Ext.getCmp('li_craddguamount').show();
    				}
				},
				change : function(f,newValue) {
					if(newValue){
						Ext.getCmp('li_craddguamount').show();
    				} else {
    					Ext.getCmp('li_craddguamount').hide();
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
	}
});