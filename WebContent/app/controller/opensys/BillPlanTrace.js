Ext.QuickTips.init();
Ext.define('erp.controller.opensys.BillPlanTrace', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'opensys.billPlanTrace.BillPlanTrace','common.query.Form','opensys.billPlanTrace.BillPlanTraceGrid','core.form.YnField',
    		'core.trigger.DbfindTrigger','core.grid.YnColumn','opensys.billPlanTrace.ProdInOutMakeInGrid','opensys.billPlanTrace.OrderProcessChart'
    	],
    init:function(){
    	this.control({
    		'BillPlanTraceGrid':{
 			   itemmousedown: function(selModel, record){ 
    					if(record.data['isleaf'] == '0' && record.childNodes.length == 0 ){
    						Ext.getCmp('querygrid').loadChildNodes(record);
    					}
    			}
 		     },
 		     'field[id=sa_pocode]':{
 		     	change:function(f){
	 		     		var code = Ext.getCmp('sa_pocode')?Ext.getCmp('sa_pocode').value:'';
	 		     		if(code!=''){
	 		     			Ext.Ajax.request({
								url: basePath + 'common/VisitERP/orderProcess.action',
								params: {
									purchaseCode: code
								},
								async:false,
								callback: function(options, success, response){
									var res = Ext.decode(response.responseText);
									var me = Ext.getCmp('OrderProcessChart');
									me.view.store.removeAll();
									me.view.store.add(res);
								}
							});
	 		     		}
 		     	}
 		     }
    	});
    }
});