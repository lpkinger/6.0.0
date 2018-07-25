Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ReceiveBudget', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fa.fp.ReceiveBudget','core.form.Panel',
			'core.button.CalBudget',
			'core.button.Add','core.button.Save','core.button.Update','core.button.Delete',
			'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
			'core.grid.Panel2','core.toolbar.Toolbar','core.grid.YnColumn',
			'core.button.Sync','core.button.Close','core.button.Update',
			'core.trigger.DbfindTrigger','core.form.YnField','core.form.MonthDateField',
			'core.form.FileField','core.trigger.MultiDbfindTrigger',
			'core.form.FtDateField','core.form.ConDateFPField'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			/*'erpFormPanel' : {
    			afterload : function(form) {
    				form.getForm().getFields().each(function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val) && Ext.isEmpty(this.getValue())) {
							this.setValue(val);
						}
					});
				}
    		},*/

			'field[name=rb_code]' : {
  			  aftertrigger : function(f) {
  				  if (f.value != null && f.value != '') {
  					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
  						  caller: caller,
  						  condition: 'rbd_rbid='+ Ext.getCmp('rb_id').value 						  
  					  });
  				  }
  			  }
  		    },
    		'condatefpfield[name=rb_period]': {
    			afterrender:function(t){
    				Ext.defer(function(){
    					me.hidecolumns(t.combo.value);
    				}, 100);
    				t.combo.on('select', function(m){
    					Ext.defer(function(){
    						me.hidecolumns(m.value);
    					}, 100);
    				});
    		    }
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addReceiveBudget', '新收款预算', 'jsps/fa/fp/ReceiveBudget.jsp');
    			}
    		},
			'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('rb_id').value);
    			}
    		}, 
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('rb_id').value);
    			}
    		},
			'erpCalBudgetButton' : {
				afterrender: function(btn){
    				var status = Ext.getCmp('rb_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					grid.setLoading(true);
					Ext.Ajax.request({
						url : basePath + "fa/fp/ReceiveBudgetCalBudget.action",
						params:{
			    			yearmonth:Ext.getCmp('rb_date').value,
						},
						method : 'post',
						timeout : 300000,
						callback : function(options, success, response) {
							grid.setLoading(false);
							var res = Ext.decode(response.responseText);
							if (res.exceptionInfo) {
								showError(res.exceptionInfo);
								return;
							}
							if (res.success) {
								showMessage("提示", "计算收款预算成功！");
							}
						}
					});
				}
			}
		});
	},
	hidecolumns:function(value){
		if(!Ext.isEmpty(value)) {
			var grid = Ext.getCmp("grid");
			if(value <= 2){
				grid.down('gridcolumn[dataIndex=rbd_weekone]').show();
				grid.down('gridcolumn[dataIndex=rbd_weektwo]').show();
				grid.down('gridcolumn[dataIndex=rbd_weekthree]').show();
				grid.down('gridcolumn[dataIndex=rbd_weekfour]').show();
				grid.down('gridcolumn[dataIndex=rbd_monthone]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monthtwo]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monththree]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season1]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season2]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season3]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season4]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalmonth]').show();
				grid.down('gridcolumn[dataIndex=rbd_subtotalseason]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalyear]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotal]').hide();
			} else if(value >= 2 && value <= 6){
				grid.down('gridcolumn[dataIndex="rbd_weekone"]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weektwo]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weekthree]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weekfour]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monthone]').show();
				grid.down('gridcolumn[dataIndex=rbd_monthtwo]').show();
				grid.down('gridcolumn[dataIndex=rbd_monththree]').show();
				grid.down('gridcolumn[dataIndex=rbd_season1]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season2]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season3]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season4]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalmonth]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalseason]').show();
				grid.down('gridcolumn[dataIndex=rbd_subtotalyear]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotal]').hide();	
			} else if(value == 7){
				grid.down('gridcolumn[dataIndex=rbd_weekone]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weektwo]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weekthree]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weekfour]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monthone]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monthtwo]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monththree]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season1]').show();
				grid.down('gridcolumn[dataIndex=rbd_season2]').show();
				grid.down('gridcolumn[dataIndex=rbd_season3]').show();
				grid.down('gridcolumn[dataIndex=rbd_season4]').show();
				grid.down('gridcolumn[dataIndex=rbd_subtotalmonth]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalseason]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalyear]').show();
				grid.down('gridcolumn[dataIndex=rbd_subtotal]').hide();	
			} else if(value == 8){
				grid.down('gridcolumn[dataIndex=rbd_weekone]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weektwo]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weekthree]').hide();
				grid.down('gridcolumn[dataIndex=rbd_weekfour]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monthone]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monthtwo]').hide();
				grid.down('gridcolumn[dataIndex=rbd_monththree]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season1]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season2]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season3]').hide();
				grid.down('gridcolumn[dataIndex=rbd_season4]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalmonth]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalseason]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotalyear]').hide();
				grid.down('gridcolumn[dataIndex=rbd_subtotal]').show();	
			}
		}
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});