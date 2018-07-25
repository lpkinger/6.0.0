Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.CustomerQuotaZL', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.CustomerQuotaZL', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export', 'core.button.Sync','core.button.FormsDoc', 'core.form.SeparNumber',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.button.InfoPerfect','core.button.PrintByCondition'],
	init : function() {
		var me = this;
		this.control({
//			'erpFormPanel' : {
//    			afterload : function(form) {
//    				this.hidecolumns(form.down('#cq_quotatype'));
//				}
//    		},
//    		'combo[name=cq_quotatype]': {
//    			delay: 200,
//    			change: function(m){
//					this.hidecolumns(m);
//				}
//    		},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CustomerQuotaZL', '融资租赁额度申请', 'jsps/fs/cust/customerQuotaZL.jsp');
    			}
        	},
        	'erpInfoPerfectButton': {
				click : function(btn) {
					var cq_id = Ext.getCmp('cq_id'),caid = null;
					if(cq_id&&cq_id.value){
						cqid = cq_id.value;
					}
					var readOnly = 1;
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value == 'ENTERING') {
						readOnly = 0;
					}
					var cq_custname = Ext.getCmp('cq_custname'),custname = '';
					if(cq_custname&&cq_custname.value){
						custname = cq_custname.value;
					}
		   			me.FormUtil.onAdd('InfoPerfect'+cqid, custname+'项目风控报告', 'jsps/fs/cust/infoPerfectZL.jsp?caid='+cqid+'&readOnly='+readOnly+'&custname='+custname);

				}
        	},
			'erpSaveButton': {
    			click: function(btn){
    				var cq_feetype = Ext.getCmp('cq_maxperiod').value;
    				if(cq_feetype > 30){
    					showError('最长宽限期天数不能超过30天！');
						return;
    				}
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					this.FormUtil.beforeSave(this);				
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cq_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					var cq_feetype = Ext.getCmp('cq_maxperiod').value;
    				if(cq_feetype > 30){
    					showError('最长宽限期天数不能超过30天！');
						return;
    				}
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					var cq_feetype = Ext.getCmp('cq_maxperiod').value;
    				if(cq_feetype > 30){
    					showError('最长宽限期天数不能超过30天！');
						return;
    				}
					me.FormUtil.onSubmit(Ext.getCmp('cq_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cq_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cq_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cq_id').value);
				}
			},
			'erpSyncButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('cq_statuscode');
					if(status && status.value != 'AUDITED' && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				}
    		}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	hidecolumns:function(m){
		if(!Ext.isEmpty(m.getValue())) {
			var form = m.ownerCt;
			Ext.getCmp('cq_factorcode') && Ext.getCmp('cq_factorcode').hide();
			Ext.getCmp('cq_factorname') && Ext.getCmp('cq_factorname').hide();
			if(m.value == '反向保理业务'){
				form.down('#cq_hxcustcode').show();
				form.down('#cq_hxcustname').show();
				form.down('#cq_cacode').show();
				form.down('#cq_hxcredit').show();
			} else {
				form.down('#cq_hxcustcode').hide();
				form.down('#cq_hxcustname').hide();
				form.down('#cq_cacode').hide();
				form.down('#cq_hxcredit').hide();
			}
			if (m.value == '票据再保理'){
				Ext.getCmp('cq_factorcode') && Ext.getCmp('cq_factorcode').show();
				Ext.getCmp('cq_factorname') && Ext.getCmp('cq_factorname').show();
			}
		}
	}
});