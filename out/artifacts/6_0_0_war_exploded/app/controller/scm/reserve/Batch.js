Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.Batch', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.Batch','core.form.Panel',
    		'core.button.Audit','core.button.Save','core.button.Close',
    			'core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
    		'core.form.FileField'
    	],
    init:function(){
    	this.control({ 
    		'erpFormPanel' : {
    			afterload : function(form) {
    				form.getForm().getFields().each(function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
						}
					});
    				var t = form.down('#ba_kind');
    				this.hidecolumns(t);
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				var kind = Ext.getCmp('ba_kind').value;
    				if(kind == ''){
    					
    				}
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var kind = Ext.getCmp('ba_kind').value;
    				if(kind == '10'){
    					Ext.getCmp('ba_barcode').setValue(null);
    				} else if (kind == '20') {
    					Ext.getCmp('ba_salecode').setValue(null);
    				} else {
    					Ext.getCmp('ba_barcode').setValue(null);
    					Ext.getCmp('ba_salecode').setValue(null);
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'combo[name=ba_kind]': {
    			delay: 200,
    			change: function(m){
					this.hidecolumns(m);
					var f = m.ownerCt, s = f.down('field[name=ba_salecode]'),
					c = f.down('field[name=ba_barcode]');
					if (s) {
						if(m.value == '10') {
							s.allowBlank = false;
						} else {
							s.allowBlank = true;
						}
					}
					if (c) {
						if(m.value == '20') {
							c.allowBlank = false;
						} else {
							c.allowBlank = true;
						}
					}
				}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	hidecolumns:function(m){
		if(!Ext.isEmpty(m.getValue())) {
			var form = m.ownerCt;
			if(m.value == 10){
				form.down('#ba_salecode').show();
				form.down('#ba_barcode').hide();
			} else if(m.value == 20){
				form.down('#ba_salecode').hide();
				form.down('#ba_barcode').show();
			}else{ 
				form.down('#ba_salecode').hide();
				form.down('#ba_barcode').hide();
			}
		}
	}
});