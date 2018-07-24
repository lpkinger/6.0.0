Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.MonthAccount', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
    views: ['fa.gs.MonthAccount', 'core.feature.FloatingGrouping'],
    init:function(){
    	var me = this;
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
    		'#gsgrid': {
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
    	var me = this, grid = Ext.getCmp('gsgrid');
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gs/monthAccount.action',
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
    			type: 'MONTH-B'
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
    	var grid = chkbalance.ownerCt.ownerCt.down('gridpanel');
    	grid.store.filterBy(function(item) {
    		return chkbalance.value ? item.get('enddiff') != 0 : true;
    	});
    },
    loadDetail: function(record) {
    	var me = this, code = record.get('code'), name = record.get('name'),
    		currency = record.get('currency'), type = record.get('type'),
    		yearmonth = record.get('yearmonth'), chkun = Ext.getCmp('chkun').value;
    	if(name && Number(record.get('enddiff')) != 0){
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
					"jsps/fa/gs/showDiffer.jsp?type="+type+"&currency="+currency+
    				"&code="+code+"&name="+name+"&yearmonth="+yearmonth+"&chkun="+chkun+
    				'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
				}]
			});
			win.show();    									
		}
    },
    loadDifferAll: function() {
    	var ym = Ext.getCmp('info_ym').value, chkun = Ext.getCmp('chkun').value;
    	this.BaseUtil.onAdd('GLDifferAll_' + ym, '全部差异', 
    			'jsps/fa/gs/showDifferAll.jsp?yearmonth=' + ym + '&chkun=' + chkun);
    }
});