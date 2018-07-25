Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.WriteVoucher', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.gla.WriteVoucher', 'core.button.Accounted', 'core.grid.Panel2'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {
    			afterrender: function(grid) {
    				var f = Ext.getCmp('yearmonth');
    				this.getCurrentMonth(grid, f);
    				var total = Ext.getCmp('total'),
						enter = Ext.getCmp('enter'),
						commit = Ext.getCmp('commit'),
						audit = Ext.getCmp('audit');
    				this.getCount(total, enter, commit, audit);
    			}
    		},
    		'erpAccountedButton': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');
    				grid.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'fa/ars/accountVoucher.action',
    					params: {
    						month: me.currentMonth
    					},
    					timeout: 150000,
    					callback: function(opt, s, r) {
    						grid.setLoading(false);
    						var rs = Ext.decode(r.responseText);
    						if(rs.result) {
    							showMessage('提示', rs.result);
    							window.location.reload();
    						} else if(rs.exceptionInfo) {
    							showError(rs.exceptionInfo);
    						} else {
    							alert('OK!');
    							window.location.reload();
    						}
    					}
    				});
    			}
    		}
    	});
    },
    getCurrentMonth: function(grid, f) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				me.currentMonth = rs.data.PD_DETNO;
    				me.datestart = Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Ymd');
    				me.dateend = Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Ymd');
    				f.setText(rs.data.PD_DETNO + ' 从' + Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Y-m-d')
    						+ ' 到    ' + Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Y-m-d'));
    				grid.GridUtil.loadNewStore(grid, {caller: caller, condition: 'vo_yearmonth=' +
    					me.currentMonth + ' AND vo_statuscode=\'AUDITED\''});
    			}
    		}
    	});
    },
    getCount: function(total, enter, commit, audit) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getVoucherCount.action',
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				total.setText(String(rs.data.total));
    				enter.setText(String(rs.data.enter));
    				commit.setText(String(rs.data.commit));
    				audit.setText(String(rs.data.audit));
    			}
    		}
    	});
    }
});