Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.CustomerChange','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
    			'core.button.ResSubmit','core.button.Scan',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
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
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustomerChange', '新增客户资料变更', 'jsps/scm/sale/customerChange.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('cc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('cc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('cc_id').value);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('cc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('cc_id').value);
				}
			},
			'textfield[name=cc_cucode]': {
    			aftertrigger: function(field){
    				if(field != null && field != ''){
        				Ext.getCmp('cc_newsellerid').setValue(Ext.getCmp('cc_sellerid').value);
        				Ext.getCmp('cc_newsellercode').setValue(Ext.getCmp('cc_sellercode').value);
        				Ext.getCmp('cc_newsellername').setValue(Ext.getCmp('cc_sellername').value);
        				Ext.getCmp('cc_newcukind').setValue(Ext.getCmp('cc_cukind').value);
        				Ext.getCmp('cc_newagenttype').setValue(Ext.getCmp('cc_agenttype').value);
        				Ext.getCmp('cc_newcurrency').setValue(Ext.getCmp('cc_currency').value);
        				Ext.getCmp('cc_newrate').setValue(Ext.getCmp('cc_rate').value);
        				Ext.getCmp('cc_newtaxrate').setValue(Ext.getCmp('cc_taxrate').value);
        				Ext.getCmp('cc_newinvoicetype').setValue(Ext.getCmp('cc_invoicetype').value);
        				Ext.getCmp('cc_newpaymentid').setValue(Ext.getCmp('cc_paymentid').value);
        				Ext.getCmp('cc_newpaymentscode').setValue(Ext.getCmp('cc_paymentscode').value);
        				Ext.getCmp('cc_newpayments').setValue(Ext.getCmp('cc_payments').value);
        				Ext.getCmp('cc_newshipment').setValue(Ext.getCmp('cc_shipment').value);
        				Ext.getCmp('cc_newcontact').setValue(Ext.getCmp('cc_contact').value);
        				Ext.getCmp('cc_newdegree').setValue(Ext.getCmp('cc_degree').value);
        				Ext.getCmp('cc_newmobile').setValue(Ext.getCmp('cc_mobile').value);
        				Ext.getCmp('cc_newtel').setValue(Ext.getCmp('cc_tel').value);
        				Ext.getCmp('cc_newfax').setValue(Ext.getCmp('cc_fax').value);
        				Ext.getCmp('cc_newemail').setValue(Ext.getCmp('cc_email').value);
        				if(typeof (f = Ext.getCmp('cc_newprovince')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_province').value);
        				if(typeof (f = Ext.getCmp('cc_newarcustcode')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_arcustcode').value);
        				if(typeof (f = Ext.getCmp('cc_newarcustname')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_arcustname').value);
        				if(typeof (f = Ext.getCmp('cc_newmonthsend')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_monthsend').value);
        				if(typeof (f = Ext.getCmp('cc_newwebserver')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_webserver').value);
        				if(typeof (f = Ext.getCmp('cc_newdistrict')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_district').value);
        				if(typeof (f = Ext.getCmp('cc_newcuname')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_cuname').value);
        				if(typeof (f = Ext.getCmp('cc_newshortcuname')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_shortcuname').value);
        				if(typeof (f = Ext.getCmp('cc_newengname')) !== 'undefined')
    						f.setValue(Ext.getCmp('cc_engname').value);
    				}
    			}
    		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});