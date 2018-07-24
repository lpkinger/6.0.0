Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.MonthAccount', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
    views: ['co.cost.MonthAccount'],
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
    				me.loadDifferAll();
    			}
    		},
    		'#info_ym': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		},
    		'checkbox[id=chkbalance]': {
    			change: function(f) {
    				me.filterBalance();
    			}
    		},
    		'checkbox[id=chkun]': {
    			change: function(f) {
    				me.getAccount();
    			}
    		},
    		'#cogrid': {
    			afterrender: function(grid){
    				me.getAccount();
    			},
    			itemclick: function(view, record) {
    				me.loadDetail(record);
    			}
    		}
    	});
    },
    getAccount: function() {
    	var me = this, grid = Ext.getCmp('cogrid');
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'co/cost/monthAccount.action',
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
    getCurrentMonth: function(f) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-T'
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
    /**
     * 显示客户明细;
     * 只显示有差额科目
     */
    filterBalance: function() {
    	var chkbalance = Ext.getCmp('chkbalance');
    	var grid = Ext.getCmp('cogrid');
    	grid.store.filterBy(function(item) {
    		return chkbalance.value ? item.get('endbalance') != 0 : true;
    	});
    },
    loadDetail: function(record) {
    	var me = this, type = record.get('cm_code'), name = record.get('cm_name'),
    		yearmonth = record.get('cm_yearmonth'),chkun = Ext.getCmp('chkun').value;
    	if(name && Number(record.get('endbalance')) != 0){
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
					"jsps/co/cost/showDiffer.jsp?type="+type+"&name="+name+"&yearmonth="+yearmonth+"&chkun="+chkun+
    				'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
				}]
			});
			win.show();    									
		}
    },
    loadDifferAll: function() {
    	var ym = Ext.getCmp('info_ym').value, chkun = Ext.getCmp('chkun').value;
    	this.BaseUtil.onAdd('CODifferAll_' + ym, '全部科目差异', 
    			'jsps/co/cost/showDifferAll.jsp?yearmonth=' + ym + '&chkun=' + chkun);
    }
});