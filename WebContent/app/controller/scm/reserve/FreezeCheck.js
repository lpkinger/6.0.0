Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.FreezeCheck', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['scm.reserve.FreezeCheck','core.form.MonthDateField'],
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=check]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.hide();
    				me.check(grid, 0, btn);
    			}
    		},
    		'#allow' : {
    			change : function(f) {
    				if(!me.checked) {
    					if(f.getValue()) {
	    					Ext.getCmp('accoutover').setDisabled(false);
	    				} else {
	    					Ext.getCmp('accoutover').setDisabled(true);
	    				}
    				}
    			}
    		},
    		'button[id=accoutover]': {
    			click: function() {
    				this.startAccount();
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[id=resaccoutover]': {
    			click: function() {
    				this.overAccount();
    			}
    		},
    		'grid[id=account-check]': {
    			itemclick: function(selModel, record) {
    				var val = record.get('check');
    				if(val == 'error') {
    					me.showDetail(record);
    				}
    			}
    		},
    		'#yearmonth': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			},
    			change: function(f){
    				if(f.value) {
	    				me.currentMonth = f.getValue();
	    				me.datestart = Ext.Date.format(Ext.Date.getFirstDateOfMonth(Ext.Date.parse(me.currentMonth, 'Ym')), 'Ymd');
	    				me.dateend = Ext.Date.format(Ext.Date.getLastDateOfMonth(Ext.Date.parse(me.currentMonth, 'Ym')), 'Ymd');
    					me.preMonth = Ext.Date.format(new Date(Ext.Date.getFirstDateOfMonth(Ext.Date.parse(me.currentMonth, 'Ym'))-24*60*60*1000) , 'Ym');
    				}
    			}
    		},
    		'#date': {
    			afterrender: function(f) {
    				me.getFreezeDetno(f);
    			}
    		}
    	});
    },
    startAccount: function(){
    		var me = this;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "scm/reserves/Periodsdetailfreeze.action",
    			params:{
    				pd_detno : Ext.getCmp('yearmonth').value
    			},
    			timeout: 120000,
    			method:'post',
    			callback:function(options,success,response){
    				me.FormUtil.setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if (localJson.success) {
						window.location.reload();
					} else {
						if (localJson.exceptionInfo) {
							var str = localJson.exceptionInfo;
							if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {// 特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								showMessage('提示', str);
								window.location.reload();
							} else {
								showError(str);
								return;
							}
						}
					}
    			}
    		});
    	},
    	overAccount: function(){
    		var me = this;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "scm/reserves/Periodsdetailcancelfreeze.action",
    			method:'post',
    			callback:function(options,success,response){
    				me.FormUtil.setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if (localJson.success) {
						window.location.reload();
					} else {
						if (localJson.exceptionInfo) {
							var str = localJson.exceptionInfo;
							if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {// 特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								showMessage('提示', str);
								window.location.reload();
							} else {
								showError(str);
								return;
							}
						}
					}
    			}
    		});
    	},
    check: function(grid, idx, btn) {
    	var me =this,f = grid.store.getAt(idx);
    	if(!f) {
    		btn.setDisabled(false);
    		return;
    	}
    	f.set('check', 'loading');
    	var win = Ext.getCmp('win-' + f.get('type'));
    	if(win) {
    		win.destroy();
		}
    	Ext.Ajax.request({
    		url: basePath + f.get('action'),
    		params: {
    			type: f.get('type'),
    			month: me.currentMonth
    			//pmonth: me.preMonth,
    			//start: me.datestart,
    			//end: me.dateend
    		},
    		callback: function(opt, s, r) {
    			me.check(grid, ++idx, btn);
    			var rs = Ext.decode(r.responseText);
    			if(rs.ok) {
    				f.set('check', 'checked');
    			} else {
    				f.set('check', 'error');
    			}
    			if(idx == grid.store.data.length) {
    				var ch = 0;
    				grid.store.each(function(){
    					if(this.get('check') == 'checked') {
    						ch ++;
    					}
    				});
    				if(idx == ch) {
    					me.checked = true;
    					Ext.getCmp('accoutover').setDisabled(false);
    				} else {
    					me.checked = false;
    					Ext.getCmp('allow').show();
    				}
    			}
    		}
    	});
    },
    showDetail: function(record) {
    	var me = this, wid = 'win-' + record.get('type'),
    		win = Ext.getCmp(wid);
    	if(!win) {
    		win = Ext.create('Ext.Window', {
        		title: record.get('value'),
        		id: wid,
        		width: 800,
        		height: 500,
        		layout: 'anchor',
        		items: [{
        			xtype: 'gridpanel',
        			anchor: '100% 100%',
        			columnLines: true,
        			cls: 'custom',
        			columns: [{
        				text: '单据',
        				flex: 1,
        				dataIndex: 'be_class'
        			},{
        				text: '编号',
        				flex: 1.6,
        				dataIndex: 'be_code'
        			},{
        				text: '检测时间',
        				flex: 1,
        				dataIndex: 'be_date',
        				renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d');}
        			},{
        				text: '检测人',
        				flex: .8,
        				dataIndex: 'be_checker'
        			},{
        				text: '备注',
        				flex: 3,
        				dataIndex: 'be_remark'
        			}],
        			store: new Ext.data.Store({
        				fields: ['be_class', 'be_code', 'be_date', 'be_checker', 'be_remark']
        			})
        		}],
        		buttonAlign: 'center',
        		buttons: [{
        			text: $I18N.common.button.erpExportButton,
        			iconCls: 'x-button-icon-excel',
        	    	cls: 'x-btn-blue',
        			handler: function(btn) {
        				me.BaseUtil.exportGrid(btn.up('window').down('gridpanel'));
        			}
        		},{
        			text: $I18N.common.button.erpCloseButton,
        			cls: 'x-btn-blue',
        			handler: function(btn) {
        				btn.ownerCt.ownerCt.close();
        			}
        		}]
        	});
    		this.getBillError(record.get('type'), win.down('gridpanel'));
    	}
    	win.show();
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
    				me.datestart = Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Ymd');
    				me.dateend = Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Ymd');
    				me.preMonth = Ext.Date.format(new Date(rs.data.PD_STARTDATE-24*60*60*1000) , 'Ym');
    				f.setValue(me.currentMonth);
    			}
    		}
    	});
    },
    getBillError: function(type, grid) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getBillError.action',
    		params: {
    			type: type
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				grid.store.loadData(rs.data);
    			} else if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			}
    		}
    	});
    },
    getFreezeDetno: function(f) {
		Ext.Ajax.request({
			url: basePath + 'scm/reserve/getFreezeDetno.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				} else {
					f.setValue('无');
				}
			}
		});
	}
});