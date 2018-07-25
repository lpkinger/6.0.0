Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerPaymentsApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil : Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.CustomerPaymentsApply','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2', 'core.toolbar.Toolbar', 'core.grid.YnColumn',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.button.PrintByCondition',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'),items = grid.store.data.items;
					var bool = true;
					var i=0;
					Ext.each(items, function(item){
						if(item.data['cad_operation']=='修改' && (item.data['cad_oldpaymentcode']==null || item.data['cad_oldpaymentcode']=='')){
							bool = false;
    						showError('选择修改操作时，明细表第' + item.data['cad_detno'] + '行的原收款方式编号不能为空');return;
						}
						if(item.data['cad_operation']=='修改' && (item.data['cad_paymentcode']==null || item.data['cad_paymentcode']=='')){
							bool = false;
    						showError('选择修改操作时，明细表第' + item.data['cad_detno'] + '行的新收款方式编号不能为空');return;
						}
						if(item.data['cad_operation']=='删除' && (item.data['cad_oldpaymentcode']==null || item.data['cad_oldpaymentcode']=='')){
							bool = false;
    						showError('选择删除操作时，明细表第' + item.data['cad_detno'] + '行的原收款方式编号不能为空');return;
						}
						if(item.data['cad_operation']=='删除' && item.data['cad_isdefault'] == '是'){
							bool = false;
    						showError('选择删除操作时，明细表第' + item.data['cad_detno'] + '行的是否默认不能选是');return;
						}
						if(item.data['cad_operation']=='增加' && (item.data['cad_paymentcode']==null || item.data['cad_paymentcode']=='')){
							bool = false;
    						showError('选择增加操作时，明细表第' + item.data['cad_detno'] + '行的新收款方式编号不能为空');return;
						}
						if (item.data['cad_isdefault'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						bool = false;
						showError('默认收款方式只能选择一个,请重新选择!');return;
					}
					if(bool){
						var form = me.getForm(btn);
						if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
							me.BaseUtil.getRandomNumber();//自动添加编号
						}
						//保存之前的一些前台的逻辑判定
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
				click: function(btn){
					var grid = Ext.getCmp('grid'),items = grid.store.data.items;
					var bool = true;
					var i=0;
					Ext.each(items, function(item){
						if(item.data['cad_operation']=='修改' && (item.data['cad_oldpaymentcode']==null || item.data['cad_oldpaymentcode']=='')){
							bool = false;
    						showError('选择修改操作时，明细表第' + item.data['cad_detno'] + '行的原收款方式编号不能为空');return;
						}
						if(item.data['cad_operation']=='修改' && (item.data['cad_paymentcode']==null || item.data['cad_paymentcode']=='')){
							bool = false;
    						showError('选择修改操作时，明细表第' + item.data['cad_detno'] + '行的新收款方式编号不能为空');return;
						}
						if(item.data['cad_operation']=='删除' && (item.data['cad_oldpaymentcode']==null || item.data['cad_oldpaymentcode']=='')){
							bool = false;
    						showError('选择删除操作时，明细表第' + item.data['cad_detno'] + '行的原收款方式编号不能为空');return;
						}
						if(item.data['cad_operation']=='删除' && item.data['cad_isdefault'] == '是'){
							bool = false;
    						showError('选择删除操作时，明细表第' + item.data['cad_detno'] + '行的是否默认不能选是');return;
						}
						if(item.data['cad_operation']=='增加' && (item.data['cad_paymentcode']==null || item.data['cad_paymentcode']=='')){
							bool = false;
    						showError('选择增加操作时，明细表第' + item.data['cad_detno'] + '行的新收款方式编号不能为空');return;
						}
						if (item.data['cad_isdefault'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						bool = false;
						showError('默认收款方式只能选择一个,请重新选择!');return;
					}
					if(bool){
						var form = me.getForm(btn);
						if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
							me.BaseUtil.getRandomNumber();//自动添加编号
						}
						//保存之前的一些前台的逻辑判定
						this.FormUtil.onUpdate(this);
					}
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ca_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ca_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ca_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ca_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ca_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ca_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ca_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ca_id').value);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustomerPaymentsApply', '新增客户多付款方式维护申请', 'jsps/scm/sale/customerPaymentsApply.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=ca_cucode]':{
				afterrender: function(t){
					if (t.fieldConfig == 'PT') {
						t.dbBaseCondition = "cd_sellercode='" + em_code + "'";
					}
    			}
			},
			'dbfindtrigger[name=cad_oldpaymentcode]':{
				focus:function(t){
					t.autoDbfind = false;
    				var cu_id =Ext.getCmp('ca_cuid').value; 
    				if(cu_id==''||cu_id==null){
    					showError("请先选择客户 !");
    					return;
    				}
    				t.dbBaseCondition = "cp_cuid='" + cu_id + "'";
				}
			}
    	});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});