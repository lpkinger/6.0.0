Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustTurn', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.sale.CustTurn', 'core.form.Panel', 'core.form.MultiField',
			'core.grid.Panel2', 'core.button.Add', 'core.button.Save',
			'core.button.Submit','core.button.Audit','core.button.ResAudit',
			'core.button.ResSubmit',
			'core.button.Close', 'core.button.Upload', 'core.button.Update',
			'core.button.Delete', 'core.button.Sync',
			'core.trigger.MultiDbfindTrigger', 'core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger', 'core.form.YnField'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'erpFormPanel':{
				afterrender:this.afterrender
			},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null
							|| Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					var ct_ysellercode = Ext.getCmp('ct_ysellercode'),ct_newsellercode = Ext.getCmp('ct_newsellercode');
					if(ct_ysellercode && ct_newsellercode && ct_ysellercode.value == ct_newsellercode.value){
						showError('原业务员和新业务员不能一致');
					}else{
						this.FormUtil.beforeSave(this);
					}
				}
			},
			'erpUpdateButton' : {
				click: function(btn){
					var ct_ysellercode = Ext.getCmp('ct_ysellercode'),ct_newsellercode = Ext.getCmp('ct_newsellercode');
					if(ct_ysellercode && ct_newsellercode && ct_ysellercode.value == ct_newsellercode.value){
						showError('原业务员和新业务员不能一致');
					}else{
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ct_id').value);
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ct_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ct_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ct_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ct_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ct_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ct_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ct_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ct_id').value);
				}
			},
			'multidbfindtrigger[name=cd_custcode]': {
    			afterrender:function(trigger){
	    			//主记录放大镜值
	    			trigger.dbKey='ct_ysellercode';
	    			//映射的值
	    			trigger.mappingKey='cd_sellercode';
	    			trigger.dbMessage='请先选择原业务员';
    			}
    		},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addCustTurn', '新增客户转移',
							'jsps/scm/sale/custTurn.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {// grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	afterrender:function(form){
		  var configItem=[{
			        columnWidth:0.5,
			        fieldStyle:"background:#FFFAFA;color:#515151;",
			        html:"<p>【注：类型=移交时，原业务员的客户分配明细将转交到新业务员；类型=复制时，原业务员的客户分配明细将复制到新业务员；类型=删除时，原业务员的客户分配明细将删除】</p>",
			        id:"ct_auditdate",
			        xtype:"textfield",
        }];
		form.add(configItem);
		form.doLayout();
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});