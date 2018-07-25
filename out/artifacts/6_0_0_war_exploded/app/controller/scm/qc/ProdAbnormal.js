Ext.QuickTips.init();
Ext.define('erp.controller.scm.qc.ProdAbnormal', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.qc.ProdAbnormal','core.toolbar.Toolbar','core.form.MultiField',
      			'core.form.FileField','core.form.CheckBoxGroup',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      			'core.button.ResSubmit','core.button.Flow','core.button.Check','core.button.ResCheck',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
    				var inqty = Ext.Number.from(Ext.getCmp('pa_inqty').getValue(), 0), 
						checkqty = Ext.Number.from(Ext.getCmp('pa_checkqty').getValue(), 0),
						ngqty = Ext.Number.from(Ext.getCmp('pa_ngqty').getValue(), 0);
					if(checkqty > inqty){
						showError('抽检数不能大于来料数量！');return;
					}
					if(ngqty > checkqty){
						showError('不合格数量不能大于抽检数！');return;
					}
					var form = Ext.getCmp('form');
					if(checkqty != 0){
						Ext.getCmp('pa_ngrate').setValue(form.BaseUtil.numberFormat(ngqty/checkqty, 6));
					}
					this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var inqty = Ext.Number.from(Ext.getCmp('pa_inqty').getValue(), 0), 
    					checkqty = Ext.Number.from(Ext.getCmp('pa_checkqty').getValue(), 0),
    					ngqty = Ext.Number.from(Ext.getCmp('pa_ngqty').getValue(), 0);
    				if(checkqty > inqty){
    					showError('抽检数不能大于来料数量！');return;
    				}
    				if(ngqty > checkqty){
    					showError('不合格数量不能大于抽检数！');return;
    				}
    				var form = Ext.getCmp('form');
    				if(checkqty != 0){
    					Ext.getCmp('pa_ngrate').setValue(form.BaseUtil.numberFormat(ngqty/checkqty, 6));
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProdAbnormal', '新增物料品质异常联络单', 'jsps/scm/qc/prodAbnormal.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pa_statuscode'), checkstatus = Ext.getCmp('pa_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value == 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pa_statuscode'), checkstatus = Ext.getCmp('pa_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value == 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pa_checkstatuscode');
    				if(status && status.value != 'UNAPPROVED'){
    					btn.hide();
    				}
    				var auditstatus = Ext.getCmp('pa_statuscode');
    				if(auditstatus && auditstatus.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pa_statuscode'), checkstatus = Ext.getCmp('pa_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value == 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pa_statuscode'), checkstatus = Ext.getCmp('pa_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value != 'UNAPPROVED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onCheck(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpResCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pa_checkstatuscode');
    				if(status && status.value != 'APPROVE' ){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResCheck(Ext.getCmp('pa_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var condition = '{ProdAbnormal.pa_id}=' + Ext.getCmp('pa_id').value + '';
    				var id = Ext.getCmp('pa_id').value;
    				reportName="ProdAbnormal";
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});