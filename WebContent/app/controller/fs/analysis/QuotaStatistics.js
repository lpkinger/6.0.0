Ext.QuickTips.init();
Ext.define('erp.controller.fs.analysis.QuotaStatistics', {
	extend : 'Ext.app.Controller',
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : ['fs.analysis.StatisticsForm', 'core.grid.Panel2','fs.analysis.QuotaStatistics', 'core.form.ConDateField','core.trigger.DbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpStatisticsForm #query' : {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt;
					me.onQuery(form);
				}
			},
			'#seller':{
				itemclick:function(view,record){
					var cqcode = record.data['cq_code'];
					if(!cqcode){
						return;
					}
					var condition = " cq_code = '"+cqcode+"'";
					me.createWin(condition);
				}
			},
			'erpGridPanel2': {
				afterrender : function(grid){
					var form = grid.ownerCt.down('erpStatisticsForm');
					me.onQuery(form);
				}
			}
		})
	},
	onQuery: function(form){
		var me = this, condition ='';
		var custcode = form.down('#custcode'),groupby = "";
		if(custcode && custcode.value){
			condition += "cq_custcode = '" + custcode.value + "'";
			groupby = " group by cq_custcode";
		}
		
		var date = form.down('#date');
		
		if(date && date.value){
			if(condition){
				condition += " and ";
			}
			condition += "cq_indate " + date.value;
		}
		
		var grid = Ext.getCmp('seller'),win = Ext.getCmp('win');
		grid.store.removeAll();
		if(win){
			win.close();
		}
		var container = form.down('#amount');
		Ext.Array.each(container.items.items, function(item){
			item.setValue(null);
		});
		if(!condition){
			condition = "1=1";
		}
		
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		async: false,
	   		params: {
	   			caller: "SELLER_QUOTA_VIEW",
	   			fields: "sum(nvl(cq_quota,0)) cq_quota,sum(nvl(cq_dueamount,0)) cq_dueamount,sum(nvl(cq_spamount,0)) cq_spamount",
	   			condition: condition + groupby
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			var data = new Ext.decode(res.data);
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);
	   				return;
	   			}
				if(res.success&&data.length>0){
					Ext.Array.each(container.items.items, function(item){
						item.setValue(data[0][item.name]);
					});
	   			}
	   		}
		});
		condition += ' order by cq_indate desc';
		var gridParam = {caller: grid.caller, condition: condition, _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid, gridParam);//从后台拿到gridpanel的配置及数据
		
	},
	createWin: function(condition){
		
		var me = this,win = Ext.getCmp('win');
		
		if(!win){
			win = Ext.create('Ext.window.Window',{
				title:'买方客户',
				id:'win',
				width: '80%',
				height:'85%',
				closeAction : 'hide',
				layout: 'fit',
				items:[{
					id: 'buyer',
					xtype : 'erpGridPanel2',
					caller: 'Quota!Buyer',
					condition: condition,
					plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					bbar: {
						xtype: 'erpToolbar',
						id:'toolbar1',
						enableAdd : false,
						enableDelete : false,
						enableCopy : false,
						enablePaste : false,
						enableUp : false,
						enableDown : false
					}
				}]
			}); 
		}else{
			var grid = win.down('erpGridPanel2');
			var gridParam = {caller: grid.caller, condition: condition, _config: getUrlParam('_config')};
			me.GridUtil.loadNewStore(grid, gridParam);//从后台拿到gridpanel的配置及数据
		}
		win.show();
	}
})