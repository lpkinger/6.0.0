Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.PurchaseList', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:['vendbarcode.purchaseList.Viewport','vendbarcode.purchaseList.PurchaseListGridPanel','vendbarcode.vendpurchase.VendToolbar'],
	init:function(){
			this.BaseUtil = Ext.create('erp.util.BaseUtil');
	 	    this.FormUtil = Ext.create('erp.util.FormUtil');
	 	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    	this.control({ 
    		'erpPurchaseListGrid': {
			   	itemclick: this.onGridItemClick
		   	}
    	});
    },
    onGridItemClick: function(selModel, record){
    	var me = this;
    	var id = record.data['PU_ID'];
    	var code = record.data['PU_CODE'];
    	var formCondition = "pu_idIS" + id ;
        var gridCondition = "pd_puidIS" + id +"";
        var caller = getUrlParam('whoami');
		caller = caller.replace(/'/g, "");
        var linkCaller = caller;
    	me.FormUtil.onAdd('purchase'+id, '采购单('+code+')', 'jsps/vendbarcode/vendpurchase.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&formCondition=' + formCondition + '&gridCondition=' + gridCondition);
    }
    
});