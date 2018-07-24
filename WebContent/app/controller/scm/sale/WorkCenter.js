Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.WorkCenter', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'scm.sale.WorkCenter','core.form.Panel','core.grid.Panel2',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Scan',
    		'core.button.Upload','core.button.Update','core.button.Delete','core.button.Submit',
    		'core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.Banned',
    		'core.button.ResBanned','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {
    			itemclick: this.GridUtil.onGridItemClick
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
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWorkCenter', '新增工作中心', 'jsps/scm/sale/workCenter.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var check = me.checkGridData();
					if(check){
						me.FormUtil.onSubmit(Ext.getCmp('wc_id').value);
					}
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('wc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('wc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('wc_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wc_statuscode');
					if(status && (status.value == 'BANNED' || status.value == 'DISABLE') && status.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('wc_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('wc_statuscode');
					if(status && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('wc_id').value);
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	checkGridData : function(){
		var me = this;
		var data = me.GridUtil.getAllGridStoreData();
		var error='';
		for(var i=0;i<data.length-1;i++){
			if(data[i].wm_id!=0){
				for(var j=i+1;j<data.length;j++){
					if(data[i].wm_emcode==data[j].wm_emcode){
						error +=data[i].wm_detno+'和'+data[j].wm_detno+'、';
					}
				}
			}
		}
		if(error.length>0){
			error = error.substring(0,error.length-1);
			showError('行号：'+error+'存在重复的人员编号，不允许提交!');
			return false;
		}
		return true;
	}
});