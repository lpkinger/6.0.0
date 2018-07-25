Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.BeforeAccount', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['co.cost.BeforeAccount'],
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
    				btn.setDisabled(true);
    				me.check(grid, 0, btn);
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'grid[id=account-check]': {
    			itemclick: function(selModel, record) {
    				var val = record.get('check');
    				if(val == 'error') {
    					me.showDetail(selModel.ownerCt, record);
    				}
    			}
    		},
    		'tbtext[id=yearmonth]': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
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
    			month: me.currentMonth,
    			pmonth: me.preYearmonth,
    			start: me.datestart,
    			end: me.dateend
    		},
    		callback: function(opt, s, r) {
    			me.check(grid, ++idx, btn);
    			var rs = Ext.decode(r.responseText);
    			if(rs.ok) {
    				f.set('check', 'checked');
    			} else {
    				f.set('check', 'error');
    			}
    		}
    	});
    },
    checkAll: function(grid, record) {
    	var me =this;
    	record.set('check', 'loading');
    	Ext.Ajax.request({
    		url: basePath + record.get('action'),
    		params: {
    			type: record.get('type'),
    			month: me.currentMonth,
    			pmonth: me.preYearmonth,
    			start: me.datestart,
    			end: me.dateend,
    			all: true
    		},
    		async: false,
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.ok) {
    				record.set('check', 'checked');
    			} else {
    				record.set('check', 'error');
    			}
    		}
    	});
    },
    exportExcel: function(type) {
    	var title = Ext.Date.format(new Date(), 'Y-m-d H:i:s');
		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		}  
		Ext.Ajax.request({
			url: basePath + ('fa/exportBillError.xls'),
			method: 'post',
			form: Ext.fly('ext-grid-excel'),
			isUpload: true,
			params: {
				type: type,
				title: title
			}
		});
    },
    getAll: function(grid, record) {
    	var p = Ext.create('Ext.ProgressBar', {
    		renderTo: Ext.getBody(),
    		floating: true,
    		width: 300,
    		value: 0.4,
    		text: '获取全部数据...'
    	}).show();
    	this.checkAll(grid, record);
    	p.updateProgress(0.8, '正在下载...', true);
    	this.exportExcel(record.get('type'));
    	Ext.defer(function(){
    		p.setVisible(false);
    	}, 3000);
    },
    showDetail: function(grid, record) {
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
        			text: '导出全部',
        			iconCls: 'x-button-icon-excel',
        	    	cls: 'x-btn-blue',
        			handler: function(btn) {
        				me.getAll(grid, record);
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
    			type: 'MONTH-T'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				me.currentMonth = rs.data.PD_DETNO;
    				me.datestart = Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Ymd');
    				me.dateend = Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Ymd');
    				me.preYearmonth = rs.data.PreYearmonth;
    				f.setText(rs.data.PD_DETNO + ' (从' + Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Y-m-d')
    						+ ' 到    ' + Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Y-m-d') + 
    						') 上个期间: ' + rs.data.PreYearmonth);
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
    }
});