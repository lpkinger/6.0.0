Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.MonthAccount', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
    views: ['scm.reserve.MonthAccount'],
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=query]': {
    			click: function(btn) {
    				me.getAccount();
    			}
    		},
    		'button[id=alldiffer]': {
    			click: function(btn) {
    				me.loadARDifferAll();
    			}
    		},
    		'#scmMonthAccountGrid': {
    			afterrender: function(grid){
    				me.getAccount();
    			},
    			itemclick: function(view, record) {
    				me.loadDetail(record);
    			}
    		},
    		'checkbox[id=chkun]': {
    			change: function(f) {
    				me.getAccount();
    			}
    		},
    		'checkbox[id=chkbalance]': {
    			change: function(f) {
    				me.filterBalance();
    			}
    		},
    		'#info_ym': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		}
    	});
    },
    loadDetail: function(record) {
    	var me = this, catecode = record.get('cm_catecode'), catename = record.get('cm_catename'),
    		yearmonth = record.get('cm_yearmonth'),chkun = Ext.getCmp('chkun').value;
    	if(catename && Number(record.get('endbalance')) != 0){
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
					"jsps/scm/reserve/monthAccountDetail.jsp?catecode="+catecode+"&catename="+catename+"&yearmonth="+yearmonth+"&chkun="+chkun+
    				'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
				}]
			});
			win.show();    									
		}
    },
    loadARDifferAll: function() {
    	var ym = Ext.getCmp('info_ym').value, chkun = Ext.getCmp('chkun').value;
    	this.BaseUtil.onAdd('ARDifferAll_' + ym, '全部科目差异', 
    			'jsps/scm/reserve/showDifferAll.jsp?yearmonth=' + ym + '&chkun=' + chkun);
    },
    getAccount: function() {
    	var me = this, grid = Ext.getCmp('scmMonthAccountGrid');
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'scm/reserve/monthAccount.action',
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
    	var chkbalance = Ext.getCmp('chkbalance');
    	var grid = chkbalance.ownerCt.ownerCt.down('gridpanel');
    	grid.store.filterBy(function(item) {
    		var bool = true;
    		if(bool && chkbalance.value) {
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
    			type: 'MONTH-P'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				me.currentMonth = rs.data.PD_DETNO;
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    }
});