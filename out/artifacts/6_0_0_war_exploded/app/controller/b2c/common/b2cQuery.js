Ext.QuickTips.init();
Ext.define('erp.controller.b2c.common.b2cQuery', {
	extend : 'Ext.app.Controller',
	views : ['b2c.common.Viewport','b2c.common.b2cQueryForm','b2c.common.b2cQueryGrid','erp.view.b2c.sale.b2cPanel','core.trigger.AddDbfindTrigger','core.trigger.DbfindTrigger','core.form.FtNumberField',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField','core.form.YearDateField',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 'core.button.Refresh',
			'core.form.ConMonthDateField', 'core.trigger.TextAreaTrigger','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
			],
	init : function() {
		var me=this;
		var emcode = null;
		var value='';
		this.control({
			'erpQueryFormPanel' : {
				beforerender : function(form){
				},
				alladded : function(form) {
					var items = form.items.items, autoQuery = false;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoShowTriggerWin=false;
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(autoQuery) {
						setTimeout(function(){
							form.onQuery();
						}, 1000);
					}
				}
			},
			'button[name=refresh]':{
    			click: function(btn){   
    				var form = me.getForm(btn);
    				form.onQuery();
    			}
			},
			'erpQueryGridPanel' : {
				itemclick : this.onGridItemClick,
			},
			'monthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'cd_yearmonth') {
						type = 'MONTH-T';
					}
					if(f.name == 'cmc_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'cm_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'am_yearmonth') {
						type = 'MONTH-B';
					}
					if(type != '' && Ext.isEmpty(getUrlParam(f.name))) {
						this.getCurrentMonth(f, type);
					}
				}
			},
			'conmonthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'cd_yearmonth') {
						type = 'MONTH-T';
					}
					if(f.name == 'cmc_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'cm_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'am_yearmonth') {
						type = 'MONTH-B';
					}
					if(type != '' && Ext.isEmpty(getUrlParam(f.name))) {
						this.getCurrentMonth(f, type);
					}
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {
		
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCurrentMonth: function(f, type) {
	    Ext.Ajax.request({
	    	url: basePath + 'fa/getMonth.action',
	    	params: {
	    		type: type
	    	},
	    	callback: function(opt, s, r) {
	    		var rs = Ext.decode(r.responseText);
	    		if(rs.data) {
	    			f.setValue(rs.data.PD_DETNO);
	    		}
	    	}
	    });
	}
});