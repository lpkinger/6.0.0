Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.CustCreditQuery', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['fs.credit.CustCreditForm', 'fs.credit.CustCreditQuery', 'fs.credit.CustCreditGrid','core.trigger.DbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpCustCreditFormPanel #query' : {
				click : function(btn) {
					me.onQuery();
				}
			},
			'erpCustCreditFormPanel #export' : {
				click : function(btn) {
					var grid = Ext.getCmp('custCreditGrid');
					var count = grid.store.getCount();
					if(count==0){
						grid.store.loadData([{}]);
					}
    				me.BaseUtil.exportGrid(grid,'客户信用评级');
    				if(count==0){
						grid.store.removeAll();
					}
				}
			},
			'erpCustCreditGridPanel':{
				afterrender : function(){
					me.onQuery();
				},
				itemclick:function(view,record){
					var id = record.data['CRA_ID'];
					var cuvename = record.data['CRA_CUVENAME'];
					var yearmonth = record.data['CRA_YEARMONTH'];
					me.FormUtil.onAdd('CustCreditTargets'+id, '客户'+cuvename+yearmonth+'年财务报表', 'jsps/fs/credit/custCreditTargets.jsp?whoami=CustCreditTargets&gridCondition=cct_craidIS'+id);
				}
			}
		})
	},
	onQuery: function(){
		var condition ='';
		var cuvename = Ext.getCmp('cra_cuvename');
		if(cuvename && cuvename.value){
			if(condition!=''){
				condition += ' and ';
			}
			condition += "cra_cuvename = '"+cuvename.value+"'";
		} else {
			condition = " 1=1";
		}
		if(condition==''){
			var grid = Ext.getCmp('custCreditGrid');
			grid.store.removeAll();
			return;
		}
		condition += ' order by cra_cuvename,cra_yearmonth,cra_date desc';
		
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		async: false,
	   		params: {
	   			caller: "CustCreditRatingApply",
	   			fields: "cra_id,cra_cuvecode,cra_cuvename,cra_yearmonth,cra_sysscore,cra_sysdate,cra_manscore,cra_mandate,cra_manstatus,cra_finalstatus",
	   			condition: condition
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);
	   				return;
	   			}
				if(res.success){
					var grid = Ext.getCmp('custCreditGrid');
					var data = Ext.decode(res.data);
					if(data.length>0){
						grid.store.loadData(data);	
					}
	   			}
	   		}
		});
	}
})