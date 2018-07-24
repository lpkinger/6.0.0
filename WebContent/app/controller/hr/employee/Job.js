Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.Job', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.Job','core.form.Panel','core.button.Sync','core.button.Submit',
    		'core.button.Audit','core.button.ResAudit','core.button.ResSubmit',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.CopyPower',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Banned','core.button.ResBanned',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSyncButton': {
    			afterrender: function(btn){
    				btn.autoClearCache = true;
    			}
    		},
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
    			afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('jo_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addJob', '新增岗位资料', 'jsps/hr/employee/job.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('jo_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('jo_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('jo_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('jo_id').value);
				}
			},
    		'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('jo_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('jo_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('jo_id').value);
    			}
    		},
    		'erpCopyPowerButton': {//复制权限
    			click: function() {
    				var id = Ext.getCmp('jo_id').value;
    				if(id == null || id == 0) {
    					return;
    				}
    				var win = Ext.getCmp('power-copy-win');
    				if(!win ) {
    					win = Ext.create('Ext.Window', {
        					id: 'power-copy-win',
        					width: 800,
        					height: 400,
        					title: '选择目标岗位',
        					layout: {
        						type: 'vbox'
        					},
        					defaults: {
        						margin: '3 3 3 10'
        					},
        					items: [{
        						xtype: 'dbfindtrigger',
        						fieldLabel: '岗位编号',
        						id: 'jo_code_source',
        						name: 'jo_code_source'
        					}, {
        						xtype: 'displayfield',
        						fieldLabel: '岗位名称',
        						id: 'jo_name_source',
        						name: 'jo_name_source'
        					}, {
        						xtype: 'displayfield',
        						fieldLabel: '岗位描述',
        						id: 'jo_description_source',
        						name: 'jo_description_source'
        					}, {
        						xtype: 'hidden',
        						fieldLabel: 'ID',
        						id: 'jo_id_source',
        						name: 'jo_id_source'
        					}],
        					buttonAlign: 'center',
        					buttons: [{
        						text: $I18N.common.button.erpConfirmButton,
        						cls: 'x-btn-blue',
        						handler: function(btn) {
        							var w = btn.ownerCt.ownerCt;
        							if(w.down('#jo_id_source').value > 0) {
        								warnMsg('确定复制 ' + w.down('#jo_name_source').value + ' 的权限吗?', function(btn){
            								if(btn == 'yes'){
            									me.copyPower(Ext.getCmp('jo_id').value, w.down('#jo_id_source').value);
            								}
            							});
        							}
        						}
        					}, {
        						text: $I18N.common.button.erpCloseButton,
        						cls: 'x-btn-blue',
        						handler: function(btn) {
        							btn.ownerCt.ownerCt.close();
        						}
        					}]
        				});
    				}
    				win.show();
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	copyPower: function(id, sourceid) {
		Ext.Ajax.request({
			
		});
	}
});