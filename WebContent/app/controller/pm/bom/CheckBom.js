Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.CheckBom', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['pm.bom.CheckBom','core.form.Panel','core.button.CheckBom','core.button.Close','core.button.ExportBomCheckMsg',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	   ],
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=checkbombtn]': {
    			click: function(btn) {
    				var grid = Ext.getCmp('bom-check');
    				grid.store.each(function(r){
    					r.set('check', 'loading');
    					var win = Ext.getCmp('win-' + r.get('TYPE'));
    	                if(win) {
    	                	 win.destroy();
		                   }
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
    		'grid[id=bom-check]': {
    			itemclick: function(selModel, record) {
    				var val = record.get('check');
    				if(val == 'error') {
    					me.showDetail(record);
    				}
    			}
    		}
    	});
    },
    check: function(grid, idx, btn) {
    	var me =this;
    	var form =Ext.getCmp('formPanel');
    	var jsonGridData = new Array();
    	var s = grid.getStore().data.items;
    	for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				jsonGridData.push(Ext.JSON.encode(data));
    	}   	
    	Ext.Ajax.request({
    		url: basePath + 'pm/bomCheck/checkBom.action',
    		params: {
    			bomId: Ext.getCmp(form.keyField).value,
    			bomMotherCode:Ext.getCmp('pr_code').value,
    			gridStore:unescape(jsonGridData.toString())
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);    			
    			var data=rs.ok;
    			data = eval('('+data+')');
        		if(data != null && data.length > 0){
        			Ext.each(data, function(item, index){
        				if(item.result=='true'){
        				grid.getStore().getAt(index).set('check','checked');
        				} else{
        					grid.getStore().getAt(index).set('check','error');
        				}
        				
        			});
        	   }
	    	   var bool = false;
	           Ext.each(grid.getStore().data.items, function(item, index){
	        		if(item.get('check')=='error'){
	        			bool = true;
	        		}
	    	   });
	           Ext.getCmp('ExportBomCheckMsg').setDisabled(!bool);
    		}
    	});
    	btn.haveChecked = Ext.getCmp(form.keyField).value;
    	btn.setDisabled(false);
    },
    showDetail: function(record) {
    	var me = this, wid = 'win-' + record.get('TYPE'),
    		win = Ext.getCmp(wid);
    	if(!win) {
    		win = Ext.create('Ext.Window', {
        		title: record.get('VALUE'),
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
        				text: '编号',
        				flex: 0.5,
        				dataIndex: 'BM_ID'
        			},{
        				text: 'BOMID',
        				flex: 1,
        				dataIndex: 'BM_BOMID'
        			},{
        				text: '检测条目',
        				flex: 1,
        				dataIndex: 'BM_ITEM'
        			},{
        				text: '异常描述',
        				flex: 1.5,
        				dataIndex: 'BM_DESCRIPTION'
        			},{
        				text: '检测时间',
        				flex: 1,
        				dataIndex: 'BM_DATE',
        				renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d');}
        			}],
        			store: new Ext.data.Store({
        				fields: ['BM_ID', 'BM_BOMID', 'BM_ITEM', 'BM_DESCRIPTION', 'BM_DATE']
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
    		this.getBomError(record.get('VALUE'), win.down('gridpanel'));
    	}
    	win.show();
    },
    getBomError: function(type, grid) {
    	var form =Ext.getCmp('formPanel');
    	Ext.Ajax.request({
    		url: basePath + 'pm/bomCheck/getBomMessage.action',
    		params: {
    			bomId: Ext.getCmp(form.keyField).value,
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