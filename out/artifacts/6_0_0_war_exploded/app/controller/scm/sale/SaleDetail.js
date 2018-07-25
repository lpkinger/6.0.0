Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SaleDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.SaleDetail','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Close','core.button.Update',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					this.FormUtil.beforeSave(this);
				}
			},
			'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				Date.prototype.Format = function(fmt){ //author: meizz   
    					var o = {   
						    "M+" : this.getMonth()+1,                 //月份   
						    "d+" : this.getDate(),                    //日   
						    "h+" : this.getHours(),                   //小时   
						    "m+" : this.getMinutes(),                 //分   
						    "s+" : this.getSeconds(),                 //秒   
						    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
						    "S"  : this.getMilliseconds()             //毫秒   
    					};   
					    if(/(y+)/.test(fmt))   
						    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
						  for(var k in o)   
						    if(new RegExp("("+ k +")").test(fmt))   
						    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
						    return fmt;   
    				}; 
    				var allnum =0;
    				var StringMes = null;
    				Ext.each(items, function(item){
    					if(item.data['sdd_delivery'] !=null&&item.data['sdd_delivery'] !=''){
    						allnum+=item.data['sdd_qty'];
    						var oldtime = item.data['sdd_delivery'].Format("yyyy-MM-dd");
    						var now=new Date();
    						var nowtime = now.Format("yyyy-MM-dd");
    					    if(nowtime>oldtime){
    					    	StringMes = "序号为："+item.data['sdd_detno']+"的交期小于当前时间，请重新填写!";
    					    }
    					}
    				});
    				if(StringMes !=null){
    					showError(StringMes);
    					return;
    				}
					if(allnum !=(Ext.getCmp('sd_qty').value-Ext.getCmp('sd_sendqty').value)){
						showError("数量不对，请重新填写!");
				    	return;
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});