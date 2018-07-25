Ext.QuickTips.init();
Ext.define('erp.controller.common.Print', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.print.Viewport','common.print.Form','core.trigger.DbfindTrigger','core.form.FtField',
     		'core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger','core.form.MonthDateField','core.trigger.MasterTrigger',
     		'core.form.FtFindField','core.form.ConDateField','core.form.MultiField','core.form.ConMonthDateField','core.form.YnField'
     	],
    init:function(){
    	this.control({ 
    		'erpPrintFormPanel button[name=confirm]': {
    			click: function(btn){
    				//...检查dbFind出来的单据是否满足打印条件
    				//...交付打印
    			}
    		},
    		'monthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'cd_yearmonth') {
						type = 'MONTH-T';
					}
					/*if(f.name == 'vo_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'cm_yearmonth') {
						type = 'MONTH-A';
					}*/
					if(type != '') {
						this.getCurrentMonth(f, type);
					}
				}
			}
    	});
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