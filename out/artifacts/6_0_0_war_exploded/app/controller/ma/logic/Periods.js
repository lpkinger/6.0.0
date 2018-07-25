Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.Periods', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.logic.Periods','core.form.MonthDateField','core.button.Confirm'],
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
    		'monthdatefield': {
        		afterrender: function(f) {
        			me.getCurrentYearmonth(f);
        		}
        	},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'grid[id=check-Periods]': {
    			itemclick: function(selModel, record) {
    				var val = record.get('check');
    				if(val == 'error') {
    					me.showDetail(record);
    				}
    			}
    		},
    		'erpConfirmButton':{
    			click: function(btn){
					this.confirm();
				}
    		}
    	});
    },
    getCurrentYearmonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'ma/logic/getCurrentYearmonth.action',
    		method: 'GET',
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			} else if(rs.data) {
    				f.setValue(rs.data);
    			}
    		}
    	});
    },
    confirm: function(){
		Ext.Ajax.request({
			url : basePath + "ma/logic/addperiods.action",
			params:{
				date: Ext.getCmp('date').value
			},
			method:'post',
			callback:function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					Ext.Msg.alert("提示","操作成功！");
				} else {
					if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
		   					str = str.replace('AFTERSUCCESS', '');
		   					showMessage('提示', str);
		   					window.location.reload();
		   				} else {
		   					showError(str);return;
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
    			month: me.currentMonth,
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
    			if(idx == grid.store.data.length) {
    				var ch = 0;
    				grid.store.each(function(){
    					if(this.get('check') == 'checked') {
    						ch ++;
    					}
    				});
    				if(idx == ch) {
    					Ext.getCmp('confirm').setDisabled(false);
    				}
    			}
    		}
    	});
    },
    showDetail: function(record) {
    	var wid = 'win-' + record.get('type'),
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