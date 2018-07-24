Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.TenderQuestion', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    	'core.form.Panel','scm.purchase.TenderQuestion','core.trigger.TextAreaTrigger','core.button.Close','core.form.FileField2'
    ],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpFormPanel': {
    			afterrender: function(panel){
    				Ext.Ajax.request({
			        	url : basePath + 'scm/purchase/getTenderQuestion.action',
			        	params: {
			        		id:id
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			return;
			        		}else{
								var form = panel.ownerCt.down('form');
				        		Ext.Array.each(form.items.items,function(field){
			        				if(typeof(field.setValue)=='function'&&res.tenderQuestion[field.name]){
				        				if(field.xtype=='datetimefield'){
					        				field.setValue(Ext.Date.format(new Date(res.tenderQuestion[field.name]),'Y-m-d H:i:s'));
					        			}else if(field.xtype=='datefield'){
					        				field.setValue(Ext.Date.format(new Date(res.tenderQuestion[field.name]),'Y-m-d'));
					        			}else{
					        				field.setValue(res.tenderQuestion[field.name]);
					        			}
				        			}
				        		});
			        		}
			        	}
    				})
    			}
    		},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    }
});