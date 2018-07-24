Ext.QuickTips.init();
Ext.define('erp.controller.co.inventory.ProductWHMonthAdjust', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','co.inventory.ProductWHMonthAdjust','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Post','core.button.ResPost','core.form.MonthDateField',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.StatusField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'field[name=pwa_date]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value)){
						me.getCurrentMonth(function(end, currentMonth){
							f.setValue(end);
							var ym = Ext.getCmp('pwa_yearmonth');
							ym && (ym.setValue(currentMonth));
						});
    				}
				}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
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
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
    					return;
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductWHMonthAdjust', '新增期初调整单维护', 'jsps/co/inventory/productWHMonthAdjust.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
    					return;
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
    					return;
    				}
    				me.FormUtil.onAudit(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
    					return;
    				}
    				me.FormUtil.onPost(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpCertificateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
    					return;
    				}
    				me.FormUtil.onEnd(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpResCertificateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pwa_statuscode');
    				if(status && status.value != 'CERTIFICATED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('pwa_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('pwa_date').value, 'Ym') != Ext.getCmp('pwa_yearmonth').value){
    					showError("调整日期所属期间与期间编号不一致");
    					return;
    				}
    				me.FormUtil.onPrint(Ext.getCmp('pwa_id').value);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCurrentMonth : function(callback) {
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'fa/getMonth.action',
			params : {
				type : 'MONTH-P'
			},
			callback : function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if (rs.data) {
					me.currentMonth = rs.data.PD_DETNO;
					me.datestart = Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Ymd');
					me.dateend = Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Y-m-d');
					callback.call(null, me.dateend, me.currentMonth);
				}
			}
		});
	}
});