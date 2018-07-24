Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.AcceptNotifyList', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:['vendbarcode.AcceptNotifyList.Viewport','vendbarcode.AcceptNotifyList.AcceptNotifyListGridPanel','vendbarcode.vendpurchase.VendToolbar'],
	init:function(){
			this.BaseUtil = Ext.create('erp.util.BaseUtil');
	 	    this.FormUtil = Ext.create('erp.util.FormUtil');
	 	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    	this.control({
	    		'acceptNotifyListGrid': {
				   	itemclick: this.onGridItemClick
			   	}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
    	var id = record.data['AN_ID'];
    	var code = record.data['AN_CODE'];
    	var formCondition = "an_idIS" + id ;
        var gridCondition = "and_anidIS" + id +"";
        var caller = getUrlParam('whoami');
		caller = caller.replace(/'/g, "");
        var linkCaller = caller;
    	me.FormUtil.onAdd('purchase'+id, '送货通知单('+code+')', 'jsps/vendbarcode/vendAcceptNotify.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&formCondition=' + formCondition + '&gridCondition=' + gridCondition);
   	},
});