Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.MyBatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:['scm.sale.batchDeal.Viewport','scm.sale.batchDeal.MyForm','scm.sale.batchDeal.MyGridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.DeblockSplit','core.button.HandLocked'	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'#addToTempStore':{
    			click:function(){
    				this.addToTempStore();
    			}
    		},
    		'#checkTempStore':{
    			click:function(){
    				this.checkTempStore();
    			}
    		},
    		'erpMyBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealMyGridPanel');
    				var items = form.items.items, autoQuery = false;
    				form.onQuery();
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(!form.tempStore){
						grid.columns[1].hide();
					}
					if(form.source=='allnavigation'){
        				Ext.each(form.dockedItems.items[0].items.items,function(btn){
        					btn.setDisabled(true);
        				});
        			}
    			}  			
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    				grid.store.on('datachanged', function(store){//dataChanged事件
						me.getProductWh(grid);
					});
    			},
    		},
    		'field[name=differ]': {
				change: function(field){
					var grid = Ext.getCmp('batchDealGridPanel');
					me.countAmount(grid);
				}
    		},
    		'erpVastDealButton': {
    			click: {
    				fn: function(btn){
	    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
	    			},
	    			lock: 2000
    			}
    		},
    		'erpVastAnalyseButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastAllotButton':{
    			click:function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpSaveCostDetailButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastSaveCostDetail.action');
    			}
    		},
    		'erpDifferVoucherCreditButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastDifferVoucherCredit.action');
    			}
    		},
    		'erpNowhVoucherCreditButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastNowhVoucherCredit.action');
    			}
    		},
    		'SchedulerTrigger':{
				afterrender:function(trigger){					
					trigger.setFields=[{field:'va_vecard',mappingfield:'ID'},{field:'va_driver',mappingfield:'VA_DRIVER'}];
				}
			},
    		'erpEndCRMButton':{
    			click:function(btn){
    				me.vastDeal('crm/chanceTurnEnd.action');
    			}
    		},
    		'monthdatefield': {
				afterrender: function(f) {
					var type = '', con = null;
					if(f.name == 'vo_yearmonth' && caller == 'Voucher!Audit!Deal') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vo_yearmonth' && caller == 'Voucher!ResAudit!Deal') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vo_yearmonth' && caller == 'CashFlowSet') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vm_yearmonth' && caller == 'VendMonth!Cyf!Batch') {
						type = 'MONTH-V';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'cm_yearmonth' && caller == 'CustMonth!Cys!Batch') {
						type = 'MONTH-C';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'cd_yearmonth' && caller == 'Make!Cost!Deal') {
						type = 'MONTH-T';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'pc_yearmonth' && caller == 'ProjectCost!Deal') {
						type = 'MONTH-O';
						con = Ext.getCmp('condatefield');
					}
					if(type != '') {
						this.getCurrentMonth(f, type, con);
					}
				},
    			change: function(f) {
    				if(f.name == 'vo_yearmonth' &&( caller == 'Voucher!Audit!Deal'||caller == 'Voucher!ResAudit!Deal')){
        				if(!Ext.isEmpty(f.value)) {
        					var d = Ext.ComponentQuery.query('condatefield');
        					if(d && d.length > 0)
        						d[0].setMonthValue(f.value);
        				}
    				}

    			}
			},
			'erpRefreshQtyButton': {
				click : function() {
					this.refreshQty(caller);
				}
			},
			'gridcolumn[dataIndex=md_canuseqty]':{
    			 beforerender:function(column){
    			 }
    		}
    	
    	});
    }
});