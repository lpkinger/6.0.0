Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Reandpunish', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Reandpunish','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Submit','core.button.ResAudit','core.button.ResSubmit','core.button.Audit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
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
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('rp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				if(caller == 'Reandpunish'){
    					me.FormUtil.onAdd('addReandpunish', '新增奖惩', 'jsps/hr/emplmana/reandpunish/reandpunish.jsp');
    				}else{
    					me.FormUtil.onAdd('addReandpunish1', '新增奖惩', 'jsps/hr/emplmana/reandpunish/reandpunish1.jsp');
    				}
    				
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('rp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('rp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('rp_id').value);
				}
			},
			'field[name=rp_sourcecode]':{
	  			   afterrender:function(f){
	  				   f.setFieldStyle({
	  					   'color': 'red'
	  				   });
	  				   f.focusCls = 'mail-attach';
	  				   var c = Ext.Function.bind(me.openSource, me);
	  				   Ext.EventManager.on(f.inputEl, {
	  					   mousedown : c,
	  					   scope: f,
	  					   buffer : 100
	  				   });
	  			   }
	 			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('rp_id').value);
				}
			}
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    openSource : function(e, el, obj) {
    	var f = obj.scope;
    	if(f.value) {
    		this.FormUtil.onAdd(null, f.ownerCt.down('#rp_sourcecode').value, 
    				f.ownerCt.down('#rp_sourcelink').value + '&_noc=1');
    	}
    }
});