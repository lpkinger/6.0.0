Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.LogicChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.logic.LogicChange','core.form.Panel',
   		'core.button.Add','core.button.Save','core.button.Close',
   		'core.button.Update', 'core.button.Design','core.button.Submit',
   		'core.trigger.DbfindTrigger'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'textarea[name=lc_oldsource]': {
    			afterrender: function(f){
    				f.setHeight(300);
    			}
    		},
    		'textarea[name=lc_newsource]': {
    			afterrender: function(f){
    				f.setHeight(300);
    				if(Ext.isEmpty(f.value) && Ext.getCmp('lc_oldsource') && !Ext.isEmpty(Ext.getCmp('lc_oldsource').value)) {
    					f.setValue(Ext.getCmp('lc_oldsource').value);
    				}
    			}
    		},
    		'field[name=lc_ldcode]': {
    			afterrender: function(f){
    				if (!Ext.isEmpty(lc_ldcode) && Ext.isEmpty(f.value) ) {
    					f.setValue(lc_ldcode);
    				}
    			}
    		},
    		'field[name=lc_oldversion]': {
    			afterrender: function(f){
    				
    			},
    			change: function(f){
    				var form = f.ownerCt,
    					lc_oldsource = form.down('#lc_oldsource');
    				if(!lc_oldsource) {
    					form.insert(form.items.items.length - 3, {
    						xtype: 'textarea',
    						columnWidth: 1,
    						id: 'lc_oldsource',
    						name: 'lc_oldsource',
    						fieldLabel: '原代码',
    						cls: 'form-field-allowBlank',
    						fieldStyle: 'background:#f1f1f1;',
    						readOnly: true
    					});
    					Ext.getCmp('lc_newsource').show();
    				}
    				me.getSource(form.down('#lc_ldcode').value, f.value, 'lc_oldsource');
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var f = Ext.getCmp(btn.ownerCt.ownerCt.statuscodeField);
    				if(f && f.value == 'VALID') {
    					btn.hide();
    				}
    				f = Ext.getCmp('lc_oldversion');
    				if (Ext.isEmpty(f.value) ) {
    					if(!Ext.isEmpty(lc_oldversion)) {
    						f.setValue(lc_oldversion);
    					}
    				} else {
    					var form = f.ownerCt,
    						lc_oldsource = form.down('#lc_oldsource');
    					if(!lc_oldsource) {
    						form.insert(form.items.items.length - 3, {
	    						xtype: 'textarea',
	    						columnWidth: 1,
	    						id: 'lc_oldsource',
	    						name: 'lc_oldsource',
	    						fieldLabel: '原代码',
	    						cls: 'form-field-allowBlank',
	    						fieldStyle: 'background:#f1f1f1;',
	    						readOnly: true
	    					});
    						Ext.getCmp('lc_newsource').show();
    					}
    					me.getSource(form.down('#lc_ldcode').value, f.value, 'lc_oldsource');
					}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var f = Ext.getCmp(btn.ownerCt.ownerCt.statuscodeField);
    				if(f && f.value == 'VALID') {
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				Ext.Ajax.request({
    					url: basePath + 'ma/logic/submitLogicChange.action',
    					method: 'post',
    					params: {
    						id: Ext.getCmp('lc_id').value
    					},
    					callback: function(options, success, response){
    						var res = Ext.decode(response.responseText);
    						if(res.id){
    							id = res.id;
    							me.FormUtil.onAdd('LogicDesc', '算法设计', 'jsps/ma/logic/logicDesc.jsp?formCondition=ld_idIS' + id + 
    									'&gridCondition=ldf_ldidIS' + id);
    							window.location.reload();
    						}
    					}
    				});
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addLogicChange', '需求变更', 'jsps/ma/logic/logicChange.jsp');
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
	},
	getSource: function(code, version, field){
		this.FormUtil.getFieldValue('LogicDesc', 'ld_source', "ld_code='" + code + "' AND ld_version='" + version + "'",
				field);
	}
});