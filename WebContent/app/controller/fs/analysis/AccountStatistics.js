Ext.QuickTips.init();
Ext.define('erp.controller.fs.analysis.AccountStatistics', {
	extend : 'Ext.app.Controller',
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : ['fs.analysis.StatisticsForm', 'core.grid.Panel2','fs.analysis.AccountStatistics', 'core.form.ConDateField','core.trigger.DbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpStatisticsForm #query' : {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt;
					me.onQuery(form);
				}
			},
			'#unclose': {
				afterrender : function(grid){
					var form = grid.ownerCt.down('erpStatisticsForm');
					me.onQuery(form);
				}
			}
		})
	},
	onQuery: function(form){
		var me = this, condition ="aa_statuscode = 'AUDITED'";
		var custcode = form.down('#custcode'),groupby = "";
		if(custcode && custcode.value){
			condition += " and aa_custcode = '" + custcode.value + "'";
			groupby = " group by aa_custcode";
		}
		
		var date = form.down('#date');
		
		if(date && date.value){
			condition += " and aa_indate " + date.value;
		}
		
		var grid = Ext.getCmp('close'),grid1 = Ext.getCmp('unclose');
		var container = form.down('#amount');
		Ext.Array.each(container.items.items, function(item){
			item.setValue(null);
		});
		
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		async: false,
	   		params: {
	   			caller: "AccountApply",
	   			fields: "sum(nvl(aa_transferamount,0)) aa_transferamount,sum(nvl(aa_dueamount,0)) aa_dueamount,sum(nvl(aa_leftamount,0)) aa_leftamount",
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
		
		var gridParam = {caller: grid.caller, condition: condition + " and nvl(aa_iscloseoff,'否')='是' order by aa_indate desc", _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid, gridParam);//从后台拿到gridpanel的配置及数据
		
		var gridParam1 = {caller: grid1.caller, condition: condition + " and nvl(aa_iscloseoff,'否')='否' order by aa_indate desc", _config: getUrlParam('_config')};
		me.GridUtil.loadNewStore(grid1, gridParam1);//从后台拿到gridpanel的配置及数据
		
	}
})