Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.SecondCategory', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.SecondCategory','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.trigger.CateTreeDbfindTrigger',
      		'core.button.DeleteDetail', 'core.trigger.MultiDbfindTrigger','core.trigger.DbfindTrigger',
      		'core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var code = Ext.getCmp('ca_code'),
    					level = Ext.getCmp('ca_level'),
    					err = null;
    				if(level == 1) {
    					if(code.length != 3 && code.length != 4) {
    						err = "一级科目的编号一般为3~4位,当前科目号:" + code + ".是否仍然保存?";
    					}
    				} else {
    					if(code.length <= 4) {
    						err = "下级科目的编号一般大于4位,当前科目号:" + code + ".是否仍然保存?";
    					}
    				}
    				if(err != null) {
    					warnMsg(err, function(btn){
    						if(btn == 'yes') {
    		    				this.FormUtil.beforeSave(this);
    						}
    					});
    				} else {
        				this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var code = Ext.getCmp('ca_code'),
						level = Ext.getCmp('ca_level'),
						err = null;
					if(level == 1) {
						if(code.length != 3 && code.length != 4) {
							err = "一级科目的编号一般为3~4位,当前科目号:" + code + ".是否仍然保存?";
						}
					} else {
						if(code.length <= 4) {
							err = "下级科目的编号一般大于4位,当前科目号:" + code + ".是否仍然保存?";
						}
					}
					if(err != null) {
						warnMsg(err, function(btn){
							if(btn == 'yes') {
								this.FormUtil.onUpdate(this);
							}
						});
					} else {
						this.FormUtil.onUpdate(this);
					}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSecondCategory', '新增科目维护', 'jsps/fa/ars/SecondCategory.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'field[name=ca_assname]': {
    			change: function(f) {
    				if(Ext.isEmpty(f.value)) {
    					var t = f.ownerCt.down('#ca_asstype');
    					t && t.setValue(null);
    				}    			
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'combobox[name=ca_type]': {
    			change: function(f){
    				var n = Ext.getCmp('ca_typename');
    				if(n){
    					switch(f.value){
		    				case 0:
		    					n.setValue('借');
		    					break;
		    				case 1:
		    					n.setValue('贷');
		    					break;
		    				case 2:
		    					n.setValue('借或贷');
		    					break;
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=ca_pcode]': {
    			change: function(f) {
    				if(Ext.isEmpty(f.value)) {
    					var n = Ext.getCmp('ca_level');
        				if(n){
        					n.setValue(1);
        				}
    				}
    			},
    			aftertrigger: function(f, r){
    				var n = Ext.getCmp('ca_level');
    				if(n){
    					n.setValue(n.value + 1);
    				}
    				var sCode = f.value, cf = Ext.getCmp('ca_code');
    				if(!Ext.isEmpty(cf.getValue())) {
    					return;
    				}
					Ext.Ajax.request({
				   		url : basePath + 'common/getFieldData.action',
				   		async: false,
				   		params: {
				   			caller: 'Category',
				   			field: 'count(*)',
				   			condition: 'ca_subof=(SELECT ca_id FROM Category WHERE ca_code=\'' + sCode + '\')'
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);return null;
				   			}
				   			var str = '001';
			    			if(localJson.success && localJson.data != null){
			    				var count = localJson.data + 1;
			    				str = '' + count;
			    				if(count < 10) {
			    					str = '00' + count;
			    				} else if (count < 100) {
			    					str = '0' + count;
			    				}
				   			}
			    			cf.setValue(sCode + str);
			    		}
					});
    			}
    		},
    		'field[name=ca_iscash]': {
    			change: function(f) {
    				var s = Ext.getCmp('ca_isbank');
    				if(f.value == '-1') {
    					Ext.getCmp('ca_iscashbank').setValue('-1');
    					if(s.value == '-1')
    						Ext.getCmp('ca_isbank').setValue('0');
    				}
    			}
    		},
    		'field[name=ca_isbank]': {
    			change: function(f) {
    				var s = Ext.getCmp('ca_iscash');
    				if(f.value == '-1') {
    					Ext.getCmp('ca_iscashbank').setValue('-1');
    					if(s.value == '-1')	
    						Ext.getCmp('ca_iscash').setValue('0');
    				}
    			}
    		},
    		'field[name=ca_iscashbank]': {
    			change: function(f) {
    				if(f.value == '0') {
    					Ext.getCmp('ca_iscash').setValue('0');
    					Ext.getCmp('ca_isbank').setValue('0');
    				}
    			}
    		},
    		'cateTreeDbfindTrigger[name=ca_pcode]': {
    			change: function(f) {
    				if(Ext.isEmpty(f.value)) {
    					var n = Ext.getCmp('ca_level');
        				if(n){
        					n.setValue(1);
        				}
    				}
    			},
    			aftertrigger: function(tri, data){
//    				var newlevel = Number(Ext.getCmp('ca_level').getValue()) + 1;
//    				Ext.getCmp('ca_level').setValue(newlevel);
    				var depth = data[0].data.depth;
    				var n = Ext.getCmp('ca_level');
    				if(depth != null){
        				if(n){
        					n.setValue(Number(depth) + 1);
        				}
    				} else {
    					n.setValue(1);
    					
    				} 
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});