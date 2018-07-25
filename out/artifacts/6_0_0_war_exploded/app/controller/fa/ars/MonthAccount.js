Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.MonthAccount', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
    views: ['fa.ars.MonthAccount', 'core.feature.FloatingGrouping'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			click: function(btn) {
    				me.getArAccount();
    			}
    		},
    		'button[id=alldiffer]': {
    			click: function(btn) {
    				me.loadARDifferAll();
    			}
    		},
    		'checkbox[id=chkbalance]': {
    			change: function(f) {
    				me.filterBalance();
    			}
    		},
    		'checkbox[id=chkun]': {
    			change: function(f) {
    				me.getArAccount();
    			}
    		},
    		'checkbox[id=chkdetail]': {
    			change: function(f) {
    				me.filterBalance();
    			}
    		},
    		'#info_ym': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		},
    		'#arMonthAccountGrid': {
    			afterrender: function(grid){
    				me.getArAccount();
    			},
    			itemclick: function(view, record) {
    				me.loadDetail(record);
    			}
    		}
    	});
    },
    getArAccount: function() {
    	var me = this, grid = Ext.getCmp('arMonthAccountGrid');
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/monthAccount.action',
    		params: {
    			condition: Ext.encode({chkun: Ext.getCmp('chkun').value})
    		},
    		timeout: 120000,
    		callback: function(opt, s, r) {
    			me.FormUtil.setLoading(false);
    			var rs = Ext.decode(r.responseText);
    			if(rs.success) {
    				grid.store.loadData(rs.data);
    				me.filterBalance();
    				grid.normalGrid.scrollByDeltaX(10000);
    			} else if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			}
    		}
    	});
    },
    /**
     * 显示客户明细;
     * 只显示有差额科目
     */
    filterBalance: function() {
    	var showerr = Ext.getCmp('chkbalance').value,
    		showdetail = Ext.getCmp('chkdetail').value;
    	var grid = Ext.ComponentQuery.query('gridpanel')[0];
    	grid.store.filterBy(function(item) {
    		var bool = true;
    		if(!showdetail) {
    			bool = item.get('isCount');
    		}
    		if(bool && showerr) {
    			bool = item.get('endbalance') != 0;
    		}
    		return bool; 
    	});
    },
    getCurrentMonth: function(f) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-C'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				me.currentMonth = rs.data.PD_DETNO;
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    loadDetail: function(record) {
    	var me = this, custcode = record.get('am_asscode'), custname = record.get('am_assname'),
    		currency = record.get('am_currency'), catecode = record.get('am_catecode'),
    		yearmonth = record.get('am_yearmonth'), chkun = Ext.getCmp('chkun').value;
    	if(custname && Number(record.get('endbalance')) != 0){
    		var win = new Ext.window.Window({
				id : 'win',
				title: '差异明细',
				height: "85%",
				width: "70%",
				maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				modal: true,
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe src="' + basePath + 
					"jsps/fa/ars/showARDiffer.jsp?catecode="+catecode+"&currency="+currency+
    				"&custcode="+custcode+"&custname="+custname+"&yearmonth="+yearmonth+"&chkun="+chkun+
    				'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
				}]
			});
			win.show();    									
		}
    },
    loadARDifferAll: function() {
    	var ym = Ext.getCmp('info_ym').value, chkun = Ext.getCmp('chkun').value;
    	this.BaseUtil.onAdd('ARDifferAll_' + ym, '全部客户差异', 
    			'jsps/fa/ars/showARDifferAll.jsp?yearmonth=' + ym + '&chkun=' + chkun);
    }
});