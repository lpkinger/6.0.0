Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.ReturnApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.ReturnApply','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.TurnReturn',
  			'core.button.ResSubmit','core.button.ResAudit','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'field[name=ra_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ra_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(new Date(value), 'Ym');
    				}
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
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ra_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addReturnApply', '新增退货申请单', 'jsps/scm/sale/returnApply.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ra_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ra_id').value);
				}
			},
    		'erpPrintButton':{
    			click:function(btn){
    				var reportName="ReturnApply";
    				var condition='{ReturnApply.ra_id}='+Ext.getCmp('ra_id').value+'';
    				var id=Ext.getCmp('ra_id').value;
    				me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    		},
    		'erpTurnReturnButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ra_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.batchdeal('ReturnApply!ToSaleReturn!Deal', 'rad_raid=' + Ext.getCmp('ra_id').value + ' AND nvl(rad_yqty,0)<rad_qty', 'scm/sale/turnReturn.action');
    			}
    		},
    		'field[name=ra_custcode]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != ''){
    					f.setReadOnly(true);
    					f.setFieldStyle(f.fieldStyle + ';background:#f1f1f1;');
    				}
    			}
    		},
    		'dbfindtrigger[name=ra_paymentscode]': {
    			afterrender:function(trigger){
    				if(trigger.fieldConfig == 'PT') {
    	    			trigger.dbKey='ra_custcode';
    	    			trigger.mappingKey='cu_code';
    	    			trigger.dbMessage='请先选客户编号！';	
    				}
    			}
    		},
    		'dbfindtrigger[name=rad_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('ra_custcode')){
    					var code = Ext.getCmp('ra_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
        				}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('ra_custcode')){
    					var obj = me.getCodeCondition();
    					me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    				}
    			}
    		},
    		'dbfindtrigger[name=rad_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['rad_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sa_code='" + code + "'";
    				}
    			}
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	batchdeal: function(nCaller, condition, url){
    	var win = new Ext.window.Window({
	    	id : 'win',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
			    items: [{
			    	  tag : 'iframe',
			    	  frame : true,
			    	  anchor : '100% 100%',
			    	  layout : 'fit',
			    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
			    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	name: 'confirm',
			    	text : $I18N.common.button.erpConfirmButton,
			    	iconCls: 'x-button-icon-confirm',
			    	cls: 'x-btn-gray',
			    	listeners: {
			    		buffer: 500,
			    		click: function(btn) {
			    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
   				    		btn.setDisabled(true);
   				    		grid.updateAction(url);
			    		}
			    	}
			    }, {
			    	text : $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(){
			    		Ext.getCmp('win').close();
			    	}
			    }]
			});
			win.show();
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * rad_ordercode的限制条件
	 */
	getCodeCondition: function(){
		var field = null;
		var fields = '';
		var tablename = '';
		var myfield = '';
		var tFields = '';
		var obj = new Object();
		
		tFields = 'ra_custid,ra_custcode,ra_custname,ra_currency,ra_rate,ra_paymentscode,ra_payments,ra_sellercode,ra_seller';
		fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_paymentscode,sa_payments,sa_sellercode,sa_seller';
		tablename = 'Sale';
		myfield = 'sa_code';
		field = "sa_custcode";
		
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	}
});